package org.iota.ict.ixi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.iota.ict.IctInterface;
import org.iota.ict.Main;
import org.iota.ict.api.GithubGateway;
import org.iota.ict.ixi.context.IxiContext;
import org.iota.ict.utils.Constants;
import org.iota.ict.utils.IOHelper;
import org.iota.ict.utils.RestartableThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class IxiModuleHolder extends RestartableThread {

    public static final Logger LOGGER = LogManager.getLogger("IxiMH");
    protected static final File MODULE_DIRECTORY = new File("modules/");
    protected static final File MODULE_MANAGEMENT = new File(MODULE_DIRECTORY, "management.json");
    protected static JSONObject moduleManagementJSON;

    protected final IctInterface ict;
    protected Map<String, IxiModule> modulesByPath = new HashMap<>();
    protected Map<IxiModule, IxiModuleInfo> modulesWithInfo = new HashMap<>();

    static {
        if (!MODULE_DIRECTORY.exists())
            MODULE_DIRECTORY.mkdirs();
        if(!MODULE_MANAGEMENT.exists()) {
            moduleManagementJSON = new JSONObject().put("modules", new JSONObject());
            storeModuleManagement();
        }
        loadModuleManagement();
    }

    public IxiModuleHolder(IctInterface ict) {
        super(LOGGER);
        this.ict = ict;
    }

    private static void loadModuleManagement() {
        try {
            moduleManagementJSON = new JSONObject(IOHelper.readFile(MODULE_MANAGEMENT));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void storeModuleManagement() {
        try {
            IOHelper.writeToFile(MODULE_MANAGEMENT, moduleManagementJSON.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JSONObject getModuleFromModuleManagement(String path) {
        JSONObject modules = moduleManagementJSON.getJSONObject("modules");
        if(modules.has(path)) return modules.getJSONObject(path);
        return new JSONObject().put("installed", false);
    }

    private static void setModuleFromModuleManagement(String path, JSONObject jsonObject) {
        moduleManagementJSON.getJSONObject("modules").put(path, jsonObject);
        storeModuleManagement();
    }

    private static void putModulePropertyInModuleManagement(String path, String propertyName, Object propertyValue) {
        setModuleFromModuleManagement(path, getModuleFromModuleManagement(path).put(propertyName, propertyValue));
    }

    @Override
    public void run() { ; }

    public void update(String path, String version) throws Throwable {
        IxiModule module = modulesByPath.get(path);
        IxiModuleInfo info = modulesWithInfo.get(module);
        URL urlOfNewVersion = GithubGateway.getAssetDownloadUrl(info.repository, version);
        uninstall(path);
        IxiModule newModule = install(urlOfNewVersion);
        newModule.start();
    }

    public void uninstall(String path) {

        IxiModule module = modulesByPath.get(path);
        LOGGER.info("Uninstalling module " + path + " ...");

        if (module == null)
            throw new RuntimeException("No module '" + path + "' installed.");

        module.uninstall();

        IxiModuleInfo info = modulesWithInfo.get(module);

        modulesWithInfo.remove(module);
        modulesByPath.remove(path);

        try {
            module.terminate();
        } catch (IllegalStateException e) {
            LOGGER.warn("IllegalStateException while trying to terminate module '" + path+"'", e);
        }

        File jar = new File(MODULE_DIRECTORY, path);
        File guiDirectory = new File(Constants.WEB_GUI_PATH, "modules/" + info.name + "/");

        if (!jar.exists())
            throw new RuntimeException("Could not find file '" + path + "'.");
        if (jar.isDirectory())
            throw new RuntimeException("'" + path + "' is a directory.");
        if (!path.endsWith(".jar"))
            throw new RuntimeException("'" + path + "' is not a .jar file.");

        Throwable jarDeletedSuccess = IOHelper.deleteRecursively(jar);
        Throwable guiDirectoryDeletedSuccess = guiDirectory.exists() ? IOHelper.deleteRecursively(guiDirectory) : null;

        if(jarDeletedSuccess != null)
            throw new RuntimeException("Could not delete jar file: " + jar.getAbsolutePath() + " (note that Windows does not allow deletion of loaded jar files, please delete manually after Ict terminated)", jarDeletedSuccess);
        if(guiDirectoryDeletedSuccess != null)
            throw new RuntimeException("Could not delete module's GUI directory" + guiDirectory, guiDirectoryDeletedSuccess);

        putModulePropertyInModuleManagement(path, "installed", false);
    }

    public IxiModule install(URL url) throws Throwable {

        String split[] = url.getFile().split("/");
        String fileName = split[split.length - 1];
        Path target = Paths.get("./modules/" + fileName);

        LOGGER.info("Downloading " + target.toString() + " ...");
        try (InputStream in = url.openStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        LOGGER.info("Download of " + target.toString() + " complete. Installing ...");
        IxiModule module = initModuleFromJar(target);
        LOGGER.info("Installation of " + target.toString() + " complete.");

        putModulePropertyInModuleManagement(getInfo(module).path, "installed", true);

        return module;
    }

    public void initAllModules() {
        File[] files = MODULE_DIRECTORY.listFiles();
        try {
            if (files == null)
                throw new NullPointerException("failed listing files in directory " + MODULE_DIRECTORY.getAbsolutePath());
        } catch (NullPointerException e) {
            return;
        }
        modulesWithInfo = new HashMap<>();
        initModulesFromFiles(files);
    }

    public void startAllModules() {
        for (IxiModule module : modulesWithInfo.keySet())
            if (!module.isRunning())
                module.start();
    }

    private void initModulesFromFiles(File[] files) {
        for (File file : files) {
            Path jar = Paths.get(file.toURI());
            if (!jar.toString().endsWith(".jar"))
                continue;
            try {
                initModuleFromJar(jar);
            } catch (Throwable t) {
                LOGGER.error("could not load module " + jar, t);
            }
        }
    }

    private IxiModule initModuleFromJar(Path jar) throws Exception {
        LOGGER.info("loading IXI module " + jar.getFileName() + "...");
        String path = MODULE_DIRECTORY.toURI().relativize(jar.toUri()).toString();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jar.toFile().toURI().toURL()}, Main.class.getClassLoader());
        IxiModuleInfo info = readModuleInfoFromJar(classLoader, path);
        if (!info.supportsCurrentVersion())
            LOGGER.warn("IXI module '" + info.name + "' does not specify your Ict's version '" + Constants.ICT_VERSION + "' as supported in module.json.");

        Class ixiClass = getIxiClass(classLoader, info.mainClass);
        return initModule(ixiClass, info);
    }

    public IxiModule loadVirtualModule(Class moduleClass, String path) throws Exception {
        JSONObject infoJSON = new JSONObject()
                    .put("version", "1.0")
                    .put("repository", "virtual/module")
                    .put("description", "A virtual module.")
                    .put("gui_port", "-1")
                    .put("name", path)
                    .put("main_class", moduleClass.toString())
                    .put("supported_versions", new JSONArray().put(Constants.ICT_VERSION));
        IxiModuleInfo info = new IxiModuleInfo(infoJSON, path);
        return initModule(moduleClass, info);
    }

    private IxiModule initModule(Class moduleClass, IxiModuleInfo info) throws Exception {
        Constructor<?> c = moduleClass.getConstructor(Ixi.class);
        IxiModule module =  (IxiModule) c.newInstance(ict);
        modulesWithInfo.put(module, info);
        modulesByPath.put(info.path, module);

        if(!getModuleFromModuleManagement(info.path).getBoolean("installed"))
            module.install();

        subWorkers.add(module);

        try {
            File configFile = new File(MODULE_DIRECTORY, info.path+".cfg");
            if(configFile.exists()) {
                JSONObject config = new JSONObject(IOHelper.readFile(configFile));
                module.getContext().tryToUpdateConfiguration(config);
            }
        } catch (Throwable t) {
            LOGGER.warn("Failed to read configuration of module '"+info.path+"'.");
        }
        return module;
    }

    private static Class getIxiClass(URLClassLoader classLoader, String mainClassName) {
        try {
            return Class.forName(mainClassName, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid IXI module: Could not find class '" + mainClassName + "'.");
        }
    }

    private static IxiModuleInfo readModuleInfoFromJar(URLClassLoader classLoader, String path) {
        try {
            InputStream is = classLoader.getResourceAsStream("module.json");
            if (is == null)
                throw new RuntimeException("Could not find 'module.json'");
            String str = IOHelper.readInputStream(is);
            JSONObject json = new JSONObject(str);
            return new IxiModuleInfo(json, path);
        } catch (IOException e) {
            throw new RuntimeException("Failed reading 'module.json' file: " + e.getMessage());
        } catch (JSONException e) {
            throw new RuntimeException("Failed parsing 'module.json' file: " + e.getMessage());
        }
    }

    @Override
    public void onTerminate() {
        for(String path : modulesByPath.keySet()) {
            storeModuleConfiguration(path);
        }
    }

    public void storeModuleConfiguration(String path) {
        IxiModule module = modulesByPath.get(path);
        IxiContext context = module.getContext();
        JSONObject configuration = context.getConfiguration();
        if(configuration != null)
            try {
                IOHelper.writeToFile(new File(MODULE_DIRECTORY, path+".cfg"), configuration.toString());
            } catch (IOException e) {
                LOGGER.warn("Failed to store configuration of module '"+path+"'.", e);
            }
    }

    public Set<IxiModule> getModules() {
        return new HashSet<>(modulesWithInfo.keySet());
    }

    public IxiModule getModule(String path) {
        return modulesByPath.get(path);
    }

    public IxiModuleInfo getInfo(IxiModule module) {
        return modulesWithInfo.get(module);
    }
}
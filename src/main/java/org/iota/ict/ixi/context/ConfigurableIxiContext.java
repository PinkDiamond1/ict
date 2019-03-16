package org.iota.ict.ixi.context;

import org.json.JSONObject;

public abstract class ConfigurableIxiContext implements IxiContext {

    protected JSONObject configuration;
    private final JSONObject defaultConfiguration;

    @Override
    public String respondToRequest(String request) {
        return "This module has not implemented a response mechanism.";
    }

    protected ConfigurableIxiContext(JSONObject defaultConfiguration) {
        this.defaultConfiguration = defaultConfiguration;
        this.configuration = defaultConfiguration;
    }

    @Override
    public void tryToUpdateConfiguration(JSONObject newConfiguration) {
        validateConfiguration(newConfiguration);
        this.configuration = newConfiguration;
        applyConfiguration();
    }

    protected abstract void validateConfiguration(JSONObject newConfiguration);

    protected abstract void applyConfiguration();

    @Override
    public JSONObject getDefaultConfiguration() {
        return cloneJSONObject(defaultConfiguration);
    }

    @Override
    public JSONObject getConfiguration() {
        return cloneJSONObject(configuration);
    }

    private JSONObject cloneJSONObject(JSONObject jsonObject) {
        return new JSONObject(jsonObject, JSONObject.getNames(jsonObject));
    }
}

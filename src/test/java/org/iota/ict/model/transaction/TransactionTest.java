package org.iota.ict.model.transaction;

import org.iota.ict.utils.Trytes;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class TransactionTest {
    private static final String TRYTES_INVALID_FLAGS = "9EGQSWYCJHRLJYEGZLRYQAZPLVRAYIWGWJUMFFX99UZUKBQNFYAOQLOFARIKNEBKDRHJJWDJARXTNPHPAODJRSGJBVVYBVJHZALJWDCJHZRSACOVCVVAVHZVTPFTAJWVGFSVLSYXHNNXEGSMJHDBZKGFQNYJJJBAPDHFFGZ9POSOMWTDPGXI9KQRLMUVWNEQDANMXROVORJVALWVGDDJAFOOBXUKVCCIVXSSHZUCZV9XVBASLWX9NXPWGMGYCRD9ILQMKIGPBGGMKAIJKNALBLABATYFVIRBKTXTWNUZAUXRASB9EEIQHWBD9ZYUDBUPBSWXVYXQXECRCHQAYH9ZBUZBASPOIGBSGWJYFKFRITUBVMCYGCMAPTXOIWEVTUXSUOUPTUQOPMMPUTHXMOP9CW9THAZXEPMOMNEOBLUBPOAIOBEBERRZCIKHSTDWUSUPUWNJOCLNZDCEKWWAAJDPJXJEHHSYFN9MH9BGUDQ9CSZBIHRC9PSQJPGKH9ILZDWUWLEKWFKUFFFIMOQKRMKOYXEJHXLCEGCGGKHGJUHOXINSWCKRNMUNAJDCVLZGEBII9ASTYFTDYDZIZSNHIWHSQ9HODQMVNDKMKHCFDXIIGDIVJSBOOE9GRIXCD9ZUTWCUDKFTETSYSRBQABXCXZFOWQMQFXHYZWD9JZXUWHILMRNWXSGUMIIXZYCTWWHCWMSSTCNSQXQXMQPTM9MOQMIVDYNNARDCVNQEDTBKWOIOSKPKPOZHJGJJGNYWQWUWAZMBZJ9XEJMRVRYFQPJ9NOIIXEGIKMMN9DXYQUILRSCSJDIDN9DCTFGQIYWROZQIEQTKMRVLGGDGA9UVZPNRGSVTZYAPMWFUWDEUULSEEGAGITPJQ9DBEYEN9NVJPUWZTOTJHEQIXAPDOICBNNCJVDNM9YRNXMMPCOYHJDUFNCYTZGRCBZKOLHHUK9VOZWHEYQND9WUHDNGFTAS99MRCAU9QOYVUZKTIBDNAAPNEZBQPIRUFUMAWVTCXSXQQIYQPRFDUXCLJNMEIKVAINVCCZROEWEX9XVRM9IHLHQCKC9VLK9ZZWFBJUZKGJCSOPQPFVVAUDLKFJIJKMLZXFBMXLMWRSNDXRMMDLE9VBPUZB9SVLTMHA9DDDANOKIPY9ULDWAKOUDFEDHZDKMU9VMHUSFG9HRGZAZULEJJTEH9SLQDOMZTLVMBCXVNQPNKXRLBOUCCSBZRJCZIUFTFBKFVLKRBPDKLRLZSMMIQNMOZYFBGQFKUJYIJULGMVNFYJWPKPTSMYUHSUEXIPPPPPJTMDQLFFSFJFEPNUBDEDDBPGAOEJGQTHIWISLRDAABO9H9CSIAXPPJYCRFRCIH9TVBZKTCK9SPQZUYMUOKMZYOMPRHRGF9UAKZTZZG9VVVTIHMSNDREUOUOSLKUHTNFXTNSJVPVWCQXUDIMJIAMBPXUGBNDTBYPKYQYJJCDJSCTTWHOJKORLHGKRJMDCMRHSXHHMQBFJWZWHNUHZLYOAFQTRZFXDBYASYKWEVHKYDTJIAUKNCCEPSW9RITZXBOFKBAQOWHKTALQSCHARLUUGXISDMBVEUKOVXTKTEVKLGYVYHPNYWKNLCVETWIHHVTBWT9UPMTQWBZPRPRSISUBIBECVDNIZQULAGLONGVFLVZPBMHJND9CEVIXSYGFZAGGN9MQYOAKMENSEOGCUNKEJTDLEDCD9LGKYANHMZFSSDDZJKTKUJSFL9GYFDICTPJEPDSBXDQTARJQEWUVWDWSQPKIHPJONKHESSQH9FNQEO9WUCFDWPPPTIQPWCVDYTTWPLCJJVYNKE9ZEJNQBEJBMDBLNJKQDOQOHVS9VY9UPSU9KZVDFOESHNRRWBK9EZCYALAUYFGPCEWJQDXFENSNQEAUWDXJGOMCLQUQWMCPHOBZZ9SZJ9KZXSHDLPHPNYMVUJQSQETTN9SG9SIANJHWUYQXZXAJLYHCZYRGITZYQLAAYDVQVNKCDIYWAYBAFBMAYEAEAGMTJGJRSNHBHCEVIQRXEFVWJWOPU9FPDOWIFL9EWGHICRBNRITJDZNYACOGTUDBZYIYZZWAOCDBQFFNTTSTGKECWTVWZSPHX9HNRUYEAEWXENEIDLVVFMZFVPUNHMQPAIOKVIBDIHQIHFGRJOHHONPLGBSJUD9HHDTQQUZN9NVJYOAUMXMMOCNUFLZ9BAJSZMDMPQHPWSFVWOJQDPHV9DYSQPIBL9LYZHQKKOVF9TFVTTXQEUWFQSLGLVTGK99VSUEDXIBIWCQHDQQSQLDHZ9999999999999999999TRINITY99999999999999999999TNXSQ9D99A99999999B99999999MXKZAGDGKVADXOVCAXEQYZGOGQKDLKIUPYXIL9PXYBQXGYDEGNXTFURSWQYLJDFKEV9VVBBQLTLHIBTFYOGBHPUUHS9CKWSAPIMDIRNSUJ9CFPGKTUFAGQYVMFKOZSVAHIFJXWCFBZLICUWF9GNDZWCOWDUIIZ9999OXNRVXLBKJXEZMVABR9UQBVSTBDFSAJVRRNFEJRL9UFTOFPJHQMQKAJHDBIQAETS9OUVTQ9DSPAOZ9999TRINITY99999999999999999999LPZYMWQME999999999MMMMMMMMMDTIZE9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999";
    private static final String TRYTES_VALID_FLAGS = "EEQGWZJRO9YOG9UEYOLF9ZPDQXKZZKXNMXGWNGN9ALBGOYTQFCWIITBVFZFZJT9XWVMMDL9OSITYHUZUYALMGSDYJLMLGBTLUITIOYHYEPNFMQLZQDONICAWBXFMPKJVCE9PHUQVXIZHGOIELYYYBIEGFGOPLXJV9ZJOKREIMXYSVIVRFMLYJCQYVDFHHQMJRAMTVVXVGSHFEHMFD99HDQZJJTTAWSNSZLJABMYJZJMRGKSHYUYWBQRFFDSWJGLVPUOUQBSPIHMZDUPGIYZCOPDSNBGFWQWTFQYKVUGSYSNLZFICGCFQHBXWRWSMI9KIEB9WWSYENHX9HREGHROAJUNWFPMNSSB99UARJJZUMUMRJMMILBNSZFFYYFJ9SWSITERRLUIOJALNQ9BZXZQYDHUURTXRKCIAQSMHUCHECLYRPWXAMTHVLMKGWQPOMQPPTCAKUBJCYBJJWWEAMUHWWTXNLLIVPXEBVBTEHWR9RBCJMRSPXAHTHJTKMAMYKSUVUZKRZMSMROYALGOIRGHXERYVKIVUGECXBBTKVWT9VZWVEHIRIRVGPCAHFDRPTYMTAMXKJKW9P9LCYSZDD9XYPCEIJERKVCPOWPGIKEISCLIFCTUMJUXFJMFYS9LPCJRHQTTSXCNXXYMYOGSGPVUOPXRLPDMLFQOXRKZYOOCNAFAQMSYVSYUZXJIYHOVIBGNIXOUCCPKMDAMY9F9EPDLSREQMWUZAQIIRU9STCZFUTLSFBWNPVW9YCVCNTUHIYICZSBZTLUTQCZCYXCYQIBHHRJJHIBJAZCAYNWRJJCXB9WYABTWWHEMKRTW9AWWFLOSXNEWSJCKZECWVPBUGGJQFBPKBI9JAS9XJRGGLEYFPEWACJKY99AUMIWJPJXXXBCKQSLBNIGSAQQUANPBKMKLG9ZIKKOAHGSOACOURRRBTBMF99QE9AJAE9IMLVZQJMTNT9HWRHIRFHS9DCKFPWWODXNEWLHXYQLLNYRNEJLAFPUXEZSTNTUVDUPMEIATNFKGRMSVUZZLCDJYABNHLJMKBIXTHVCEQZHTQVHEQPFWROPO9XOSTULVEGSDUABLASMWUSOINWRCRXETLLDUZLRQFYREF9CVRXPMSMQRLKGPSOWNKSWPAQACEEGJSHACGIXVSSGHOZCHPPXJKTJUUKWKJPHKBGYRCE9BIJTAMBODPFJJDASTWMKUBJWWPQWZQJIAKPUNUHC9WLBJQGVQKIONE9OSLNCIG9UMRRMAJXFTPJRDWQPSENLEEOYUGONAXG9JBWXQWEAZOQBADEQHDUSAYQELYWETOCULMEMJJDIHOEJZXFCYIVAIQRZZTHHCGQFWYYEDHWOYBEEISCUURGHAIKA9AC9CRDEBUXPGZPGUFECVAEMEHBBOJUKQOJHSBZVIUXUFUIFZQBBAJDMFUVCDCIUNBABODAQSGAFLYZY9NYIKLHOMGZWSZEPTNAUIEJSMOWLVKETYR9TS9ZVXKANZQOOSPLCRVUECEFWDVCOABUSZRDALK9YAXKHXRCA9XNTOWVTXXVGCUVFMJARXB9OJ9WOHDTEHOTMPQBOWDEOBDMMC9SWRDZGRBKWHODBHZOVUNMVKSNUISOMKSGMKONY99PLZQQKECMSIZTWTSPVGRADQP9GLJQM9NWNTABAGPJNPXYEVLGJEEQBATRJIWGJAANEBNBZTKUYHMAH9KDHNWI9YGNHGNINEMOAGZYOOTJWFDBTAOYYITO9ETNZWRCMHBVDGUEOMXZTASUEVYGAOHJGAYLQWBAJLX99G9SNTEJZNEULMURKD9UNKJDZPOPUMFWUJCOOWDAOWQDXFIMPSDXPOUNSI9XG9JHXS9OFBIIPMHUUGRBPXZOOJXRAE9ALRXP9VDJYVEHRJQSUPTSGAAMKROAVDQ9OJ9WQORFZAIUGUBWQWBZOXNW9ZZFRWNQIBHKFEB9YBPYHASOONPMKWLTIMTZRGIHVWTHXRNIJXZMTMMOZSUWXACUMPFFLCDMAPBLOS9WAGTMFBGIOIZBA9GWVCLKNHJCNNNPNFWLGNTDROYUYLGMCBJQOECPLMHBTVIAJJU9HMFKFMIINWCRRWRARWKYKMAAYBCQKVDQDYDCETQWWBSPZPMJEMINMRPXAMYUDWAJDRECGNKNJICWXI9PTGEGMXOTJDBJZPZOZZQQLNBGKPCJSGHPWACP99VRBP9BHFTRFRKHIXECLBB9JXIBBHZHWHHSZPKCTFNCIOECOLKPXXVPKLVLWLRLDU9GOTKBLBD9EJWYAJYBXFHOZAXOWSVSJYWIASNRWRLKPFNWJDCYRGXNGCHPNZQG9VPQYICMLNBWJVTYJUKIHXGJTEZTAFVBY99999999999999999999999999999999999999999999999999999TI9JWESMEA99999999B99999999TAUBLCQSOXIECTMIXACUDFNTFIMBCMDBZCOPFHDQPCMZTMYLMAHPMEPVNJWZWQSXPT9VGWZEHFTG99999YFXIIDDFGRMMYGFVVJQCMUAQPSFDHINAWCPMPKFQIBKLLIRVVWGXUVG9KBQFTHKWODRIQLVEJCPPA9999999999999999999999999999999WXAJWESME999999999MMMMMMMMMPOWSRVIO9KD9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999";
    private static final String HASH_OF_TRYTES_VALID_FLAGS = "KDUG9GTAHUMMLROKRRSOQSCSPCVB99THBZBFXQIWXZYDJSBAUHS9BNWSFEKSVQEIUPAOKZCQQNDFHAOEV"; //"NFWYEUEPDKJLDKLMXQAABMFDWANBCCRSNMWPRHQVDLRPF9YORK9YWRQ9XBPTWYWSFKFLZGNKKPYLMUOOT"; <-- 123 rounds

    @Test(expected = Transaction.InvalidTransactionFlagException.class)
    public void testInvalidFlags() {
        new Transaction(Trytes.toBytes(TRYTES_INVALID_FLAGS));
    }

    @Test
    public void testTransactionDecoding() {
        Transaction transaction = new Transaction(Trytes.toBytes(TRYTES_VALID_FLAGS));
        Assert.assertEquals("transaction address", TRYTES_VALID_FLAGS.substring(Transaction.Field.ADDRESS.tryteOffset, Transaction.Field.ADDRESS.tryteOffset + 81), transaction.address());
        Assert.assertEquals("value", BigInteger.valueOf(-2), transaction.value);
        Assert.assertEquals("timestamp", 1545102726377L, transaction.attachmentTimestamp);
    }

    @Test
    public void testTransactionBuilder() {
        TransactionBuilder builder = new TransactionBuilder();
        builder.address = Trytes.randomSequenceOfLength(81);
        Transaction transaction = builder.build();
        Assert.assertEquals(transaction.address(), builder.address);
    }

    @Test
    public void testTransactionBuilderPadRightTAG() {
        TransactionBuilder builder = new TransactionBuilder();
        builder.tag = "UNICORN9FOR9PRESIDENT";
        Transaction transaction = builder.build();
        Assert.assertEquals(transaction.address(), builder.address);
        Assert.assertEquals(transaction.tag(), "UNICORN9FOR9PRESIDENT999999");
    }

    @Test
    public void testTransactionEncodingDecoding() {
        Transaction original = new Transaction(Trytes.toBytes(TRYTES_VALID_FLAGS));
        Transaction copy = new Transaction(Trytes.toBytes(original.decodeBytesToTrytes()+Trytes.padRight("", 81)));
        Assert.assertEquals(original.address(), copy.address());
        Assert.assertEquals(original.decodeBytesToTrytes(), copy.decodeBytesToTrytes());
    }

    @Test
    public void testCurl123() {
        Transaction transaction = new Transaction(Trytes.toBytes(TRYTES_VALID_FLAGS));
        Assert.assertEquals(HASH_OF_TRYTES_VALID_FLAGS, transaction.hash);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonTryteCharacter() {
        TransactionBuilder builder = new TransactionBuilder();
        builder.address = builder.address.substring(0, Transaction.Field.ADDRESS.tryteLength - 1) + 'z';
        builder.build();
    }
}
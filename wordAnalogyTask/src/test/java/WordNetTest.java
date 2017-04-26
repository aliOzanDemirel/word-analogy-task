import edu.mit.jwi.data.ILoadPolicy;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.file.FileActions;
import wat.wordnet.WordNetUtil;

public class WordNetTest {

    private static final Logger log = LoggerFactory.getLogger(WordNetTest.class);
    private WordNetUtil wordNetUtil = null;

    @BeforeClass
    public static void beforeClass() {

        log.info("WordNetTest class is initialized.");
    }

    @Before
    public void beforeTestMethods() throws Exception {

        final String wordNetDictHome = System.getenv("WORDNET_PATH");
        if (wordNetDictHome == null || !FileActions.isPathValid(wordNetDictHome)) {
            throw new Exception("WORDNET_PATH does not have a valid path, " +
                    "thus WordNet could not be found!");
        }
        wordNetUtil = new WordNetUtil(wordNetDictHome, ILoadPolicy.NO_LOAD);
    }

    @Test
    public void testValidateWord() {

        String wordOnlyNumber = "15";
        String wordWithNumber1 = ".22-caliber";
        String wordWithNumber2 = "155th";
        String firstPhrase = "Y2K_compliant";
        String secondPhrase = "battle_of_Wagram";
        String firstAccepted = "yarn-spinning";
        String secondAccepted = "Ukraine";

        Assert.assertFalse("Words with numbers in it, should not be sent to " +
                "trained model to check for analogy.", wordNetUtil.validateWord(wordOnlyNumber));
        Assert.assertFalse("Words with numbers in it, should not be sent to " +
                "trained model to check for analogy.", wordNetUtil.validateWord(wordWithNumber1));
        Assert.assertFalse("Words with numbers in it, should not be sent to " +
                "trained model to check for analogy.", wordNetUtil.validateWord(wordWithNumber2));
        Assert.assertFalse("Words with '_' in it (phrases), should not be sent to " +
                "trained model to check for analogy.", wordNetUtil.validateWord(firstPhrase));
        Assert.assertFalse("Words with '_' in it (phrases), should not be sent to " +
                "trained model to check for analogy.", wordNetUtil.validateWord(secondPhrase));
        Assert.assertTrue("Word: " + firstAccepted + " should be valid to send to " +
                "trained model to check for analogy.", wordNetUtil.validateWord(firstAccepted));
        Assert.assertTrue("Word: " + secondAccepted + " should be valid to send to " +
                "trained model to check for analogy.", wordNetUtil.validateWord(secondAccepted));
    }

    @Test
    public void testCalculateAnalogyOfOneWord() {



    }

    @AfterClass
    public static void afterClass() {

        log.info("WordNetTest class is finalizing.");
    }

}

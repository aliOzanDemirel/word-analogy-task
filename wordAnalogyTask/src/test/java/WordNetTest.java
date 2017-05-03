import edu.mit.jwi.data.ILoadPolicy;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.exceptions.ModelBuildException;
import wat.file.FileActions;
import wat.helper.Constants;
import wat.training.model.BaseModelInt;
import wat.training.model.word2vec.Word2vecUtil;
import wat.training.model.word2vec.Word2vecUtilInt;
import wat.wordnet.WordNetUtil;

import java.util.Arrays;
import java.util.List;

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

    private BaseModelInt prepareWord2vec() throws ModelBuildException {

        BaseModelInt w2vecModel = new Word2vecUtil();
        w2vecModel.setCorpusPath("/home/ozan/word2vec_saved/2017-04-28_56035_trained_word2vec");
        w2vecModel.createModel(Constants.CORPUS_IS_PRETRAINED);
        return w2vecModel;
    }

    @Test
    public void testCalculateAnalogyOfOneWord() throws ModelBuildException {

        final String another = "bluff";
        final String word = "gangster";
        // gangster -> Derivationally related form -> gang
        // tek bir lexical pointer var (ganster - gang) çifti ile
        // 'Derivationally related form'a sahip 250 kelime kıyaslanacak
        BaseModelInt w2vecModel = this.prepareWord2vec();
        wordNetUtil.calculateAnalogyScoreOfWordInput(w2vecModel, word);
        log.info(wordNetUtil.getCalc().toString());
    }

    @Test
    public void testGetNearestWords() throws ModelBuildException {

        final String word = "gangster";
        BaseModelInt w2vecModel = this.prepareWord2vec();
        List<String> returned = w2vecModel.getNearestWords(word);

        log.info(w2vecModel.getClosestWordSize() + " closest word for " + word);
        returned.forEach(result -> log.info(result));
    }

    @Test
    public void testPrepareMultiplePointersToWordMap() {

        wordNetUtil.prepareMultiplePointersToWordMap();
    }

    @Test
    public void testPreparePointerToWordMap() {

        wordNetUtil.preparePointerToWordMap();
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

    @AfterClass
    public static void afterClass() {

        log.info("WordNetTest class is finalizing.");
    }

}

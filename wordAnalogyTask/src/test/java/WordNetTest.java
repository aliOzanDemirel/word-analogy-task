import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.calculator.Calculator;
import wat.exceptions.ModelBuildException;
import wat.file.FileActions;
import wat.helper.Constants;
import wat.training.model.BaseModelInt;
import wat.training.model.word2vec.Word2vecUtil;
import wat.wordnet.WordNetUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    public static BaseModelInt prepareWord2vec() throws ModelBuildException {

        final BaseModelInt w2vecModel = new Word2vecUtil();
        w2vecModel.setCorpusPath("/home/ozan/word2vec_saved/65mbtrained");
        w2vecModel.createModel(Constants.CORPUS_IS_PRETRAINED);
        return w2vecModel;
    }

    public void saveCalculation(final Calculator calc, final BaseModelInt w2vecModel) {

        final List<String> lines = new ArrayList<String>(3) {{
            add("After calculation: " + calc.toString());
            add("Analogical percentage: " + calc.getAnalogicalPercentage());
            add("Similarity percentage: " + calc.getSimilarityPercentage());
        }};

        try {
            FileActions.writeToFileByCreatingFile(lines,
                    w2vecModel.getName() + "_scores", "score.txt");
        } catch (IOException e) {
            log.error("Scores could not be saved!", e);
        }
    }

    @Test
    public void testCalculateAnalogyWithOnlySynset() throws ModelBuildException {

        final String word = "father";
        final BaseModelInt w2vecModel = WordNetTest.prepareWord2vec();
        wordNetUtil.calculateAnalogyScoreOfWordInput(w2vecModel, word, true);
    }

    @Test
    public void testCalculateAnalogyInStandardWay() throws ModelBuildException {

        final String word = "father";
        final BaseModelInt w2vecModel = WordNetTest.prepareWord2vec();
        wordNetUtil.calculateAnalogyScoreOfWordInput(w2vecModel, word, false);
    }

    @Test
    public void testCalculateAnalogyOfNounPOSWithOnlySynset() throws ModelBuildException {

        final BaseModelInt w2vecModel = WordNetTest.prepareWord2vec();
        wordNetUtil.calculateScoreForPOSFromController(w2vecModel,
                POS.NOUN, Constants.IS_ANALOGY_TEST, true);

        this.saveCalculation(wordNetUtil.getCalc(), w2vecModel);
    }

    @Test
    public void testCalculateAnalogyOfNounPOSInStandardWay() throws ModelBuildException {

        final BaseModelInt w2vecModel = WordNetTest.prepareWord2vec();
        wordNetUtil.calculateScoreForPOSFromController(w2vecModel,
                POS.NOUN, Constants.IS_ANALOGY_TEST, false);

        this.saveCalculation(wordNetUtil.getCalc(), w2vecModel);
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

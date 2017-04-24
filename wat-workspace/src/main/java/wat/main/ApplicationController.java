package wat.main;

import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.POS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.calculator.AccuracyCalculatorInt;
import wat.calculator.GloveCalculator;
import wat.calculator.GloveCalculatorInt;
import wat.calculator.Word2vecCalculator;
import wat.calculator.Word2vecCalculatorInt;
import wat.wordnet.WordNetUtil;
import wat.wordnet.WordNetUtilInt;
import wat.exceptions.ModelBuildException;
import wat.helper.Constants;

import java.io.IOException;

public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(WordNetUtil.class);
    private WordNetUtilInt wordNetUtil;

    private Word2vecCalculatorInt w2vecCalc = new Word2vecCalculator();
    private GloveCalculatorInt gloveCalc = new GloveCalculator();
    // default olarak word2vec kullanımda
    private int modelBeingUsed = Constants.WORD2VEC;
    private AccuracyCalculatorInt calculator = w2vecCalc;

    public ApplicationController(String wordNetPath) throws IOException, ModelBuildException {

        wordNetUtil = new WordNetUtil(wordNetPath, ILoadPolicy.NO_LOAD);
    }

    /**
     * closes the wordnet and exits from the program.
     */
    public void exit() {

        log.info("Exiting.");
        wordNetUtil.closeDictionary();
        System.exit(0);
    }

    /**
     * updates the info of which model is being used.
     *
     * @param choice 1 for glove, 2 for word2vec.
     */
    public void changeModelToUse(int choice) {

        this.modelBeingUsed = choice;
        if (choice == Constants.GLOVE) {
            calculator = gloveCalc;
        } else if (choice == Constants.WORD2VEC) {
            calculator = w2vecCalc;
        }
    }

    /**
     * calculate accuracy up to user choice. calculated score will be stored in calculator.
     *
     * @param choice
     * @throws IOException
     */
    public void calculateSimilarityScore(int choice) throws IOException {

        if (calculator.isModelReady()) {
            switch (choice) {
                case Constants.ALL_WORDS:
                    wordNetUtil.calculateSimilarityScoreForAllWords(calculator, false);
                    break;
                case Constants.NOUNS_ONLY:
                case Constants.VERBS_ONLY:
                case Constants.ADJECTIVES_ONLY:
                case Constants.ADVERBS_ONLY:
                    wordNetUtil.calculateSimilarityScoreForPOS(calculator, POS.getPartOfSpeech
                            (choice), false);
                    break;
                default:
                    log.error("Invalid POS choice: " + choice);
            }
        } else {
            log.warn("You should first train or load a word2vec model.");
        }
    }

    public void loadDictionaryIntoMemory() {
        // a wordnet will definetely be created when program starts.
        wordNetUtil.loadDictionaryIntoMemory();
    }

    public void changeCorpusPath(String newPath) {

        calculator.updateCorpusPath(newPath);
    }

    public void prepareModel(int corpusIsPretrained) throws ModelBuildException {

        if (this.usesGivenModel(Constants.WORD2VEC)) {
            w2vecCalc.createModel(corpusIsPretrained);
        } else {
            log.warn("Word2vec model is not selected.");
        }
    }

    public void updateWord2vecParams(int trainingParamType) {

        if (this.usesGivenModel(Constants.WORD2VEC)) {
            calculator.updateTrainingParams(trainingParamType);
        } else {
            log.warn("Word2vec model is not selected.");
        }
    }

    public void listBySelection(int selection) throws IOException {

        switch (selection) {
            case 1:
                wordNetUtil.listPointerMap();
                break;
            case 2:
                wordNetUtil.listWordsLexicalPointers();
                break;
            case 3:
                wordNetUtil.listWordsSemanticPointers();
                break;
            case 4:
                wordNetUtil.listNouns();
                break;
            case 5:
                wordNetUtil.listVerbs();
                break;
            case 6:
                break;
            default:
                log.warn("Invalid selection for listing words: " + selection);
        }
    }

    private boolean usesGivenModel(int modelType) {

        return this.modelBeingUsed == modelType;
    }

}

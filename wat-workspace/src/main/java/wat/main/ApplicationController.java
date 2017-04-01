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
import wat.dictionary.DictionaryUtil;
import wat.dictionary.DictionaryUtilInt;
import wat.exceptions.VocabularyBuildException;
import wat.exceptions.Word2vecBuildException;
import wat.helper.Constants;
import wat.helper.ModelType;
import wat.helper.Word2vecParamType;

import java.io.IOException;

public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(DictionaryUtil.class);
    private static boolean debugEnabled = log.isDebugEnabled();
    private Word2vecCalculatorInt w2vecCalc = new Word2vecCalculator();
    private GloveCalculatorInt gloveCalc = new GloveCalculator();
    private DictionaryUtilInt dictionaryUtil;

    // default olarak word2vec kullanımda
    private int modelBeingUsed = ModelType.WORD2VEC;
    private AccuracyCalculatorInt calculator = w2vecCalc;

    public ApplicationController(String wordNetPath) throws IOException, Word2vecBuildException,
            VocabularyBuildException {
        dictionaryUtil = new DictionaryUtil(wordNetPath, ILoadPolicy.NO_LOAD);
    }

    /**
     * updates the info of which model is being used.
     *
     * @param choice 1 for glove, 2 for word2vec.
     */
    public void changeModelToUse(int choice) {
        this.modelBeingUsed = choice;
        if (choice == ModelType.GLOVE) {
            calculator = (AccuracyCalculatorInt) gloveCalc;
        } else if (choice == ModelType.WORD2VEC) {
            calculator = (AccuracyCalculatorInt) w2vecCalc;
        }
    }

    /**
     * closes the dictionary and exits from the program.
     */
    public void exit() {
        log.info("Exiting.");
        dictionaryUtil.closeDictionary();
        System.exit(0);
    }

    /**
     * calculate accuracy up to user choice. calculated score will be stored in calculator.
     *
     * @param choice
     * @throws IOException
     */
    public void calculateAccuracy(int choice) throws IOException {
        if (calculator.isModelReady()) {
            switch (choice) {
                case Constants.ALL_WORDS:
                    dictionaryUtil.calculateAccuracyForAllWords(calculator);
                    break;
                case Constants.NOUNS_ONLY:
                case Constants.VERBS_ONLY:
                case Constants.ADJECTIVES_ONLY:
                case Constants.ADVERBS_ONLY:
                    dictionaryUtil.calculateAccuracyForGivenPOS(calculator, POS.getPartOfSpeech(choice));
                    break;
                default:
                    log.error("Invalid POS choice: " + choice);
            }
        } else {
            log.warn("You should first train or load a word2vec model.");
        }
    }

    public void loadDictionaryIntoMemory() {
        // a dictionary will definetely be created when program starts.
        dictionaryUtil.loadDictionaryIntoMemory();
    }

    public void changeCorpusPath(String newPath) {
        calculator.setCorpusPath(newPath);
    }

    public void prepareWord2vec(int corpusIsPretrained) throws Word2vecBuildException, VocabularyBuildException {
        if (this.usesGivenModel(ModelType.WORD2VEC)) {
            w2vecCalc.prepareWord2vec(corpusIsPretrained);
        } else {
            log.warn("Word2vec model is not selected.");
        }
    }

    public void updateWord2vecParams(int paramType) {
        if (this.usesGivenModel(ModelType.WORD2VEC)) {
            switch (paramType) {
                case Word2vecParamType.WORKERS:
                    int workers = UserInput.getSelectionBetween(1, 16);
                    int availableProcessors = Runtime.getRuntime().availableProcessors();
                    if (workers > availableProcessors) {
                        log.info(workers + " processors are not available so workers param is set to: " +
                                availableProcessors);
                        workers = availableProcessors;
                    }
                    w2vecCalc.getWord2vecParams().setWorkers(workers);
                    break;
                case Word2vecParamType.MIN_WORD_FREQUENCY:
                    break;
                default:
                    log.warn("Wrong word2vec param type: " + paramType);
            }
        } else {
            log.warn("Word2vec model is not selected.");
        }
    }

    public void listBySelection(int selection) throws IOException {
        switch (selection) {
            case 1:
                dictionaryUtil.listSenseKeyAndSynsetsOfAdjectives();
                break;
            case 2:
                dictionaryUtil.listNounsWithPointers();
                break;
            case 3:
                dictionaryUtil.listVerbs();
                break;
            default:
                log.warn("Invalid selection for listing words: " + selection);
        }
    }

    private boolean usesGivenModel(int modelType) {
        return this.modelBeingUsed == modelType;
    }

}
package wat.main;

import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.POS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.calculator.CalculatorInt;
import wat.exceptions.ModelBuildException;
import wat.file.FileActions;
import wat.helper.Constants;
import wat.training.model.BaseModelInt;
import wat.training.model.glove.GloveUtil;
import wat.training.model.glove.GloveUtilInt;
import wat.training.model.word2vec.Word2vecUtil;
import wat.training.model.word2vec.Word2vecUtilInt;
import wat.wordnet.WordNetUtil;
import wat.wordnet.WordNetUtilInt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// isCorpusTrained kullanılmalı
public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

    private Word2vecUtilInt w2vecCalc = new Word2vecUtil();
    private GloveUtilInt gloveCalc = new GloveUtil();
    // default olarak word2vec kullanımda
    private int usedModelID = Constants.WORD2VEC;
    private BaseModelInt usedModel = w2vecCalc;
    private WordNetUtilInt wordNetUtil = null;

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

        if (usedModelID != choice) {
            if (choice == Constants.GLOVE) {
                usedModel = gloveCalc;
            } else if (choice == Constants.WORD2VEC) {
                usedModel = w2vecCalc;
            }
            usedModelID = choice;
        } else {
            log.info("Already using " + usedModel.getName());
        }
    }

    /**
     * sends the folder path to save compressed zip file in.
     * folder path is the 'HOME_DIR'/'MODEL_NAME'_saved
     */
    public void saveTrainedModel() {

        if (usedModel.isModelReady()) {
            String name = this.getUsedModelName();
            File file = FileActions.getFolderToSaveModel(name + "_saved");
            if (usedModel.saveTrainedModel(file)) {
                log.info(this.getUsedModelName() + " embeddings are saved successfully.");
            }
        } else {
            log.warn(this.getUsedModelName() + " is not created.");
        }
    }

    /**
     * calculate accuracy up to user choice of POS.
     * calculated score will be saved into a text file.
     *
     * @param choice
     * @param isAnalogyTest
     * @throws IOException
     */
    public void calculateScore(int choice, boolean isAnalogyTest) throws IOException {

        if (usedModel.isModelReady()) {
            switch (choice) {
                case Constants.ALL_WORDS:
                    wordNetUtil.calculateScoreForAllWords(usedModel, isAnalogyTest);
                    break;
                case Constants.NOUNS_ONLY:
                case Constants.VERBS_ONLY:
                case Constants.ADJECTIVES_ONLY:
                case Constants.ADVERBS_ONLY:
                    wordNetUtil.calculateScoreForPOS(usedModel,
                            POS.getPartOfSpeech(choice), isAnalogyTest);
                    break;
                default:
                    log.error("Invalid POS choice: " + choice);
            }
            // first save scores
            this.saveCalculationScore();
            // then reset all
            wordNetUtil.getCalc().resetScores();
        } else {
            log.warn("You should first train or load a model.");
        }
    }

    public void getAnalogyScoreOfTypedWord(final String wordInput) {

        if (usedModel.isModelReady()) {
            wordNetUtil.calculateAnalogyScoreOfWordInput(usedModel, wordInput);
        } else {
            log.warn("You should first train or load a model.");
        }
    }

    public void saveCalculationScore() {

        CalculatorInt calc = wordNetUtil.getCalc();
        List<String> lines = new ArrayList<String>(7) {{
            add("Similarity score: " + calc.getSimilarityScore());
            add("Total similarity calculations: " + calc.getTotalSimCalculations());
            add("Similarity percentage: " + calc.getSimilarityPercentage());
            add("Analogy score: " + calc.getAnalogyScore());
            add("Total analogy calculations: " + calc.getTotalAnalogicCalculations());
            add("Analogical percentage: " + calc.getAnalogicalPercentage());
            add("Maximum score for analogy algorithm: " + calc.getMaxScoreForAnalogy());
        }};

        try {
            FileActions.writeToFileWithDifferentNames(usedModel.getName() + "_saved", lines);
        } catch (IOException e) {
            log.error("Scores could not be saved!", e);
        }
    }

    public void loadDictionaryIntoMemory() {
        // a wordnet will definetely be created when program starts.
        wordNetUtil.loadDictionaryIntoMemory();
    }

    public void changeCorpusPath(String newPath) {

        usedModel.setCorpusPath(newPath);
    }

    /**
     * @param corpusIsPretrained true if corpus is not raw text but a prebuilt model.
     * @throws ModelBuildException
     */
    public void prepareModel(int corpusIsPretrained) throws ModelBuildException {

        usedModel.createModel(corpusIsPretrained);
    }

    public void updateSelectedModelParams(int trainingParamType) {

        switch (trainingParamType) {
//            case Word2vecParamType.WORKERS:
//                int workers = UserInput.getSelectionBetween(1, 8);
//                int availableProcessors = Runtime.getRuntime().availableProcessors();
//                if (workers > availableProcessors) {
//                    log.info(workers + " processors are not available so param 'workers' is set to: " +
//                            availableProcessors);
//                    workers = availableProcessors;
//                }
//                usedModel.getWord2vecParams().setWorkers(workers);
//                break;
//            case Word2vecParamType.MIN_WORD_FREQUENCY:
//                break;
            default:
                log.warn("Wrong word2vec param type: " + trainingParamType);
        }
    }

    public void resetModelParams() {

        usedModel.resetParams();
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
                wordNetUtil.listAdjectives();
                break;
            case 7:
                wordNetUtil.listAdverbs();
                break;
            default:
                log.warn("Invalid selection for listing words: " + selection);
        }
    }

    public String getUsedModelName() {

        return usedModel.getName();
    }

}

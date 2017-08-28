package wat.main;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.POS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.calculator.Calculator;
import wat.exceptions.ModelBuildException;
import wat.file.FileActions;
import wat.helper.Constants;
import wat.helper.DefaultSettingValues;
import wat.helper.TrainingParamTypes;
import wat.training.model.BaseModelInt;
import wat.training.model.BaseTrainingParams;
import wat.training.model.glove.GloveTrainingParams;
import wat.training.model.glove.GloveUtil;
import wat.training.model.word2vec.Word2vecTrainingParams;
import wat.training.model.word2vec.Word2vecUtil;
import wat.wordnet.WordNetUtil;
import wat.wordnet.WordNetUtilInt;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

    private Word2vecUtil word2vecUtil = new Word2vecUtil();
    private GloveUtil gloveUtil = new GloveUtil();

    // bu kaldırılabilir
    private int usedModelID = Constants.WORD2VEC;
    // default olarak word2vec kullanımda
    private BaseModelInt usedModel = word2vecUtil;
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
                usedModel = gloveUtil;
            } else if (choice == Constants.WORD2VEC) {
                usedModel = word2vecUtil;
            }
            usedModelID = choice;
        } else {
            log.info("Already using " + usedModel.getName());
        }
    }

    /**
     * sends the compressed zip file to save model.
     * creates any non existing directory while creating file.
     */
    public void saveTrainedModel() throws IOException {

        String name = usedModel.getName();
        if (usedModel.isModelReady()) {
            long start = System.currentTimeMillis();
            Path path = FileActions.getUniquePathForGivenFileName(
                    name + "_saved", "trained_" + name);

            if (usedModel.saveTrainedModel(path.toFile())) {
                log.info(name + " embeddings are saved successfully in "
                        + (System.currentTimeMillis() - start) / 1000 + " seconds.");
            }
        } else {
            log.warn(name + " is not created.");
        }
    }

    /**
     * calculate accuracy up to user choice of POS.
     * calculated score will be saved into a text file.
     *
     * @param choice
     * @param isAnalogyTest
     * @param onlySynsets
     * @throws IOException
     */
    public void calculateScore(int choice, boolean isAnalogyTest, boolean onlySynsets) throws IOException {

        if (usedModel.isModelReady()) {
            switch (choice) {
                case Constants.ALL_WORDS:
                    wordNetUtil.calculateScoreForAllWords(usedModel, isAnalogyTest, onlySynsets);
                    break;
                case Constants.NOUNS_ONLY:
                case Constants.VERBS_ONLY:
                case Constants.ADJECTIVES_ONLY:
                case Constants.ADVERBS_ONLY:
                    wordNetUtil.calculateScoreForPOSFromController(usedModel,
                            POS.getPartOfSpeech(choice), isAnalogyTest, onlySynsets);
                    break;
                default:
                    log.error("Invalid POS choice: " + choice);
            }
            // first save scores
            this.saveCalculationScore();
            // then reset all
            wordNetUtil.getCalc().resetProperties();
        } else {
            log.warn("You should first train or load a model.");
        }
    }

    public void getAnalogyScoreOfTypedWord() {

        if (usedModel.isModelReady()) {
            final String wordInput = UserInput.getWordInput();
            if (wordInput != null) {
                wordNetUtil.calculateAnalogyScoreOfWordInput(usedModel, wordInput, UserInput.getAnalogyAlgorithm());
                // first save scores
                this.saveCalculationScore();
                // then reset all
                wordNetUtil.getCalc().resetProperties();
            }
        } else {
            log.warn("You should first train or load a model.");
        }
    }

    public void saveCalculationScore() {

        final Calculator calc = wordNetUtil.getCalc();
        final List<String> lines = new ArrayList<String>(3) {{
            add("After calculation: " + calc.toString());
            add("Analogical percentage: " + calc.getAnalogicalPercentage());
            add("Similarity percentage: " + calc.getSimilarityPercentage());
        }};

        try {
            FileActions.writeToFileByCreatingFile(lines,
                    usedModel.getName() + "_scores", "score.txt");
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

    public void updateSelectedModelParams(final TrainingParamTypes trainingParamType) {

        boolean notCommon = false;
        final BaseTrainingParams params = usedModel.getParams();

        switch (trainingParamType) {
            case LAYER_SIZE:
                System.out.println("\nParameter layer size:");
                params.setLayerSize(UserInput.getSelectionBetween(
                        DefaultSettingValues.LAYER_SIZE_LEAST, DefaultSettingValues.LAYER_SIZE_CAP));
                break;
            case WINDOW_SIZE:
                System.out.println("\nParameter window size:");
                params.setWindowSize(UserInput.getSelectionBetween(
                        DefaultSettingValues.WINDOW_SIZE_LEAST, DefaultSettingValues.WINDOW_SIZE_CAP));
                break;
            case MIN_WORD_FREQUENCY:
                System.out.println("\nParameter min word frequency:");
                params.setMinWordFrequency(UserInput.getSelectionBetween(
                        DefaultSettingValues.MIN_WORD_FREQUENCY_LEAST, DefaultSettingValues
                                .MIN_WORD_FREQUENCY_CAP));
                break;
            case WORKERS:
                System.out.println("\nParameter workers:");
                params.setWorkers(UserInput.getSelectionBetween(1, 8));
                break;
            case EPOCHS:
                System.out.println("\nParameter epochs:");
                params.setEpochs(UserInput.getSelectionBetween(
                        DefaultSettingValues.EPOCHS_LEAST, DefaultSettingValues.EPOCHS_CAP));
                break;
            case BATCH_SIZE:
                System.out.println("\nParameter batch size:");
                params.setBatchSize(UserInput.getSelectionBetween(
                        DefaultSettingValues.BATCH_SIZE_LEAST, DefaultSettingValues.BATCH_SIZE_CAP));
                break;
            case LEARNING_RATE:
                System.out.println("\nParameter learning rate:");
                params.setLearningRate(UserInput.getDoubleSelection(
                        DefaultSettingValues.LEARNING_RATE_LEAST, DefaultSettingValues.LEARNING_RATE_CAP));
                break;
            case MIN_LEARNING_RATE:
                System.out.println("\nParameter min learning rate:");
                params.setMinLearningRate(UserInput.getDoubleSelection(
                        DefaultSettingValues.MIN_LEARNING_RATE_LEAST, DefaultSettingValues
                                .MIN_LEARNING_RATE_CAP));
                break;
            case SEED:
                System.out.println("\nParameter seed:");
                params.setSeed(UserInput.getSelectionBetween(1, Integer.MAX_VALUE));
                break;
            default:
                notCommon = true;
                log.info("Param type: " + trainingParamType + " is not common.");
        }

        // glove ve word2vec için her parametre case'ine if else koymamak için
        if (notCommon) {
            if (usedModelID == Constants.GLOVE) {

                switch (trainingParamType) {
                    case SHUFFLE_G:
                        ((GloveTrainingParams) params).setShuffle(UserInput.getParamShuffle());
                        break;
                    case SYMMETRIC_G:
                        ((GloveTrainingParams) params).setSymmetric(UserInput.getParamSymmetric());
                        break;
                    case XMAX_G:
                        System.out.println("\nParameter xMax:");
                        ((GloveTrainingParams) params).setxMax(UserInput.getDoubleSelection(1.0d, 500.0d));
                        break;
                    case ALPHA_G:
                        System.out.println("\nParameter alpha:");
                        ((GloveTrainingParams) params).setAlpha(UserInput.getDoubleSelection(0.0001d, 1.0d));
                        break;
                    default:
                        log.warn("Invalid param type: " + trainingParamType
                                + " for model: " + usedModel.getName());
                }
            } else if (usedModelID == Constants.WORD2VEC) {
                switch (trainingParamType) {
                    case ITERATIONS_W2:
                        System.out.println("\nParameter iterations:");
                        ((Word2vecTrainingParams) params).setIterations(UserInput.
                                getSelectionBetween(1, 100));
                        break;
                    case NEGATIVE_W2:
                        System.out.println("\nParameter negative:");
                        ((Word2vecTrainingParams) params).setNegative(UserInput
                                .getDoubleSelection(0.0d, 100.0d));
                        break;
                    case SAMPLING_W2:
                        System.out.println("\nParameter sampling:");
                        ((Word2vecTrainingParams) params).setSampling(UserInput
                                .getDoubleSelection(0.0d, 100.0d));
                        break;
                    case HIERARCHIC_SOFTMAX_OR_NEGATIVE_SAMPLING_W2:
                        ((Word2vecTrainingParams) params).setUseHierarchicSoftmax(
                                UserInput.getParamUseHierarchicSoftmax());
                        break;
                    case HUGE_MODEL_EXPECTED_W2:
                        ((Word2vecTrainingParams) params).setHugeModelExpected(
                                UserInput.getParamHugeModelExpected());
                        break;
                    case CBOW_OR_SKIP_GRAM_W2:
                        ((Word2vecTrainingParams) params).setSkipGramOrCBOW(
                                UserInput.getParamSkipGramOrCBOW());
                        break;
                    default:
                        log.warn("Invalid param type: " + trainingParamType
                                + " for model: " + usedModel.getName());
                }
            }
        }
    }

    public void changeSettings(int setting) {

        switch (setting) {
            case Constants.CALCULATION_SETTING:
                wordNetUtil.getCalc().setCalculationOption(UserInput.getCalculationOption());
                wordNetUtil.getCalc().prepareScoresForAnalogyTask(usedModel.getClosestWordSize());
                break;
            case Constants.BASE_SENSITIVITY_SETTING:
                // proportional hesaplama yapılıyorsa sensitivity için input alma
                if (wordNetUtil.getCalc().getCalculationOption()
                        == DefaultSettingValues.CALCULATE_PROPORTIONALLY) {
                    log.info("Calculation option is chosen proportionally, " +
                            "sensitivity is not used in this function.");
                } else {
                    wordNetUtil.getCalc().setBaseSensitivityAndMaxScore(
                            UserInput.getSelectionBetween(2, 100), usedModel.getClosestWordSize());
                }
                break;
            case Constants.CLOSEST_WORD_SIZE_SETTING:
                usedModel.setClosestWordSize(UserInput.getSelectionBetween(3, 100));
                wordNetUtil.getCalc().prepareScoresForAnalogyTask(usedModel.getClosestWordSize());
                break;
            case Constants.ITERATION_CAP_FOR_POINTER_SETTING:
                wordNetUtil.setIterationCapForPointer(UserInput.getSelectionBetween(3, 200000));
                break;
            case Constants.RESET_ITERATION_CAP_SETTING:
                wordNetUtil.setIterationCapForPointer(DefaultSettingValues.ITERATION_CAP_FOR_POINTER);
                break;
            case Constants.RESET_SCORES_SETTING:
                usedModel.setClosestWordSize(DefaultSettingValues.CLOSEST_WORD_SIZE);
                wordNetUtil.getCalc().setCalculationOption(DefaultSettingValues.CALCULATE_PROPORTIONALLY);
                wordNetUtil.getCalc().prepareScoresForAnalogyTask(
                        DefaultSettingValues.BASE_SENSITIVITY, usedModel.getClosestWordSize());
                break;
            case Constants.PHRASE_SETTING:
                wordNetUtil.setPhraseComparisonSetting(UserInput.getPhraseSetting());
                break;
            default:
                log.error("Invalid setting: " + setting);
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

    public void getNearestOfInputWord() {

        if (usedModel.isModelReady()) {
            final String wordInput = UserInput.getWordInput();
            if (wordInput != null) {
                if (usedModel.hasWord(wordInput)) {
                    final List<String> result = usedModel.getNearestWords(wordInput);
                    final int resultSize = result.size();
                    StringBuilder string = new StringBuilder(30);
                    for (int i = 0; i < resultSize; i++) {
                        string.append("\n").append(i).append(" - ").append(result.get(i));
                    }
                    log.info("Nearest words: " + string.toString());
                } else {
                    log.info("Word: " + wordInput + " does not exist in model vocab.");
                }
            }
        } else {
            log.warn("You should first train or load a model.");
        }
    }

    public void printTotalWordSizeInModelVocab() {

        if (usedModel.isModelReady()) {
            log.info(usedModel.getName() + " has "
                    + usedModel.getTotalWordSizeInVocab() + " words in total.");
        } else {
            log.warn("You should first train or load a model.");
        }
    }

    public void changeLogLevel(int logLevel) {

        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        final ch.qos.logback.classic.Logger root = context.getLogger("ROOT");
        final String levelBeforeChange = root.getEffectiveLevel().toString();
        root.setLevel(Level.toLevel(logLevel));
        if (log.isWarnEnabled()) {
            log.warn("Log level of root is changed from " + levelBeforeChange
                    + " to: " + root.getEffectiveLevel().toString());
        } else {
            log.error("Logged as an error because of high level. Log level of root is changed from "
                    + levelBeforeChange + " to: " + root.getEffectiveLevel().toString());
        }
//        log.warn("Logback status for debug purposes:");
//        StatusPrinter.print(context);
    }

}

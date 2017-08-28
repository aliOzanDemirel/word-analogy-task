package wat.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.exceptions.ModelBuildException;
import wat.file.FileActions;
import wat.helper.Constants;
import wat.helper.TrainingParamTypes;

import java.io.IOException;

public class WordAnalogyTask {

    private static final Logger log = LoggerFactory.getLogger(WordAnalogyTask.class);

    public static void main(String[] args) throws IOException, ModelBuildException {

        final String wordNetDictHome = System.getenv("WORDNET_PATH");
        if (wordNetDictHome == null || !FileActions.isPathValid(wordNetDictHome)) {
            log.error("WORDNET_PATH does not have a valid path, thus WordNet could not be found!");
            System.exit(1);
        }

        log.info("Application started. WordNet home: " + wordNetDictHome
                + " Java home: " + System.getenv("JAVA_HOME"));
        final ApplicationController controller = new ApplicationController(wordNetDictHome);
        for (; ; ) {
            try {
                log.info("Using " + controller.getUsedModelName());
                switch (UserInput.getMenuSelection()) {
                    case 0:
                        controller.exit();
                    case 1:
                        // load WordNet into memory for higher speed
                        controller.loadDictionaryIntoMemory();
                        break;
                    case 2:
                        // changes some calculation settings
                        final int setting = UserInput.getSettingID();
                        if (setting != 0) {
                            controller.changeSettings(setting);
                        }
                        break;
                    case 3:
                        // change training model to use
                        final int modelID = UserInput.getModelID();
                        if (modelID != 0) {
                            controller.changeModelToUse(modelID);
                        }
                        break;
                    case 4:
                        // change parameter for model training
                        for (; ; ) {
                            int paramType = UserInput.getParamType();
                            if (paramType == 0) {
                                break;
                            }
                            controller.updateSelectedModelParams(TrainingParamTypes.getByValue(paramType));
                        }
                        break;
                    case 5:
                        // reset model parameters
                        final boolean isSure = UserInput.isUserSure();
                        if (isSure) {
                            controller.resetModelParams();
                        }
                        break;
                    case 6:
                        // changes corpus path if it is valid
                        String newPath = UserInput.getNewPathForCorpus();
                        if (newPath != null) {
                            controller.changeCorpusPath(newPath);
                        }
                        break;
                    case 7:
                        // gets corpus type before loading model
                        final int corpusType = UserInput.getCorpusType();
                        if (corpusType != 0) {
                            controller.prepareModel(corpusType);
                        }
                        break;
                    case 8:
                        // save trained model
                        controller.saveTrainedModel();
                        break;
                    case 9:
                        // save calculated scores
                        controller.saveCalculationScore();
                        break;
                    case 10:
                        // print number of words in model's vocabulary
                        controller.printTotalWordSizeInModelVocab();
                        break;
                    case 11:
                        // get closest words from model for a given word
                        controller.getNearestOfInputWord();
                        break;
                    case 12:
                        // calculate analogy score of a word of user's choice
                        controller.getAnalogyScoreOfTypedWord();
                        break;
                    case 13:
                        // calculate analogy score
                        final int pos = UserInput.getPOSSelection();
                        if (pos != 0) {
                            controller.calculateScore(pos, Constants.IS_ANALOGY_TEST, UserInput
                                    .getAnalogyAlgorithm());
                        }
                        break;
                    case 14:
                        // calculate similarity score
                        final int posChoice = UserInput.getPOSSelection();
                        if (posChoice != 0) {
                            controller.calculateScore(posChoice, Constants.IS_SIMILARITY_TEST, UserInput
                                    .getAnalogyAlgorithm());
                        }
                        break;
                    case 15:
                        // change log level of root
                        final int logLevel = UserInput.getLogLevel();
                        if (logLevel != 0) {
                            controller.changeLogLevel(logLevel);
                        }
                        break;
                    case 16:
                        log.info("(MB) Free: " + Runtime.getRuntime().freeMemory() / 1024 / 1024);
                        log.info("(MB) Total: " + Runtime.getRuntime().totalMemory() / 1024 / 1024);
                        log.info("(GB) Max: " + Runtime.getRuntime().maxMemory() / 1024 / 1024 / 1024);
                        break;
                    case 17:
                        // log WordNet data to examine relations
                        final int listing = UserInput.getListingOptions();
                        if (listing != 0) {
                            controller.listBySelection(listing);
                        }
                        break;
                    default:
                        log.warn("Wrong input!");
                }
            } catch (Throwable e) {
                log.error("Error!", e);
            }
        }
    }

}

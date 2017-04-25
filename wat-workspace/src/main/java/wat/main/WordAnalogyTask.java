package wat.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.exceptions.ModelBuildException;
import wat.helper.Constants;

import java.io.IOException;

public class WordAnalogyTask {

    private static final Logger log = LoggerFactory.getLogger(WordAnalogyTask.class);

    public static void main(String[] args) throws IOException, ModelBuildException {

        // logback status'u yazması için
        // StatusPrinter.print((LoggerContext) LoggerFactory.getILoggerFactory());

        String wordNetDictHome = System.getenv("WORDNET_PATH");
        log.info("Application started. WordNet home: " + wordNetDictHome
                + " Java home: " + System.getenv("JAVA_HOME"));
        ApplicationController controller = new ApplicationController(wordNetDictHome);
        for (; ; ) {
            try {
                log.info("Using " + controller.getUsedModelName());
                switch (UserInput.getMenuSelection()) {
                    case 1:
                        // load WordNet into memory for higher speed
                        controller.loadDictionaryIntoMemory();
                        break;
                    case 2:
                        // log WordNet data to examine relations
                        int listing = UserInput.getListingOptions();
                        if (listing != 8) {
                            controller.listBySelection(listing);
                        }
                        break;
                    case 3:
                        // change training model to use
                        int modelID = UserInput.getModelID();
                        if (modelID != 3) {
                            controller.changeModelToUse(modelID);
                        }
                        break;
                    case 4:
                        // change parameter for model training
                        for (; ; ) {
                            int paramType = UserInput.getWord2vecParam();
                            if (paramType == 8) {
                                break;
                            }
                            controller.updateSelectedModelParams(paramType);
                        }
                        break;
                    case 5:
                        // reset model parameters
                        boolean isSure = UserInput.isUserSure();
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
                        int corpusType = UserInput.getCorpusType();
                        if (corpusType != 3) {
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
                        // calculate analogy score of a word of user's choice
                        String selection = UserInput.getWordInput();
                        if (selection != null) {
                            controller.getAnalogyScoreOfTypedWord(selection);
                        }
                        break;
                    case 11:
                        // calculate analogy score
                        int pos = UserInput.getPOSSelection();
                        if (pos != 5) {
                            controller.calculateScore(pos, Constants.IS_ANALOGY_TEST);
                        }
                        break;
                    case 12:
                        // calculate similarity score
                        int posChoice = UserInput.getPOSSelection();
                        if (posChoice != 5) {
                            controller.calculateScore(posChoice, Constants.IS_SIMILARITY_TEST);
                        }
                        break;
                    case 13:
                        log.warn("FREE MB: " + Runtime.getRuntime().freeMemory() / 1024 / 1024);
                        log.warn("TOTAL MB: " + Runtime.getRuntime().totalMemory() / 1024 / 1024);
                        log.warn("MAX MB: " + Runtime.getRuntime().maxMemory() / 1024 / 1024);
                        break;
                    case 14:
                        controller.exit();
                    default:
                        log.warn("Wrong input!");
                }
            } catch (Throwable e) {
                log.error("Error!", e);
            }
        }
    }

}

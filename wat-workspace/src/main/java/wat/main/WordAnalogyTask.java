package wat.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.exceptions.ModelBuildException;

import java.io.IOException;

public class WordAnalogyTask {

    private static final Logger log = LoggerFactory.getLogger(WordAnalogyTask.class);

    public static void main(String[] args) throws IOException, ModelBuildException {

        // logback status'u yazması için
        // StatusPrinter.print((LoggerContext) LoggerFactory.getILoggerFactory());

        String wordNetDictHome = System.getenv("WORDNET_PATH");
        log.info("Application started. Total memory: " + Runtime.getRuntime().totalMemory() / 1024 +
                " WordNet home: " + wordNetDictHome + " Java home: " + System.getenv("JAVA_HOME"));
        ApplicationController controller = new ApplicationController(wordNetDictHome);
        for (; ; ) {
            try {
                switch (UserInput.getMenuSelection()) {
                    case 1:
                        controller.loadDictionaryIntoMemory();
                        break;
                    case 2:
                        int pos = UserInput.getPOSSelection();
                        if (pos != 5) {
                            controller.calculateSimilarityScore(pos);
                        }
                        break;
                    case 3:
                        int modelType = UserInput.getModelType();
                        if (modelType != 3) {
                            controller.changeModelToUse(modelType);
                        }
                        break;
                    case 4:
                        int corpusType = UserInput.getCorpusType();
                        if (corpusType != 3) {
                            controller.prepareModel(corpusType);
                        }
                        break;
                    case 5:
                        for (; ; ) {
                            int paramType = UserInput.getWord2vecParam();
                            if (paramType == 8) {
                                break;
                            }
                            controller.updateWord2vecParams(paramType);
                        }
                        break;
                    case 6:
                        String newPath = UserInput.getNewPathForCorpus();
                        if (newPath != null) {
                            controller.changeCorpusPath(newPath);
                        }
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    case 10:
                        break;
                    case 11:
                        break;
                    case 12:
                        log.info("FREE MB: " + Runtime.getRuntime().freeMemory() / 1024 / 1024);
                        log.info("TOTAL MB: " + Runtime.getRuntime().totalMemory() / 1024 / 1024);
                        log.info("MAX MB: " + Runtime.getRuntime().maxMemory() / 1024 / 1024);
                        break;
                    case 13:
                        int listing = UserInput.getListingOptions();
                        if (listing != 7) {
                            controller.listBySelection(listing);
                        }
                        break;
                    case 14:
                        controller.exit();
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

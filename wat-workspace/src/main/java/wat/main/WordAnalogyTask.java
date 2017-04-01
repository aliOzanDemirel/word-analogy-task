package wat.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.exceptions.VocabularyBuildException;
import wat.exceptions.Word2vecBuildException;

import java.io.IOException;

public class WordAnalogyTask {

    private static final Logger log = LoggerFactory.getLogger(WordAnalogyTask.class);

    public static void main(String[] args) throws IOException, Word2vecBuildException, VocabularyBuildException {

        // logback status'u yazması için
        // StatusPrinter.print((LoggerContext) LoggerFactory.getILoggerFactory());

        String wordNetDictHome = System.getenv("WordNet_PATH");
        log.info("Application started. WordNet home: " + wordNetDictHome + " Java home: " + System.getenv("JAVA_HOME"));
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
                            controller.calculateAccuracy(pos);
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
                            controller.prepareWord2vec(corpusType);
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

                    case 10:
                        break;
                    case 11:
                        break;
                    case 12:
                        break;
                    case 13:
                        int listing = UserInput.getListingOptions();
                        if (listing != 4) {
                            controller.listBySelection(listing);
                        }
                        break;
                    case 14:
                        controller.exit();
                        break;
                    default:
                        log.warn("Wrong input!");
                }
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
    }

}

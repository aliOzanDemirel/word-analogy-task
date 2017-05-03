package wat.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.file.FileActions;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UserInput {

    private static Scanner input = new Scanner(System.in);
    private static final Logger log = LoggerFactory.getLogger(UserInput.class);

    public static int getSelectionBetween(int min, int max) {

        int selection = 0;
        do {
            log.warn("Enter a valid value between " + min + " and " + max);
            try {
                selection = input.nextInt();
            } catch (InputMismatchException e) {
                log.warn("Only numbers!");
            }
        } while (selection < min || selection > max);
        return selection;
    }

    public static int getMenuSelection() {

        log.info("******************************************");
        log.info("* 1-) load word net into memory          *");
        log.info("* 2-) show listing options               *");
        log.info("* 3-) choose model for training          *");
        log.info("* 4-) change model's params              *");
        log.info("* 5-) reset model params                 *");
        log.info("* 6-) change corpus path for model       *");
        log.info("* 7-) build or load model                *");
        log.info("* 8-) save trained model                 *");
        log.info("* 9-) save calculated scores             *");
        log.info("* 10-) get analogy score of one word     *");
        log.info("* 11-) calculate analogy score           *");
        log.info("* 12-) calculate similarity score        *");
        log.info("* 13-) change calculation settings       *");
        log.info("* 14-) get most similar words of a word  *");
        log.info("* 15-) log memory                        *");
        log.info("* 16-) exit                              *");
        log.info("******************************************");
        return UserInput.getSelectionBetween(1, 16);
    }

    public static int getPOSSelection() {

        log.info("**********************");
        log.info("* 0 to cancel        *");
        log.info("* 1 for noun         *");
        log.info("* 2 for verb         *");
        log.info("* 3 for adjective    *");
        log.info("* 4 for adverb       *");
        log.info("* 5 for all          *");
        log.info("**********************");
        return UserInput.getSelectionBetween(0, 5);
    }

    public static int getCorpusType() {

        log.info("******************************************");
        log.info("* 1 to build model by training corpus    *");
        log.info("* 2 to use an already trained model      *");
        log.info("* 3 to cancel                            *");
        log.info("******************************************");
        return UserInput.getSelectionBetween(1, 3);
    }

    public static int getListingOptions() {

        log.info("************************************");
        log.info("* 1 to listPointerMap              *");
        log.info("* 2 to listWordsLexicalPointers    *");
        log.info("* 3 to listWordsSemanticPointers   *");
        log.info("* 4 to listNouns                   *");
        log.info("* 5 to listVerbs                   *");
        log.info("* 6 to listAdjectives              *");
        log.info("* 7 to listAdverbs                 *");
        log.info("* 8 to cancel                      *");
        log.info("************************************");
        return UserInput.getSelectionBetween(1, 8);
    }

    public static String getNewPathForCorpus() {

        // clear line
        input.nextLine();

        String selection = null;
        do {
            log.warn("Enter a valid path for corpus file or enter 'c' to cancel.");
            selection = input.nextLine();
            if ("c".equals(selection)) {
                return null;
            }
        } while (selection.isEmpty() || !FileActions.isPathValid(selection));

        return selection;
    }

    public static String getWordInput() {

        // clear line
        input.nextLine();

        log.warn("Enter a word or enter 'c' to cancel.");
        String selection = input.nextLine();
        if ("c".equals(selection)) {
            selection = null;
        }
        return selection;
    }

    public static int getModelID() {

        log.info("********************");
        log.info("* 0 to cancel      *");
        log.info("* 1 for glove      *");
        log.info("* 2 for word2vec   *");
        log.info("********************");
        return UserInput.getSelectionBetween(0, 2);
    }

    public static int getWord2vecParam() {

        log.info("******************************************************************");
        log.info("* 1 to change how many threads can be utilized                   *");
        log.info("* 2 to change window size                                        *");
        log.info("* 3 to change layer size                                         *");
        log.info("* 4 to change minumum word frequency                             *");
        log.info("* 5 if huge model is expected                                    *");
        log.info("* 6 to use negative sampling instead of hierarchic softmax       *");
        log.info("* 7 to disallow parallel tokenization                            *");
        log.info("* 8 to cancel                                                    *");
        log.info("******************************************************************");
        return UserInput.getSelectionBetween(1, 8);
    }

    public static boolean isUserSure() {

        // clear line
        input.nextLine();

        log.warn("Press 'y' if you are sure, press anything else to cancel.");
        String selection = input.nextLine();
        if ("y".equalsIgnoreCase(selection)) {
            return true;
        } else {
            return false;
        }
    }

    public static int getSettingID() {

        log.info("**********************");
        log.info("* 0 to cancel        *");
        log.info("* 1 for base value of max score, higher means bigger gap between 1st and 2nd order       " +
                "     *");
        log.info("* 2 for total words to retrieve from model when checking proximity of a word with a word " +
                "pair *");
        log.info("* 3 for iteration cap of a pointer while doing analogy test.                             " +
                "     *");
        log.info("* 4 for resetting iteration cap to default value.                     *");
        log.info("* 5 for resetting base sensitivity, closest word size and max score.  *");
        log.info("**********************");
        return UserInput.getSelectionBetween(0, 5);
    }

}

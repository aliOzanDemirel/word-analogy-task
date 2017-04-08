package wat.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.file.FileUtil;

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

        log.info("************************************************");
        log.info("* 1-) load word net dict into memory           *");
        log.info("* 2-) choose pos to calculate accuracy         *");
        log.info("* 3-) pick a model to vectorize words          *");
        log.info("* 4-) build word2vec                           *");
        log.info("* 5-) change word2vec params                   *");
        log.info("* 6-) change corpus path                       *");
//        log.info("* 7-) build glove                              *");
//        log.info("* 8-) change glove params                      *");
//        log.info("* 9-) reset model params                       *");
        log.info("* 10-)                                         *");
        log.info("* 11-)                                         *");
        log.info("* 12-) log memory                              *");
        log.info("* 13-) show listing options                    *");
        log.info("* 14-) exit                                    *");
        log.info("* 12-)                                         *");
        log.info("************************************************");
        return UserInput.getSelectionBetween(1, 14);
    }

    public static int getPOSSelection() {

        log.info("**********************");
        log.info("* 0 for all          *");
        log.info("* 1 for noun         *");
        log.info("* 2 for verb         *");
        log.info("* 3 for adjective    *");
        log.info("* 4 for adverb       *");
        log.info("* 5 to cancel        *");
        log.info("**********************");
        return UserInput.getSelectionBetween(0, 5);
    }

    public static int getCorpusType() {

        log.info("**************************************************");
        log.info("* 1 to build word2vec by training corpus         *");
        log.info("* 2 to load wordvec from already trained model   *");
        log.info("* 3 to cancel                                    *");
        log.info("**************************************************");
        return UserInput.getSelectionBetween(1, 3);
    }

    public static int getListingOptions() {

        log.info("*************************************");
        log.info("* 1 to listPointerMap               *");
        log.info("* 2 to listWordsLexicalPointers     *");
        log.info("* 3 to listWordsSemanticPointers    *");
        log.info("* 4 to listNouns                    *");
        log.info("* 5 to listVerbs                    *");
        log.info("* 6 to                              *");
        log.info("* 7 to cancel                       *");
        log.info("*************************************");
        return UserInput.getSelectionBetween(1, 7);
    }

    public static String getNewPathForCorpus() {

        String selection = null;
        input.nextLine();
        do {
            log.warn("Enter path of word2vec corpus file or enter 'c' to cancel.");
            selection = input.nextLine();
            if ("c".equals(selection)) {
                return null;
            }
        } while (selection.isEmpty() || !FileUtil.isPathValid(selection));

        return selection;
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

    public static int getModelType() {

        log.info("**********************");
        log.info("* 1 for glove        *");
        log.info("* 2 for word2vec     *");
        log.info("* 3 to cancel        *");
        log.info("**********************");
        return UserInput.getSelectionBetween(1, 3);
    }

}

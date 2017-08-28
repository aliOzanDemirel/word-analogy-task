package wat.main;

import wat.file.FileActions;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UserInput {

    private static Scanner input = new Scanner(System.in);

    public static int getSelectionBetween(int min, int max) {

        int selection = 0;
        do {
            System.out.println("Enter a valid value between " + min + " and " + max);
            try {
                selection = input.nextInt();
            } catch (InputMismatchException e) {
                // YAZI GİRİLİNCE exit methodu çağrılıyor.
                System.err.println("Only numbers!");
                input.nextLine();
            }
        } while (selection < min || selection > max);
        return selection;
    }

    public static double getDoubleSelection(double min, double max) {

        double selection = 0;
        do {
            System.out.println("Enter a valid value between " + min + " and " + max);
            try {
                selection = input.nextDouble();
            } catch (InputMismatchException e) {
                System.err.println("Only numbers!");
                input.nextLine();
            }
        } while (selection < min || selection > max);
        return selection;
    }

    public static int getMenuSelection() {

        System.out.println("\n******************************************");
        System.out.println("* 0-) exit                               *");
        System.out.println("* 1-) load WordNet into memory           *");
        System.out.println("* 2-) change calculation settings        *");
        System.out.println("* 3-) choose model for training          *");
        System.out.println("* 4-) change model's params              *");
        System.out.println("* 5-) reset model params                 *");
        System.out.println("* 6-) change corpus path for model       *");
        System.out.println("* 7-) train or load model                *");
        System.out.println("* 8-) save trained model                 *");
        System.out.println("* 9-) save calculated scores             *");
        System.out.println("* 10-) get size of words in vocab        *");
        System.out.println("* 11-) get most similar words of a word  *");
        System.out.println("* 12-) get analogy score of one word     *");
        System.out.println("* 13-) calculate analogy score           *");
        System.out.println("* 14-) calculate similarity score        *");
        System.out.println("* 15-) change level of root logger       *");
        System.out.println("* 16-) log free, total and max memory    *");
        System.out.println("* 17-) options for logging WordNet       *");
        System.out.println("******************************************");
        return UserInput.getSelectionBetween(0, 17);
    }

    public static int getPOSSelection() {

        System.out.println("\n******************");
        System.out.println("0 to cancel");
        System.out.println("1 for noun");
        System.out.println("2 for verb");
        System.out.println("3 for adjective");
        System.out.println("4 for adverb");
        System.out.println("5 for all");
        System.out.println("******************");
        return UserInput.getSelectionBetween(0, 5);
    }

    public static int getCorpusType() {

        System.out.println("\n**************************************");
        System.out.println("0 to cancel");
        System.out.println("1 to build model by training corpus");
        System.out.println("2 to use an already trained model");
        System.out.println("**************************************");
        return UserInput.getSelectionBetween(0, 2);
    }

    public static int getListingOptions() {

        System.out.println("\n*********************************");
        System.out.println("0 to cancel");
        System.out.println("1 to listPointerMap");
        System.out.println("2 to listWordsLexicalPointers");
        System.out.println("3 to listWordsSemanticPointers");
        System.out.println("4 to listNouns");
        System.out.println("5 to listVerbs");
        System.out.println("6 to listAdjectives");
        System.out.println("7 to listAdverbs");
        System.out.println("*********************************");
        return UserInput.getSelectionBetween(1, 8);
    }

    public static int getModelID() {

        System.out.println("\n****************");
        System.out.println("0 to cancel");
        System.out.println("1 for glove");
        System.out.println("2 for word2vec");
        System.out.println("****************");
        return UserInput.getSelectionBetween(0, 2);
    }

    public static String getNewPathForCorpus() {

        String selection;
        do {
            System.out.println("\nEnter a valid path for corpus file or enter 'c' to cancel.");
            selection = UserInput.getStringInput();
        } while (selection != null && (selection.isEmpty() || !FileActions.isPathValid(selection)));

        return selection;
    }

    public static String getWordInput() {

        System.out.println("\nEnter a word or enter 'c' to cancel.");
        return UserInput.getStringInput();
    }

    public static String getStringInput() {

        // clear line
        input.nextLine();
        String selection = input.nextLine();
        if ("c".equalsIgnoreCase(selection)) {
            selection = null;
        }
        return selection;
    }

    public static boolean isUserSure() {

        // clear line
        input.nextLine();

        System.out.println("\nPress 'y' if you are sure, press anything else to cancel.");
        String selection = input.nextLine();
        if ("y".equalsIgnoreCase(selection)) {
            return true;
        } else {
            return false;
        }
    }

    public static int getSettingID() {

        System.out.println("\n**********************************************************************");
        System.out.println("0 to return menu");
        System.out.println("1 for changing calculation function");
        System.out.println("2 for base value of max score, higher means " +
                "bigger gap between 1st and 2nd order");
        System.out.println("3 for total words to retrieve from model when " +
                "checking proximity of a word with a word pair");
        System.out.println("4 for iteration cap of a pointer while doing analogy test");
        System.out.println("5 for resetting iteration cap to default value");
        System.out.println("6 for resetting base sensitivity, closest word size and max score");
        System.out.println("**********************************************************************");
        return UserInput.getSelectionBetween(0, 6);
    }

    public static int getParamType() {

        System.out.println("\n*********************************************************************");
        System.out.println("0 to return menu");
        System.out.println("1 to change layer size");
        System.out.println("2 to change window size");
        System.out.println("3 to change minumum word frequency");
        System.out.println("4 to change how many threads can be utilized");
        System.out.println("5 to change epochs");
        System.out.println("6 to change batch size");
        System.out.println("7 to change learning rate");
        System.out.println("8 to change minumum learning rate");
        System.out.println("9 to change seed value");
        System.out.println("10 to change iterations (word2vec)");
        System.out.println("11 to change negative value (word2vec)");
        System.out.println("12 to change sampling value (word2vec)");
        System.out.println("13 to choose if huge model is expected (word2vec)");
        System.out.println("14 to choose negative sampling or hierarchic softmax (word2vec)");
        System.out.println("15 to choose CBOW or skip gram (word2vec)");
        System.out.println("16 to choose if shuffle is allowed (glove)");
        System.out.println("17 to choose if training is symmetric (glove)");
        System.out.println("18 to change xMax (glove)");
        System.out.println("19 to change alpha (glove)");
        System.out.println("*********************************************************************");
        return UserInput.getSelectionBetween(0, 19);
    }

    public static boolean getParamShuffle() {

        System.out.println("0 to not shuffle batches");
        System.out.println("1 to shuffle before training");
        return UserInput.getSelectionBetween(0, 1) == 1;
    }

    public static boolean getParamSymmetric() {

        System.out.println("0 to use one direction for word pairs");
        System.out.println("1 to build word pairs in both direction");
        return UserInput.getSelectionBetween(0, 1) == 1;
    }

    public static boolean getParamHugeModelExpected() {

        System.out.println("0 for normal model");
        System.out.println("1 for huge model");
        return UserInput.getSelectionBetween(0, 1) == 1;
    }

    public static boolean getParamUseHierarchicSoftmax() {

        System.out.println("0 for negative sampling");
        System.out.println("1 for hierarchic softmax");
        return UserInput.getSelectionBetween(0, 1) == 1;
    }

    public static boolean getParamSkipGramOrCBOW() {

        System.out.println("0 for CBOW");
        System.out.println("1 for skip gram");
        return UserInput.getSelectionBetween(0, 1) == 1;
    }

    public static boolean getCalculationOption() {

        System.out.println("0 for exponential calculation");
        System.out.println("1 for proportional calculation (default)");
        return UserInput.getSelectionBetween(0, 1) == 1;
    }

    public static boolean getPhraseSetting() {

        System.out.println("0 to include phrases for calculations");
        System.out.println("1 to exclude phrases for calculations (default)");
        return UserInput.getSelectionBetween(0, 1) == 1;
    }

    public static int getLogLevel() {

        System.out.println("0 to cancel");
        System.out.println("1 for debug");
        System.out.println("2 for info");
        System.out.println("3 for warn");
        return UserInput.getSelectionBetween(0, 3) * 10000;
    }

    public static boolean getAnalogyAlgorithm() {

        System.out.println("0 to calculate analogy with broader relations between words (default)");
        System.out.println("1 to calculate analogy within synsets of words");
        return UserInput.getSelectionBetween(0, 1) == 1;
    }

}

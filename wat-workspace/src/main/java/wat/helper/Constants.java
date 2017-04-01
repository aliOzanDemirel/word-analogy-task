package wat.helper;

public class Constants {

    // workers runtime'da uygun olan işlemci sayısı
    public static final boolean ALLOW_PARALLEL_TOKENIZATION = true;
    public static final boolean USE_HIERARCHIC_SOFTMAX = true;
    public static final boolean HUGE_MODEL_EXPECTED = false;
    public static final int MIN_WORD_FREQUENCY = 50;
    public static final int ITERATIONS = 1;
    public static final int EPOCHS = 1;
    public static final int LAYER_SIZE = 200;
    public static final int WINDOW_SIZE = 4;
    public static final int SEED = 24;
    public static final int BATCH_SIZE = 512;
    public static final double LEARNING_RATE = 0.025;
    public static final double MIN_LEARNING_RATE = 0.0001;
    public static final double NEGATIVE = 0.0d;
    public static final double SAMPLING = 0.0d;

    public static final int ALL_WORDS = 0;
    public static final int NOUNS_ONLY = 1;
    public static final int VERBS_ONLY = 2;
    public static final int ADJECTIVES_ONLY = 3;
    public static final int ADVERBS_ONLY = 4;

}

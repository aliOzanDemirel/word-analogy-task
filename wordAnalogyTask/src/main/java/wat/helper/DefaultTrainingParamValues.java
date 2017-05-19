package wat.helper;

public class DefaultTrainingParamValues {

    /**
     * workers runtime'da uygun olan işlemci sayısı
     * ortak parametreler
     */
    public static final int EPOCHS = 1;
    public static final int LAYER_SIZE = 300;
    /**
     * 5 is suggested for hierarchic softmax
     * 10 is suggested for negative sampling
     */
    public static final int WINDOW_SIZE = 5;
    public static final int SEED = 24;
    public static final int BATCH_SIZE = 512;
    public static final double LEARNING_RATE = 0.025;
    public static final double MIN_LEARNING_RATE = 0.0001;
    public static final int MIN_WORD_FREQUENCY = 5;
    /**
     * word2vec için özel
     */
    public static final int ITERATIONS = 1;
    public static final double NEGATIVE = 0.0d;
    public static final double SAMPLING = 0.0d;
    public static final boolean USE_HIERARCHIC_SOFTMAX = true;
    public static final boolean HUGE_MODEL_EXPECTED = false;
    /**
     * glove için özel
     * dl4j örneğinde learningRate 0.1, epochs 25, batchSize da 1000
     */
    public static final double X_MAX = 100.0d;
    public static final boolean SYMMETRIC = false;
    public static final boolean SHUFFLE = false;
    public static final double ALPHA = 0.75d;

}

package wat.helper;

public enum TrainingParamTypes {

    // user input types, not the values
    LAYER_SIZE(1),
    WINDOW_SIZE(2),
    MIN_WORD_FREQUENCY(3),
    WORKERS(4),
    EPOCHS(5),
    BATCH_SIZE(6),
    LEARNING_RATE(7),
    MIN_LEARNING_RATE(8),
    SEED(9),
    ITERATIONS_W2(10),
    NEGATIVE_W2(11),
    SAMPLING_W2(12),
    USE_HIERARCHIC_SOFTMAX_W2(13),
    USE_NEGATIVE_SAMPLING_W2(14),
    HUGE_MODEL_EXPECTED_W2(15),
    HUGE_MODEL_NOT_EXPECTED_W2(16),
    USE_CBOW_W2(17),
    USE_SKIP_GRAM_W2(18),
    SHUFFLE_G(19),
    SYMMETRIC_G(20),
    XMAX_G(21),
    ALPHA_G(22);

    int value;

    /**
     * @param value
     */
    TrainingParamTypes(int value) {

        this.value = value;
    }

}

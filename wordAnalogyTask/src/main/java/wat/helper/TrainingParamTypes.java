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
    HUGE_MODEL_EXPECTED_W2(13),
    HIERARCHIC_SOFTMAX_OR_NEGATIVE_SAMPLING_W2(14),
    CBOW_OR_SKIP_GRAM_W2(15),
    SHUFFLE_G(16),
    SYMMETRIC_G(17),
    XMAX_G(18),
    ALPHA_G(19);

    int value;

    /**
     * @param value
     */
    TrainingParamTypes(int value) {

        this.value = value;
    }

    public static TrainingParamTypes getByValue(final int intValue) {

        for (TrainingParamTypes param : TrainingParamTypes.values()) {
            if (param.value == intValue) {
                return param;
            }
        }
        throw new IllegalArgumentException(intValue + " is not a valid TrainingParamTypes!");
    }

}

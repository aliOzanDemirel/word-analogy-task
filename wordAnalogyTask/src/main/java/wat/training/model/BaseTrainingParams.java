package wat.training.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.helper.DefaultSettingValues;
import wat.helper.DefaultTrainingParamValues;

public class BaseTrainingParams {

    private static Logger log = LoggerFactory.getLogger(BaseTrainingParams.class);

    /**
     * useUnknown ve unknownElement şimdilik yok.
     * resetModel'e gerek yok gibi.
     * <p>
     * elementsLearningAlgorithm ile hangi algoritmayla
     * train edileceği tutuluyor dl4j'de, word2vec için
     * skip-gram ve cbow kullanılabilir, glove zaten kendisi
     */

    protected int workers;
    protected int minWordFrequency;
    // bütün corpus için döngü sayısı
    protected int epochs;
    // kelime matrisi = kelime sayısı * layer size (feature)
    protected int layerSize;
    // çok büyük olunca yavaşlıyor
    protected int windowSize;
    // random sayı üretmesi için
    protected int seed;
    protected int batchSize;
    protected double learningRate;
    protected double minLearningRate;

    public BaseTrainingParams() {

    }

    protected void resetCommonParams() {

        workers = Runtime.getRuntime().availableProcessors();
        epochs = DefaultTrainingParamValues.EPOCHS;
        layerSize = DefaultTrainingParamValues.LAYER_SIZE;
        windowSize = DefaultTrainingParamValues.WINDOW_SIZE;
        minWordFrequency = DefaultTrainingParamValues.MIN_WORD_FREQUENCY;
        seed = DefaultTrainingParamValues.SEED;
        batchSize = DefaultTrainingParamValues.BATCH_SIZE;
        learningRate = DefaultTrainingParamValues.LEARNING_RATE;
        minLearningRate = DefaultTrainingParamValues.MIN_LEARNING_RATE;
    }

    /**
     * no validation for seed. validates all other params and
     * sets the default value if any unreasonable value exists for a param.
     */
    public void validateCommonParams() {

        if (validateMinWordFrequency(minWordFrequency) && validateEpochs(epochs)
                && validateBatchSize(batchSize) && validateLearningRate(learningRate)
                && validateMinLearningRate(minLearningRate) && validateWindowSize(windowSize)
                && validateLayerSize(layerSize) && validateWorkers(workers)) {
            log.info("All common params are okay.");
        } else {
            log.info("At least one of common params is set to its default value.");
        }
    }

    public String toString() {

        return "workers: " + workers + ", windowSize: " + windowSize + ", layerSize: " + layerSize
                + ", epochs: " + epochs + ", minWordFrequency: " + minWordFrequency + ", batchSize: " +
                batchSize + ", learningRate: " + learningRate + ", minLearningRate: "
                + minLearningRate + ", seed: " + seed;
    }

    public int getWorkers() {

        return workers;
    }

    public void setWorkers(int workersInput) {

        if (this.validateWorkers(workersInput)) {
            this.workers = workersInput;
        }
    }

    private boolean validateWorkers(int workersInput) {

        boolean result = true;
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        if (workersInput > availableProcessors) {
            log.warn(workersInput + " processors are not available so param " +
                    "'workers' is set to: " + availableProcessors);
            this.workers = availableProcessors;
            result = false;
        }
        return result;
    }

    public int getMinWordFrequency() {

        return minWordFrequency;
    }

    public void setMinWordFrequency(int minWordFrequency) {

        if (this.validateMinWordFrequency(minWordFrequency)) {
            this.minWordFrequency = minWordFrequency;
        }
    }

    private boolean validateMinWordFrequency(int minWordFrequency) {

        boolean result = true;
        if (minWordFrequency < DefaultSettingValues.MIN_WORD_FREQUENCY_LEAST
                || minWordFrequency > DefaultSettingValues.MIN_WORD_FREQUENCY_CAP) {
            log.warn(minWordFrequency + " is invalid for 'minWordFrequency', it should be between 1 and " +
                    "2000. Default value is set: " + DefaultTrainingParamValues.MIN_WORD_FREQUENCY);
            this.minWordFrequency = DefaultTrainingParamValues.MIN_WORD_FREQUENCY;
            result = false;
        }
        return result;
    }

    public int getEpochs() {

        return epochs;
    }

    public void setEpochs(int epochs) {

        if (this.validateEpochs(epochs)) {
            this.epochs = epochs;
        }
    }

    private boolean validateEpochs(int totalIteration) {

        boolean result = true;
        if (DefaultSettingValues.EPOCHS_LEAST < 1
                || totalIteration > DefaultSettingValues.EPOCHS_CAP) {
            log.warn(totalIteration + " is invalid for 'epochs', it should be between 1 and 100. " +
                    "Default value is set: " + DefaultTrainingParamValues.EPOCHS);
            this.epochs = DefaultTrainingParamValues.EPOCHS;
            result = false;
        }
        return result;
    }

    public int getLayerSize() {

        return layerSize;
    }

    public void setLayerSize(int layerSize) {

        if (this.validateLayerSize(layerSize)) {
            this.layerSize = layerSize;
        }
    }

    private boolean validateLayerSize(int layerSize) {

        boolean result = true;
        if (DefaultSettingValues.LAYER_SIZE_LEAST < 1
                || layerSize > DefaultSettingValues.LAYER_SIZE_CAP) {
            log.warn(layerSize + " is invalid for 'layerSize', it should be between 1 and 2000. " +
                    "Default value is set: " + DefaultTrainingParamValues.LAYER_SIZE);
            this.layerSize = DefaultTrainingParamValues.LAYER_SIZE;
            result = false;
        }
        return result;
    }

    public int getWindowSize() {

        return windowSize;
    }

    public void setWindowSize(int windowSize) {

        if (this.validateWindowSize(windowSize)) {
            this.windowSize = windowSize;
        }
    }

    private boolean validateWindowSize(int windowSize) {

        boolean result = true;
        if (windowSize < DefaultSettingValues.WINDOW_SIZE_LEAST
                || windowSize > DefaultSettingValues.WINDOW_SIZE_CAP) {
            log.warn(windowSize + " is invalid for 'windowSize', it should be between 1 and 100. " +
                    "Default value is set: " + DefaultTrainingParamValues.WINDOW_SIZE);
            this.windowSize = DefaultTrainingParamValues.WINDOW_SIZE;
            result = false;
        }
        return result;
    }

    public int getBatchSize() {

        return batchSize;
    }

    public void setBatchSize(int batchSize) {

        if (this.validateBatchSize(batchSize)) {
            this.batchSize = batchSize;
        }
    }

    private boolean validateBatchSize(int batchSize) {

        boolean result = true;
        if (batchSize < DefaultSettingValues.BATCH_SIZE_LEAST
                || batchSize > DefaultSettingValues.BATCH_SIZE_CAP) {
            log.warn(batchSize + " is invalid for 'batchSize', it should be between 16 and 16384. " +
                    "Default value is set: " + DefaultTrainingParamValues.BATCH_SIZE);
            this.batchSize = DefaultTrainingParamValues.BATCH_SIZE;
            result = false;
        }
        return result;
    }

    public double getLearningRate() {

        return learningRate;
    }

    public void setLearningRate(double learningRate) {

        if (this.validateLearningRate(learningRate)) {
            this.learningRate = learningRate;
        }
    }

    private boolean validateLearningRate(double learningRate) {

        boolean result = true;
        if (learningRate < DefaultSettingValues.LEARNING_RATE_LEAST
                || learningRate > DefaultSettingValues.LEARNING_RATE_CAP) {
            log.warn(learningRate + " is invalid for 'learningRate', it should be between 0.0001 and 1. " +
                    "Default value is set: " + DefaultTrainingParamValues.LEARNING_RATE);
            this.learningRate = DefaultTrainingParamValues.LEARNING_RATE;
            result = false;
        }
        return result;
    }

    public double getMinLearningRate() {

        return minLearningRate;
    }

    public void setMinLearningRate(double minLearningRate) {

        if (this.validateMinLearningRate(minLearningRate)) {
            this.minLearningRate = minLearningRate;
        }
    }

    private boolean validateMinLearningRate(double minLearningRate) {

        boolean result = true;
        if (minLearningRate < DefaultSettingValues.MIN_LEARNING_RATE_LEAST
                || minLearningRate > DefaultSettingValues.MIN_LEARNING_RATE_CAP) {
            log.warn(minLearningRate + " is invalid for 'minLearningRate', it should be between 0.0001 " +
                    "and 1. Default value is set: " + DefaultTrainingParamValues.MIN_LEARNING_RATE);
            this.minLearningRate = DefaultTrainingParamValues.MIN_LEARNING_RATE;
            result = false;
        }
        return result;
    }

    public int getSeed() {

        return seed;
    }

    public void setSeed(int seed) {

        this.seed = seed;
    }

}

package wat.training.model;

import wat.helper.DefaultTrainingParamValues;

public class BaseTrainingParams {

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

    protected boolean validateCommonParams() {

        if (minWordFrequency < 20) {

        }
        return false;
    }

    public String toString() {

        return "workers: " + workers + ", window size: " + windowSize + ", layerSize: " + layerSize;
    }

    public int getWorkers() {

        return workers;
    }

    public void setWorkers(int workers) {

        this.workers = workers;
    }

    public int getMinWordFrequency() {

        return minWordFrequency;
    }

    public void setMinWordFrequency(int minWordFrequency) {

        this.minWordFrequency = minWordFrequency;
    }

    public int getEpochs() {

        return epochs;
    }

    public void setEpochs(int epochs) {

        this.epochs = epochs;
    }

    public int getLayerSize() {

        return layerSize;
    }

    public void setLayerSize(int layerSize) {

        this.layerSize = layerSize;
    }

    public int getWindowSize() {

        return windowSize;
    }

    public void setWindowSize(int windowSize) {

        this.windowSize = windowSize;
    }

    public int getSeed() {

        return seed;
    }

    public void setSeed(int seed) {

        this.seed = seed;
    }

    public int getBatchSize() {

        return batchSize;
    }

    public void setBatchSize(int batchSize) {

        this.batchSize = batchSize;
    }


    public double getLearningRate() {

        return learningRate;
    }

    public void setLearningRate(double learningRate) {

        this.learningRate = learningRate;
    }

    public double getMinLearningRate() {

        return minLearningRate;
    }

    public void setMinLearningRate(double minLearningRate) {

        this.minLearningRate = minLearningRate;
    }

}

package wat.model.word2vec;

import wat.helper.Constants;

public class Word2vecTrainingParams {

    // paralel şekilde kaç thread çalışabileceğini tutuyor
    // deneme yaparken bütün cpu çalışıp bilgisayar kapanmasın diye az bir sayı vermek için ekledim
    private int workers;
    private boolean allowParallelTokenization;
    // negative sampling tercihen kullanılıyor
    private boolean useHierarchicSoftmax;
    private boolean hugeModelExpected;
    private int minWordFrequency;
    // her batch için döngü sayısı
    private int iterations;
    // bütün corpus için döngü sayısı
    private int epochs;
    // kelime matrisi = kelime sayısı * layer size (feature)
    private int layerSize;
    // çok büyük olunca yavaşlıyor
    private int windowSize;
    // random sayı üretmesi için
    private int seed;
    private int batchSize;
    private double learningRate;
    private double minLearningRate;
    private double negative;
    private double sampling;

    public Word2vecTrainingParams() {
        this.reset();
    }

    /**
     * reset to default values.
     */
    public void reset() {
        workers = Runtime.getRuntime().availableProcessors();
        allowParallelTokenization = Constants.ALLOW_PARALLEL_TOKENIZATION;
        useHierarchicSoftmax = Constants.USE_HIERARCHIC_SOFTMAX;
        hugeModelExpected = Constants.HUGE_MODEL_EXPECTED;
        minWordFrequency = Constants.MIN_WORD_FREQUENCY;
        iterations = Constants.ITERATIONS;
        epochs = Constants.EPOCHS;
        layerSize = Constants.LAYER_SIZE;
        windowSize = Constants.WINDOW_SIZE;
        seed = Constants.SEED;
        batchSize = Constants.BATCH_SIZE;
        learningRate = Constants.LEARNING_RATE;
        minLearningRate = Constants.MIN_LEARNING_RATE;
        negative = Constants.NEGATIVE;
        sampling = Constants.SAMPLING;
    }

    // TODO: validate ve toString doldurulacak
    public void validate() {
        if (minWordFrequency < 20) {

        }
    }

    @Override
    public String toString() {
        return "workers: " + workers + ", window size: " + windowSize + ", layerSize: " + layerSize;
    }

    public int getMinWordFrequency() {
        return minWordFrequency;
    }

    public void setMinWordFrequency(int minWordFrequency) {
        this.minWordFrequency = minWordFrequency;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
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

    public double getNegative() {
        return negative;
    }

    public void setNegative(double negative) {
        this.negative = negative;
    }

    public double getSampling() {
        return sampling;
    }

    public void setSampling(double sampling) {
        this.sampling = sampling;
    }

    public int getWorkers() {
        return workers;
    }

    public void setWorkers(int workers) {
        this.workers = workers;
    }

    public boolean isAllowParallelTokenization() {
        return allowParallelTokenization;
    }

    public void setAllowParallelTokenization(boolean allowParallelTokenization) {
        this.allowParallelTokenization = allowParallelTokenization;
    }

    public boolean isUseHierarchicSoftmax() {
        return useHierarchicSoftmax;
    }

    public void setUseHierarchicSoftmax(boolean useHierarchicSoftmax) {
        this.useHierarchicSoftmax = useHierarchicSoftmax;
    }

    public boolean isHugeModelExpected() {
        return hugeModelExpected;
    }

    public void setHugeModelExpected(boolean hugeModelExpected) {
        this.hugeModelExpected = hugeModelExpected;
    }

}

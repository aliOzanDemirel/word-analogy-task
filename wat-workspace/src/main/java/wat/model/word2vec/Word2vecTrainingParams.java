package wat.model.word2vec;

import org.deeplearning4j.models.embeddings.learning.ElementsLearningAlgorithm;
import org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import wat.helper.DefaultTrainingParamValues;
import wat.model.BaseTrainingParams;

public class Word2vecTrainingParams extends BaseTrainingParams {

    /**
     * word2vec için hard coded:
     * trainElementsVectors = true;
     * trainSequenceVectors = false;
     * tokenizer çalışırken multithread için var,
     * gerek yok gibi buna, default true zaten
     */

    // her batch için döngü sayısı
    private int iterations;
    // negative sampling yavaş ama daha iyi
    private boolean useHierarchicSoftmax;
    private boolean hugeModelExpected;
    private double negative;
    private double sampling;
    ElementsLearningAlgorithm skipGramOrCBOW;

    public Word2vecTrainingParams() {


        this.reset();
    }

    /**
     * reset to default values.
     */
    public void reset() {

        this.resetCommonParams();

        useHierarchicSoftmax = DefaultTrainingParamValues.USE_HIERARCHIC_SOFTMAX;
        hugeModelExpected = DefaultTrainingParamValues.HUGE_MODEL_EXPECTED;
        iterations = DefaultTrainingParamValues.ITERATIONS;
        negative = DefaultTrainingParamValues.NEGATIVE;
        sampling = DefaultTrainingParamValues.SAMPLING;
        skipGramOrCBOW = new SkipGram();
    }

    // TODO: validate ve toString doldurulacak
    public boolean validate() {

        return this.validateCommonParams();
    }

    @Override
    public String toString() {

        return super.toString();
    }

    public ElementsLearningAlgorithm getSkipGramOrCBOW() {

        return skipGramOrCBOW;
    }

    /**
     * @param useSkipGram true for skip gram, false for cbow.
     */
    public void setSkipGramOrCBOW(boolean useSkipGram) {

        if (useSkipGram) {
            skipGramOrCBOW = new SkipGram();
        } else {
            skipGramOrCBOW = new CBOW();
        }
    }

    public int getIterations() {

        return iterations;
    }

    public void setIterations(int iterations) {

        this.iterations = iterations;
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
package wat.training.model.glove;

import wat.helper.DefaultTrainingParamValues;
import wat.training.model.BaseTrainingParams;

public class GloveTrainingParams extends BaseTrainingParams {

    /**
     * glove'da hard coded:
     * trainElementsVectors = true;
     * trainSequenceVectors = false;
     * useAdeGrad = true;
     * <p>
     * iterations'a gerek yok, epochs ile aynı
     * maxMemory'ye de gerek yok, xmx yeter
     */

    // if set to true, batches will be shuffled before training
    private boolean shuffle;
    // if set to true word pairs will be built in both directions, LTR and RTL
    private boolean symmetric;
    // cutoff for weighting function
    private double xMax;
    private double alpha;

    public GloveTrainingParams() {

        this.reset();
    }

    /**
     * reset to default values.
     */
    public void reset() {

        this.resetCommonParams();

        xMax = DefaultTrainingParamValues.X_MAX;
        alpha = DefaultTrainingParamValues.ALPHA;
        shuffle = DefaultTrainingParamValues.SHUFFLE;
        symmetric = DefaultTrainingParamValues.SYMMETRIC;
    }

    @Override
    public String toString() {

        return super.toString() + " xMax: " + xMax + ", alpha: " + alpha
                + ", shuffle: " + shuffle + ", symmetric: " + symmetric;
    }

    public boolean isShuffle() {

        return shuffle;
    }

    public void setShuffle(boolean shuffle) {

        this.shuffle = shuffle;
    }

    public boolean isSymmetric() {

        return symmetric;
    }

    public void setSymmetric(boolean symmetric) {

        this.symmetric = symmetric;
    }

    public double getxMax() {

        return xMax;
    }

    public void setxMax(double xMax) {

        this.xMax = xMax;
    }

    public double getAlpha() {

        return alpha;
    }

    public void setAlpha(double alpha) {

        this.alpha = alpha;
    }

}

package wat.model;

import wat.helper.Constants;

public abstract class BaseModel implements BaseModelInt {

    /**
     * raw text file if model is to built. csv, binary or
     * dl4j compressed file can be set if model is prebuilt.
     */
    protected String corpusPath = null;

    /**
     * true if corpus is not raw text but a prebuilt model.
     */
    protected boolean corpusIsTrained = false;

    /**
     * setting to hold how many words should be retrieved
     * while querying word2vec by negative&positive words
     */
    protected int closestWordSize = Constants.CLOSEST_WORD_SIZE;

    /**
     * base number to set maximum score while evaluating word2vec's accuracy. higher number means
     * exponentially bigger gap between orders of returned nearest words from word2vec.
     */
    protected int baseSensitivity = Constants.BASE_SENSITIVITY;

    public String getCorpusPath() {

        return corpusPath;
    }

    public void setCorpusPath(String corpusPath) {

        this.corpusPath = corpusPath;
    }

    public boolean isCorpusIsTrained() {

        return corpusIsTrained;
    }

    public void setCorpusIsTrained(boolean corpusIsTrained) {

        this.corpusIsTrained = corpusIsTrained;
    }

    public int getClosestWordSize() {

        return closestWordSize;
    }

    public void setClosestWordSize(int closestWordSize) {

        this.closestWordSize = closestWordSize;
    }

    public int getBaseSensitivity() {

        return baseSensitivity;
    }

    public void setBaseSensitivity(int baseSensitivity) {

        this.baseSensitivity = baseSensitivity;
    }

}

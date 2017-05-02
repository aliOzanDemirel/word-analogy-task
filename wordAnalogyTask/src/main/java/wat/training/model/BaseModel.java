package wat.training.model;

import wat.helper.DefaultSettings;

public abstract class BaseModel implements BaseModelInt {

    /**
     * raw text file if model is to built. csv, binary or
     * dl4j compressed file can be set if model is prebuilt.
     */
    protected String corpusPath = null;

    /**
     * setting to hold how many words should be retrieved
     * while querying word2vec by negative&positive words
     */
    protected int closestWordSize = DefaultSettings.CLOSEST_WORD_SIZE;

    /**
     * base number to set maximum score while evaluating word2vec's accuracy. higher number means
     * exponentially bigger gap between orders of returned nearest words from word2vec.
     */
    protected int baseSensitivity = DefaultSettings.BASE_SENSITIVITY;

    public String getCorpusPath() {

        return corpusPath;
    }

    public void setCorpusPath(String corpusPath) {

        this.corpusPath = corpusPath;
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

package wat.model.glove;

public interface GloveUtilInt {

    public void buildGloveFromCorpus(String corpusPath) throws Exception;

    public double getSimilarity(String firstWord, String secondWord);

    public boolean hasWord(String word);

    public boolean isGloveReady();

}

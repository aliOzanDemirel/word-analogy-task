package wat.model;

import wat.exceptions.ModelBuildException;

import java.util.List;

public interface BaseModelInt {

    void createModel(int corpusIsPretrained) throws ModelBuildException;

    String getCorpusPath();

    void setCorpusPath(String corpusPath);

    boolean isCorpusIsTrained();

    void setCorpusIsTrained(boolean corpusIsTrained);

    int getClosestWordSize();

    void setClosestWordSize(int closestWordSize);

    int getBaseSensitivity();

    void setBaseSensitivity(int baseSensitivity);

    boolean isModelReady();

    boolean hasWord(String word);

    int getTotalWordNumberInModelVocab();

    double getSimilarity(String firstWord, String secondWord);

    List<String> getClosestWords(List<String> positive, List<String> negative);
}

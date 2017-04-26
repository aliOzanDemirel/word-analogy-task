package wat.training.model;

import wat.exceptions.ModelBuildException;

import java.io.File;
import java.util.List;

public interface BaseModelInt {

    String getName();

    void createModel(int corpusIsPretrained) throws ModelBuildException;

    String getCorpusPath();

    void setCorpusPath(String corpusPath);

    int getClosestWordSize();

    void setClosestWordSize(int closestWordSize);

    int getBaseSensitivity();

    void setBaseSensitivity(int baseSensitivity);

    boolean isModelReady();

    boolean hasWord(String word);

    int getTotalWordNumberInModelVocab();

    boolean saveTrainedModel(File file);

    double getSimilarity(String firstWord, String secondWord);

    List<String> getClosestWords(List<String> positive, List<String> negative);

    void resetParams();
}

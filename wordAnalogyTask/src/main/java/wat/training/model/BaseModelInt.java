package wat.training.model;

import wat.exceptions.ModelBuildException;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface BaseModelInt {

    String getName();

    void createModel(int corpusIsPretrained) throws ModelBuildException;

    void setCorpusPath(String corpusPath);

    void setClosestWordSize(int closestWordSize);

    boolean isModelReady();

    boolean hasWord(String word);

    int getTotalWordSizeInVocab();

    boolean saveTrainedModel(File file);

    double getSimilarity(String firstWord, String secondWord);

    void resetParams();

    List<String> getClosestWords(List<String> positive, List<String> negative);

    List<String> getNearestWords(String word);

    int getClosestWordSize();

    BaseTrainingParams getParams();
}

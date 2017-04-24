package wat.calculator;

import wat.exceptions.ModelBuildException;

import java.util.List;

public interface AccuracyCalculatorInt {

    void resetScores();

    double getAccuracyPercentage();

    List<String> getClosestWords(final String rootWordLemma, final String relatedWordLemma,
            final String comparedWordLemma);

    void updateSimilarityAccuracy(final String firstWord, final String secondWord);

    void updateAnalogicalAccuracy(final String relatedWordLemmaOfCompared,
            final List<String> closestWords);

    boolean isModelReady();

    boolean hasWord(final String word);

    int getTotalWordNumberInModelVocab();

    void updateTrainingParams(int trainingParamType);

    void updateCorpusPath(final String corpusPath);

    void createModel(int corpusIsPretrained) throws ModelBuildException;
}

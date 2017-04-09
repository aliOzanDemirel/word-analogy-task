package wat.calculator;

import edu.mit.jwi.item.IWord;

import java.util.HashSet;

public interface AccuracyCalculatorInt {

    void resetAccuracy();

    double updateAndGetAccuracyPercentage();

    void setCorpusPath(String corpusPath);

    void updateSimilarityAccuracy(String firstWord, String secondWord);

    boolean isModelReady();

    void resetParams();

    void updateAnalogicalAccuracy(String firstReference, String secondReference, IWord wordToCheck);

}

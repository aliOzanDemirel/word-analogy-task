package wat.calculator;

public interface AccuracyCalculatorInt {

    void resetAccuracy();

    double updateAndGetAccuracyPercentage();

    void setCorpusPath(String corpusPath);

    void updateAccuracy(String firstWord, String secondWord);

    boolean isModelReady();

    void resetParams();

}

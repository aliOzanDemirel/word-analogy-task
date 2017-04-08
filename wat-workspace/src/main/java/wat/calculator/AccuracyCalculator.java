package wat.calculator;

import edu.mit.jwi.item.IWord;

import java.util.HashSet;

public abstract class AccuracyCalculator implements AccuracyCalculatorInt {

    protected double totalAccuracySum = 0.0;
    protected double accuracyPercentage = 0.0;
    protected int totalCalculations = 0;
    protected String corpusPath = null;
    // corpus type memoryde tutulup corpus path değişince sorulacak şekilde düzenlenebilir
    protected int corpusType = 0;

    public AccuracyCalculator() {

    }

    @Override
    public void resetAccuracy() {

        totalAccuracySum = 0.0;
        accuracyPercentage = 0.0;
        totalCalculations = 0;
    }

    @Override
    public double updateAndGetAccuracyPercentage() {

        accuracyPercentage = totalAccuracySum / totalCalculations;
        return accuracyPercentage;
    }

    @Override
    public void setCorpusPath(String corpusPath) {

        this.corpusPath = corpusPath;
    }

    @Override
    public abstract void updateSimilarityAccuracy(String firstWord, String secondWord);

    @Override
    public abstract boolean isModelReady();

    @Override
    public abstract void resetParams();

    @Override
    public abstract void updateAnalogicalAccuracy(String firstReference, String secondReference,
            HashSet<IWord> wordsOfPointer);

}

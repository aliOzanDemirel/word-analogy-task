package wat.calculator;

import wat.helper.Constants;

import java.util.List;

public abstract class AccuracyCalculator implements AccuracyCalculatorInt {

    protected double similarityScore = 0.0;
    protected int totalCalculations = 0;
    protected double analogyScore = 0.0;
    protected int totalAnalogicCalculations = 0;
    protected double maxScore = Math.pow(Constants.BASE_SENSITIVITY, Constants.CLOSEST_WORD_SIZE + 1);

    public AccuracyCalculator() {

    }

    @Override
    public void resetScores() {

        analogyScore = 0.0;
        totalAnalogicCalculations = 0;
        similarityScore = 0.0;
        totalCalculations = 0;
    }

    @Override
    public double getAccuracyPercentage() {

        return similarityScore / totalCalculations;
    }

    @Override
    public abstract boolean isModelReady();

    @Override
    public abstract boolean hasWord(final String word);

    @Override
    public abstract int getTotalWordNumberInModelVocab();

    @Override
    public abstract void updateTrainingParams(int trainingType);

    @Override
    public abstract void updateCorpusPath(final String corpusPath);

    @Override
    public abstract List<String> getClosestWords(final String rootWordLemma, final String relatedWordLemma,
            final String comparedWordLemma);

    @Override
    public abstract void updateSimilarityAccuracy(String firstWord, String secondWord);

    @Override
    public abstract void updateAnalogicalAccuracy(String relatedWordLemmaOfCompared,
            List<String> closestWords);


}

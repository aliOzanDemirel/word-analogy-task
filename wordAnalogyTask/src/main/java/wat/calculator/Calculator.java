package wat.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.helper.DefaultSettingValues;

import java.util.List;

public class Calculator {

    private static final Logger log = LoggerFactory.getLogger(Calculator.class);
    private static final boolean debugEnabled = log.isDebugEnabled();

    private double[] scores;
    private double analogyScore, similarityScore, maxScoreForAnalogy;
    private int totalCalculations, totalMatchForAnalogy;

    /**
     * base number to set maximum score while evaluating word2vec's accuracy. higher number means
     * exponentially bigger gap between orders of returned nearest words from word2vec.
     */
    private int sensitivity = DefaultSettingValues.BASE_SENSITIVITY;
    private boolean calculationOption = DefaultSettingValues.CALCULATE_PROPORTIONALLY;

    public Calculator() {

        this.resetProperties();
        this.prepareScoresForAnalogyTask(DefaultSettingValues.BASE_SENSITIVITY,
                DefaultSettingValues.CLOSEST_WORD_SIZE);
    }

    public void prepareScoresForAnalogyTask(int closestWordSize) {

        this.prepareScoresForAnalogyTask(sensitivity, closestWordSize);
    }

    /**
     * prepares all scores for given closestWordSize of word list to be retrieved from model.
     *
     * @param sensitivity     sets this to default value when its option is not enabled.
     * @param closestWordSize size of word list to be checked by querying model.
     */
    public void prepareScoresForAnalogyTask(int sensitivity, int closestWordSize) {

        if (calculationOption) {
            this.sensitivity = DefaultSettingValues.BASE_SENSITIVITY;
            this.prepareScoresForProportionalError(closestWordSize);
        } else {
            this.setBaseSensitivityAndMaxScore(sensitivity, closestWordSize);
        }
    }

    /**
     * this always updates max score for analogy.
     *
     * @param baseSensitivity
     * @param closestWordSize
     */
    public void setBaseSensitivityAndMaxScore(int baseSensitivity, int closestWordSize) {

        if (baseSensitivity >= 2 && baseSensitivity <= 100) {
            this.sensitivity = baseSensitivity;
            this.prepareScoresUpToSensitiveError(baseSensitivity, closestWordSize);
        }
    }

    /**
     * sets max score and calculate scores up to {{@link #sensitivity}}.
     *
     * @param baseSensitivity
     * @param closestWordSize
     */
    public void prepareScoresUpToSensitiveError(int baseSensitivity, int closestWordSize) {

        // base'in wordSize kadar üssünün 3/2'si maximum score
        maxScoreForAnalogy = 3 * Math.pow(baseSensitivity, closestWordSize) / 2;
        this.fillScoresUpToSensitivity(closestWordSize);
    }

    /**
     * least score is too low compared to first 2-3 scores, but it can also be really high
     * and can stack way more score compared to a word that does not match.
     *
     * @param closestWordSize
     */
    private void fillScoresUpToSensitivity(int closestWordSize) {

        scores = new double[closestWordSize];
        for (int i = 0; i < closestWordSize; i++) {
            scores[i] = maxScoreForAnalogy - Math.pow(sensitivity, i + 1);
            log.info((i + 1) + ". Word Score: " + scores[i]);
        }
    }

    /**
     * sets max score and calculate scores.
     *
     * @param closestWordSize
     */
    public void prepareScoresForProportionalError(int closestWordSize) {

        maxScoreForAnalogy = Math.pow(closestWordSize, 2) * 2;
        this.fillScoresProportionally(closestWordSize);
    }

    /**
     * least score is always equal to (max score + 1) / 2
     *
     * @param closestWordSize
     */
    private void fillScoresProportionally(int closestWordSize) {

        scores = new double[closestWordSize];
        for (int i = 0; i < closestWordSize; i++) {
            scores[i] = maxScoreForAnalogy - Math.pow(i + 1, 2);
            log.info((i + 1) + ". Word Score: " + scores[i]);
        }
    }

    /**
     * @param expectedWordLemma string value of compared IWord.
     * @param closestWords      words that returned from embedding model.
     */
    public void updateAnalogicalAccuracy(final String expectedWordLemma, final List<String> closestWords) {

        String wordReturnedFromModel;
        totalCalculations++;

        int closestWordSize = closestWords.size();
        for (int i = 0; i < closestWordSize; i++) {
            wordReturnedFromModel = closestWords.get(i);
            if (expectedWordLemma.equalsIgnoreCase(wordReturnedFromModel)) {
                if (debugEnabled) {
                    log.debug(expectedWordLemma + " is found in "
                            + (i + 1) + ". result from word vectors.");
                }
                analogyScore += scores[i];
                totalMatchForAnalogy++;
                // birden çok related kelime varsa, bunlardan 2. sıradaki closestWord listesinde de
                // 2. sırada olabilir ama önceden 1. sıradaki başka bir related kelimeyle closestWord
                // eşleşmişse skorda azalma olmamalı, bu yüzden match olan kelime listeden çıkarılıyor
                closestWords.remove(i);
                break;
            }
        }
    }

    /**
     * updates similarity accuracy.
     *
     * @param similarity
     */
    public void updateSimilarity(final double similarity) {

        this.similarityScore += similarity;
        this.totalCalculations++;
    }

    public void resetProperties() {

        totalMatchForAnalogy = 0;
        totalCalculations = 0;
        similarityScore = 0.0d;
        analogyScore = 0.0d;
    }

    /**
     * similarity of one word is the cosine similarity of model.
     */
    public double getSimilarityPercentage() {

        if (totalCalculations > 0.0d) {
            return 100 * similarityScore / totalCalculations;
        } else {
            return -1.0d;
        }
    }

    /**
     * percentage of score is calculated up to the max score defined.
     */
    public double getAnalogicalPercentage() {

        if (totalCalculations > 0.0d) {
            return (analogyScore / totalCalculations) * 100 / maxScoreForAnalogy;
        } else {
            return -1;
        }
    }

    public boolean getCalculationOption() {

        return calculationOption;
    }

    public void setCalculationOption(boolean calculationOption) {

        this.calculationOption = calculationOption;
    }

    public String toString() {

        final StringBuilder strBuilder = new StringBuilder("totalCalculations: " + totalCalculations
                + "\ntotalMatchForAnalogy: " + totalMatchForAnalogy
                + "\nmaxScoreForAnalogy: " + maxScoreForAnalogy
                + "\nanalogyScore: " + analogyScore
                + "\nsimilarityScore: " + similarityScore + "\nscores: ");

        for (int i = 0; i < scores.length; i++) {
            strBuilder.append((i + 1) + "-" + scores[i] + " / ");
        }

        return strBuilder.toString();
    }

}

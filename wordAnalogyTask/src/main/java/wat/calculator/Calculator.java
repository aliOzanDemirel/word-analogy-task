package wat.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.helper.DefaultSettingValues;

import java.util.List;

public class Calculator {

    private static final Logger log = LoggerFactory.getLogger(Calculator.class);
    private static final boolean debugEnabled = log.isDebugEnabled();

    private double similarityScore, analogyScore, maxScoreForAnalogy;
    private int totalSimCalculations, totalAnalogicCalculations;

    private double[] scores;
    /**
     * base number to set maximum score while evaluating word2vec's accuracy. higher number means
     * exponentially bigger gap between orders of returned nearest words from word2vec.
     */
    private int sensitivity = DefaultSettingValues.BASE_SENSITIVITY;
    private boolean calculationOption = DefaultSettingValues.CALCULATE_PROPORTIONALLY;

    public Calculator() {

        this.resetScores();
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
            log.info(i + 1 + ". Word Score: " + scores[i]);
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
            log.info(i + 1 + ". Word Score: " + scores[i]);
        }
    }

    public boolean updateAnalogicalAccuracy(final String relatedWordLemmaOfCompared,
            final List<String> closestWords) {

        totalAnalogicCalculations++;
        int closestWordSize = closestWords.size();
        for (int i = 0; i < closestWordSize; i++) {
            String wordReturnedFromW2vec = closestWords.get(i);
            if (relatedWordLemmaOfCompared.equalsIgnoreCase(wordReturnedFromW2vec)) {
                if (debugEnabled) {
                    log.debug("Related word of the compared is found in " + (i + 1)
                            + ". result from word vectors.");
                }
                // accuracy ağırlığı fark etsin diye üssü alınacak bir base koydum
                // sensitivity daha büyük de olabilir ama closestWordSize'la çok fark olmamalı
                analogyScore += scores[i];
                return true;
            }
        }
        return false;
    }

    /**
     * updates similarity accuracy.
     *
     * @param similarity
     */
    public void updateSimilarityAccuracy(final double similarity) {

        this.similarityScore += similarity;
        this.totalSimCalculations++;
    }

    public void resetScores() {

        analogyScore = 0.0;
        totalAnalogicCalculations = 0;
        similarityScore = 0.0;
        totalSimCalculations = 0;
    }

    /**
     * similarity of one word is the cosine similarity of model.
     */
    public double getSimilarityPercentage() {

        return 100 * similarityScore / totalSimCalculations;
    }

    /**
     * percentage of score is calculated up to the max score defined.
     */
    public double getAnalogicalPercentage() {

        return (analogyScore / totalAnalogicCalculations) * 100 / maxScoreForAnalogy;
    }

    public double getSimilarityScore() {

        return similarityScore;
    }

    public int getTotalSimCalculations() {

        return totalSimCalculations;
    }

    public double getAnalogyScore() {

        return analogyScore;
    }

    public int getTotalAnalogicCalculations() {

        return totalAnalogicCalculations;
    }

    public double getMaxScoreForAnalogy() {

        return maxScoreForAnalogy;
    }

    public boolean getCalculationOption() {

        return calculationOption;
    }

    public void setCalculationOption(boolean calculationOption) {

        this.calculationOption = calculationOption;
    }

    public String toString() {

        final StringBuilder strBuilder = new StringBuilder("similarity: " + similarityScore + ", " +
                "totalSimCalculations: " + totalSimCalculations + ", analogyScore: " + analogyScore
                + ", totalAnalogicCalculations: " + totalAnalogicCalculations
                + ", maxScoreForAnalogy: " + maxScoreForAnalogy + "\n");

        for (int i = 0; i < scores.length; i++) {
            strBuilder.append(i + 1 + ". Word Score: " + scores[i]);
        }

        return strBuilder.toString();
    }

}

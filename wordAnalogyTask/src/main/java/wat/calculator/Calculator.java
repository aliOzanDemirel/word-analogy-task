package wat.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.helper.DefaultSettingValues;

import java.util.List;

public class Calculator implements CalculatorInt {

    private static final Logger log = LoggerFactory.getLogger(Calculator.class);
    private static final boolean debugEnabled = log.isDebugEnabled();

    private double similarityScore, analogyScore, maxScoreForAnalogy;
    private int totalSimCalculations, totalAnalogicCalculations;

    /**
     * base number to set maximum score while evaluating word2vec's accuracy. higher number means
     * exponentially bigger gap between orders of returned nearest words from word2vec.
     */
    private int baseSensitivity = DefaultSettingValues.BASE_SENSITIVITY;

    public Calculator() {

        this.resetScores();
        this.setBaseSensitivity(DefaultSettingValues.BASE_SENSITIVITY,
                DefaultSettingValues.CLOSEST_WORD_SIZE);
    }

    @Override
    public void resetScores() {

        analogyScore = 0.0;
        totalAnalogicCalculations = 0;
        similarityScore = 0.0;
        totalSimCalculations = 0;
    }

    /**
     * similarity of one word is the cosine similarity of model.
     */
    @Override
    public double getSimilarityPercentage() {

        return 100 * similarityScore / totalSimCalculations;
    }

    /**
     * percentage of score is calculated up to the max score defined.
     */
    @Override
    public double getAnalogicalPercentage() {

        return (analogyScore / totalAnalogicCalculations) * 100 / maxScoreForAnalogy;
    }

    /**
     * @param relatedWordLemmaOfCompared
     * @param closestWords
     */
    @Override
    public void updateAnalogicalAccuracy(final String relatedWordLemmaOfCompared,
            final List<String> closestWords) {

        // mesela king queen man gönderince woman gelirse ve man'in her related kelimesi için
        // dönen sonuçta bu kelime var mı diye kontrol edilecek ve eğer bu kelime dönen listede
        // ilk elemansa accuracy ağırlığı daha fazla olmalı
        double errorMargin = 0.0;
        // accuracy ağırlığı fark etsin diye üssü alınacak bir base koydum
        // baseSensitivity daha büyük de olabilir ama closestWordSize'la çok fark olmamalı
        //closestWordSize kadar elemanla eşleşme yoksa skora ekleme yapma

        // burada sıkıntı var üstteki döngüde birden çok related kelime olabilir ve
        // bunlardan biri önceden gelen kelimelerin ilkinde bulunmuş olabilir
        // bu durumda 2. sıradakiyle de aynı olsa kıyaslanan kelime, tam skor almalı

        // TODO: ilkinde bulma ile ikincide bulma arasındaki fark artsın diye ama yine ufak fark
        int closestWordSize = closestWords.size();
        for (int i = 0; i < closestWordSize; i++) {
            String wordReturnedFromW2vec = closestWords.get(i);
            if (relatedWordLemmaOfCompared.equalsIgnoreCase(wordReturnedFromW2vec)) {
                if (debugEnabled) {
                    log.debug("Related word of the compared is found in " + (i + 1)
                            + ". result from word vectors.");
                }
                errorMargin = Math.pow(baseSensitivity, i + 1);
                analogyScore += maxScoreForAnalogy - errorMargin;
                break;
            }
        }
        totalAnalogicCalculations++;
    }

    /**
     * updates similarity accuracy.
     *
     * @param similarity
     */
    @Override
    public void updateSimilarityAccuracy(final double similarity) {

        this.similarityScore += similarity;
        this.totalSimCalculations++;
    }

    @Override
    public String toString() {

        return "similarity: " + similarityScore + ", totalSimCalculations: "
                + totalSimCalculations + ", analogyScore: " + analogyScore
                + ", totalAnalogicCalculations: " + totalAnalogicCalculations
                + ", maxScoreForAnalogy: " + maxScoreForAnalogy;
    }

    @Override
    public double getSimilarityScore() {

        return similarityScore;
    }

    @Override
    public int getTotalSimCalculations() {

        return totalSimCalculations;
    }

    @Override
    public double getAnalogyScore() {

        return analogyScore;
    }

    @Override
    public int getTotalAnalogicCalculations() {

        return totalAnalogicCalculations;
    }

    @Override
    public double getMaxScoreForAnalogy() {

        return maxScoreForAnalogy;
    }

    @Override
    public int getBaseSensitivity() {

        return baseSensitivity;
    }

    /**
     * this always updates max score for analogy.
     *
     * @param baseSensitivity
     * @param closestWordSize
     */
    @Override
    public void setBaseSensitivity(int baseSensitivity, int closestWordSize) {

        if (baseSensitivity >= 2 && baseSensitivity <= 100) {
            this.baseSensitivity = baseSensitivity;
            this.setMaxScoreForAnalogy(baseSensitivity, closestWordSize);
        }
    }

    /**
     * it is used to reset max score and base sensitivity.
     *
     * @param baseSensitivity
     * @param closestWordSize
     */
    @Override
    public void setMaxScoreForAnalogy(int baseSensitivity, int closestWordSize) {

        this.baseSensitivity = baseSensitivity;
        // buradaki 3 arttırılarak fark arttırılabilir
        // base'in üssünün 3/2'si maximum score
        // base 4, size 5 olunca: 1536 max -> 5. 512 / 4. 768 / 3. 960
        // base 5, size 5 olunca: 4688 max -> 5. 1563 / 4. 4063 / 3. 4563
        maxScoreForAnalogy = 3 * Math.pow(baseSensitivity, closestWordSize) / 2;
    }

}

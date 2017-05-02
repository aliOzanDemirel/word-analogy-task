package wat.calculator;

import wat.helper.DefaultSettings;

import java.util.List;

public class Calculator implements CalculatorInt {

    private double similarityScore, analogyScore, maxScoreForAnalogy;
    private int totalSimCalculations, totalAnalogicCalculations;
    private int baseSensitivity, closestWordSize;

    public Calculator() {

        this.resetScores();
        this.resetMaxScoreForAnalogy();
    }

    @Override
    public void resetScores() {

        analogyScore = 0.0;
        totalAnalogicCalculations = 0;
        similarityScore = 0.0;
        totalSimCalculations = 0;
    }

    @Override
    public void resetMaxScoreForAnalogy() {

        baseSensitivity = DefaultSettings.BASE_SENSITIVITY;
        closestWordSize = DefaultSettings.CLOSEST_WORD_SIZE;
        this.setMaxScore();
    }

    private void setMaxScore() {
        // buradaki 3 arttırılarak fark arttırılabilir
        // base'in üssünün 3/2'si maximum score
        // base 4, size 5 olunca: 1536 max -> 5. 512 / 4. 768 / 3. 960
        // base 5, size 5 olunca: 4688 max -> 5. 1563 / 4. 4063 / 3. 4563
        maxScoreForAnalogy = 3 * Math.pow(baseSensitivity, closestWordSize) / 2;
    }

    /**
     * since similarity is the same as word2vec's cosine similarity,
     * it returns similarityScore / totalSimCalculations as score.
     */
    @Override
    public double getSimilarityPercentage() {

        return similarityScore / totalSimCalculations;
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
            if (relatedWordLemmaOfCompared.equals(wordReturnedFromW2vec)) {
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
    public void setBaseSensitivity(int baseSensitivity) {

        if (baseSensitivity >= 2 && baseSensitivity <= 100) {
            this.baseSensitivity = baseSensitivity;
            this.setMaxScore();
        }
    }

    @Override
    public void setClosestWordSize(int closestWordSize) {

        if (closestWordSize >= 3 && closestWordSize <= 100) {
            this.closestWordSize = closestWordSize;
            this.setMaxScore();
        }
    }
}

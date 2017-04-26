package wat.calculator;

import wat.helper.Constants;

import java.util.List;

public class Calculator implements CalculatorInt {

    private double similarityScore = 0.0;
    private int totalSimCalculations = 0;
    private double analogyScore = 0.0;
    private int totalAnalogicCalculations = 0;
    private double maxScoreForAnalogy = Math.pow(Constants.BASE_SENSITIVITY,
            Constants.CLOSEST_WORD_SIZE + 1);

    public Calculator() {

    }

    @Override
    public void resetScores() {

        analogyScore = 0.0;
        totalAnalogicCalculations = 0;
        similarityScore = 0.0;
        totalSimCalculations = 0;
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

    @Override
    public void updateAnalogicalAccuracy(String relatedWordLemmaOfCompared, List<String> closestWords) {

        // mesela king queen man gönderince woman gelirse ve man'in her related kelimesi için
        // dönen sonuçta bu kelime var mı diye kontrol edilecek ve eğer bu kelime dönen listede
        // ilk elemansa accuracy ağırlığı daha fazla olmalı
        double errorMargin = 0.0;
        // accuracy ağırlığı fark etsin diye üssü alınacak bir base koydum
        // baseSensitivity daha büyük de olabilir ama closestWordSize'la çok fark olmamalı
        //closestWordSize kadar elemanla eşleşme yoksa skora ekleme yapma


        // burada sıkıntı var üstteki döngüde birden çok related kelime olabilir ve bunlardan biri önceden
        // gelen kelimelerin ilkinde bulunmuş olabilir bu durumda 2. sıradakiyle de aynı olsa kıyaslanan
        // kelime, tam skor almalı
        int closestWordSize = closestWords.size();
        for (int i = 0; i < closestWordSize; i++) {
            String wordReturnedFromW2vec = closestWords.get(i);
            if (relatedWordLemmaOfCompared.equals(wordReturnedFromW2vec)) {
                errorMargin = Math.pow(4, i + 1);
                analogyScore += maxScoreForAnalogy - errorMargin;
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
}

package wat.calculator;

import java.util.List;

public interface CalculatorInt {

    void resetScores();

    double getSimilarityPercentage();

    double getAnalogicalPercentage();

    void updateSimilarityAccuracy(final double similarity);

    void updateAnalogicalAccuracy(final String relatedWordLemmaOfCompared,
            final List<String> closestWords);

    double getSimilarityScore();

    int getTotalSimCalculations();

    double getAnalogyScore();

    int getTotalAnalogicCalculations();

    double getMaxScoreForAnalogy();

    void setMaxScoreForAnalogy(int baseSensitivity, int closestWordSize);

    int getBaseSensitivity();

    void setBaseSensitivity(int baseSensitivity, int closestWordSize);

}

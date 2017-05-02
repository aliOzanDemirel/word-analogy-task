package wat.calculator;

import java.util.List;

public interface CalculatorInt {

    void resetScores();

    void resetMaxScoreForAnalogy();

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

    void setClosestWordSize(int closestWordSize);

    void setBaseSensitivity(int baseSensitivity);
}

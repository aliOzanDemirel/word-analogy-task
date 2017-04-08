package wat.calculator;

import edu.mit.jwi.item.IWord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.model.glove.GloveUtil;
import wat.model.glove.GloveUtilInt;

import java.util.HashSet;

public class GloveCalculator extends AccuracyCalculator implements GloveCalculatorInt {

    //    private static Logger log = LoggerFactory.getLogger(GloveCalculator.class);
    private GloveUtilInt gloveUtil = new GloveUtil();

    public GloveCalculator() {

    }

    @Override
    public void createGlove(int corpusType) throws Exception {

        gloveUtil.buildGloveFromCorpus(this.corpusPath);
    }

    /**
     * updates accuracy by using word2vec's cosine similarity.
     *
     * @param firstWord
     * @param secondWord
     */
    @Override
    public void updateSimilarityAccuracy(String firstWord, String secondWord) {

        if (this.bothWordsExist(firstWord, secondWord)) {
            this.totalAccuracySum += gloveUtil.getSimilarity(firstWord, secondWord);
            this.totalCalculations++;
        }
    }

    private boolean bothWordsExist(String firstWord, String secondWord) {

        return gloveUtil.hasWord(firstWord) && gloveUtil.hasWord(secondWord);
    }

    @Override
    public boolean isModelReady() {

        return gloveUtil.isGloveReady();
    }

    @Override
    public void resetParams() {

    }

    @Override
    public void updateAnalogicalAccuracy(String firstReference, String secondReference, HashSet<IWord>
            iWords) {

    }

}

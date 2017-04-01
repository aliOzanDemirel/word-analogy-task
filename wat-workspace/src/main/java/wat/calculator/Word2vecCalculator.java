package wat.calculator;

import wat.exceptions.VocabularyBuildException;
import wat.exceptions.Word2vecBuildException;
import wat.model.wor2vec.Word2vecTrainingParams;
import wat.model.wor2vec.Word2vecUtil;
import wat.model.wor2vec.Word2vecUtilInt;

public class Word2vecCalculator extends AccuracyCalculator implements Word2vecCalculatorInt {

    //    private static Logger log = LoggerFactory.getLogger(Word2vecCalculator.class);
    private Word2vecUtilInt w2vecUtil = new Word2vecUtil();
    private Word2vecTrainingParams params = new Word2vecTrainingParams();

    public Word2vecCalculator() {
    }

    @Override
    public void prepareWord2vec(int corpusType) throws Word2vecBuildException,
            VocabularyBuildException {
        w2vecUtil.createWord2vec(this.corpusPath, corpusType, params);
    }

    /**
     * updates accuracy by using word2vec's cosine similarity.
     *
     * @param firstWord
     * @param secondWord
     */
    @Override
    public void updateAccuracy(String firstWord, String secondWord) {
        if (this.bothWordsExist(firstWord, secondWord)) {
            this.totalAccuracySum += w2vecUtil.getSimilarity(firstWord, secondWord);
            this.totalCalculations++;
        }
    }

    private boolean bothWordsExist(String firstWord, String secondWord) {
        return w2vecUtil.hasWord(firstWord) && w2vecUtil.hasWord(secondWord);
    }

    @Override
    public boolean isModelReady() {
        return w2vecUtil.isWord2vecReady();
    }

    @Override
    public Word2vecTrainingParams getWord2vecParams() {
        return params;
    }

    @Override
    public void resetParams() {

    }


}

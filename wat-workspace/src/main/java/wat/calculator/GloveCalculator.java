package wat.calculator;

import wat.exceptions.ModelBuildException;
import wat.model.glove.GloveUtil;
import wat.model.glove.GloveUtilInt;

import java.util.List;

public class GloveCalculator extends AccuracyCalculator implements GloveCalculatorInt {

    //    private static Logger log = LoggerFactory.getLogger(GloveCalculator.class);
    private GloveUtilInt gloveUtil = new GloveUtil();

    public GloveCalculator() {

    }

    @Override
    public boolean hasWord(String word) {

        return false;
    }

    @Override
    public void createGlove(int corpusType) throws ModelBuildException {

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
            this.similarityScore += gloveUtil.getSimilarity(firstWord, secondWord);
            this.totalCalculations++;
        }
    }

    @Override
    public void updateAnalogicalAccuracy(String relatedWordLemmaOfCompared, List<String> closestWords) {

    }

    @Override
    public void updateTrainingParams(int trainingParamType) {

    }

    @Override
    public void updateCorpusPath(final String corpusPath) {

    }

    @Override
    public void createModel(int corpusIsPretrained) throws ModelBuildException {

    }

    private boolean bothWordsExist(String firstWord, String secondWord) {

        return gloveUtil.hasWord(firstWord) && gloveUtil.hasWord(secondWord);
    }

    @Override
    public boolean isModelReady() {

        return gloveUtil.isModelReady();
    }

    /**
     * @return the number of the words in vocab cache of word2vec.
     */
    @Override
    public int getTotalWordNumberInModelVocab() {

        return 0;
    }

    @Override
    public List<String> getClosestWords(String rootWordLemma, String relatedWordLemma, String
            comparedWordLemma) {

        return null;
    }

}

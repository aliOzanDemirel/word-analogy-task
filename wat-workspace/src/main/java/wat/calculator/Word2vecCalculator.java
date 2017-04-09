package wat.calculator;

import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import wat.exceptions.VocabularyBuildException;
import wat.exceptions.Word2vecBuildException;
import wat.model.word2vec.Word2vecTrainingParams;
import wat.model.word2vec.Word2vecUtil;
import wat.model.word2vec.Word2vecUtilInt;

import java.util.HashSet;
import java.util.List;

public class Word2vecCalculator extends AccuracyCalculator implements Word2vecCalculatorInt {

    private Word2vecUtilInt w2vecUtil = new Word2vecUtil();
    private Word2vecTrainingParams params = new Word2vecTrainingParams();

    public Word2vecCalculator() {

    }

    @Override
    public void prepareWord2vec(int corpusType) throws Word2vecBuildException,
            VocabularyBuildException {

        w2vecUtil.createWord2vec(this.corpusPath, corpusType, params);
    }

    @Override
    public void updateAnalogicalAccuracy(String firstReference, String secondReference,
            IWord wordToCheck) {

        final List<IWordID> relatedWords = wordToCheck.getRelatedWords();


        // mesela king queen man gönderince woman gelirse ve man'in her related kelimesi için
        // dönen sonuçta bu kelime var mı diye kontrol edilecek ve eğer bu kelime dönen listede
        // ilk elemansa accuracy ağırlığı daha fazla olmalı


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

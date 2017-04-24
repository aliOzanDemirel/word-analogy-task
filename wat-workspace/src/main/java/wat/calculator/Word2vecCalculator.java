package wat.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.exceptions.ModelBuildException;
import wat.main.UserInput;
import wat.model.word2vec.Word2vecUtil;
import wat.model.word2vec.Word2vecUtilInt;

import java.util.Arrays;
import java.util.List;

public class Word2vecCalculator extends AccuracyCalculator implements Word2vecCalculatorInt {

    private static final Logger log = LoggerFactory.getLogger(Word2vecCalculator.class);
    private Word2vecUtilInt word2vecUtil = new Word2vecUtil();

    public Word2vecCalculator() {

    }

    @Override
    public void createModel(int corpusIsPretrained) throws ModelBuildException {

        word2vecUtil.createModel(corpusIsPretrained);
    }

    @Override
    public boolean hasWord(String word) {

        return word2vecUtil.hasWord(word);
    }

    @Override
    public int getTotalWordNumberInModelVocab() {

        return word2vecUtil.getTotalWordNumberInModelVocab();
    }

    @Override
    public boolean isModelReady() {

        return word2vecUtil.isModelReady();
    }

    @Override
    public void updateTrainingParams(int trainingParamType) {

        switch (trainingParamType) {
//            case Word2vecParamType.WORKERS:
//                int workers = UserInput.getSelectionBetween(1, 8);
//                int availableProcessors = Runtime.getRuntime().availableProcessors();
//                if (workers > availableProcessors) {
//                    log.info(workers + " processors are not available so param 'workers' is set to: " +
//                            availableProcessors);
//                    workers = availableProcessors;
//                }
//                word2vecUtil.getWord2vecParams().setWorkers(workers);
//                break;
//            case Word2vecParamType.MIN_WORD_FREQUENCY:
//                break;
            default:
                log.warn("Wrong word2vec param type: " + trainingParamType);
        }
    }

    @Override
    public void updateCorpusPath(final String corpusPath) {

        word2vecUtil.setCorpusPath(corpusPath);

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
                this.analogyScore += this.maxScore - errorMargin;
            }
        }
        this.totalAnalogicCalculations++;
    }

    /**
     * updates similarity accuracy by using word2vec's cosine similarity.
     *
     * @param firstWord
     * @param secondWord
     */
    @Override
    public void updateSimilarityAccuracy(String firstWord, String secondWord) {

        if (word2vecUtil.hasWord(firstWord) && word2vecUtil.hasWord(secondWord)) {
            this.similarityScore += word2vecUtil.getSimilarity(firstWord, secondWord);
            this.totalCalculations++;
        }
    }

    @Override
    public List<String> getClosestWords(String rootWordLemma, String relatedWordLemma, String lemma) {

        return word2vecUtil.getClosestWords(Arrays.asList(rootWordLemma, relatedWordLemma),
                Arrays.asList(lemma));
    }

}

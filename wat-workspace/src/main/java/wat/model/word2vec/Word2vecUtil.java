package wat.model.word2vec;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.exceptions.VocabularyBuildException;
import wat.exceptions.Word2vecBuildException;
import wat.helper.ModelType;

import java.util.Collection;
import java.util.List;

public class Word2vecUtil implements Word2vecUtilInt {

    private static Logger log = LoggerFactory.getLogger(Word2vecUtil.class);
    private static boolean debugEnabled = log.isDebugEnabled();
    private Word2Vec word2vec = null;

    public Word2vecUtil() {

    }

    /**
     * @param corpusPath
     * @param corpusType 1 to train text file, 2 to use already trained model.
     * @throws Word2vecBuildException
     * @throws VocabularyBuildException
     */
    @Override
    public void createWord2vec(String corpusPath, int corpusType, Word2vecTrainingParams params) throws
            Word2vecBuildException, VocabularyBuildException {

        if (corpusType == ModelType.CORPUS_IS_PRETRAINED) {
            this.loadPretrainedModel(corpusPath);
        } else if (corpusType == ModelType.TRAIN_CORPUS) {
            this.buildWord2vecAfterCheckingParams(corpusPath, params);
        }
    }

    private void buildWord2vecAfterCheckingParams(String corpusPath, Word2vecTrainingParams params) throws
            Word2vecBuildException,
            VocabularyBuildException {

        params.validate();

        if (corpusPath == null || corpusPath.isEmpty()) {
            corpusPath = System.getenv("DEFAULT_CORPUS_PATH");
            log.warn("Corpus path is empty, setting to default: " + corpusPath);
        }

        SentenceIterator sentenceIterator;
        try {
            sentenceIterator = new BasicLineIterator(corpusPath);
        } catch (Exception e) {
            throw new Word2vecBuildException("SentenceIterator cannot be created. Corpus path may " +
                    "be wrong: " + corpusPath);
        }

        // tokenların cümle olarak neyi aldığını belirlemek gerekebilir default olarak line
        TokenizerFactory tokenizer = new DefaultTokenizerFactory();
        tokenizer.setTokenPreProcessor(new CommonPreprocessor());

        this.buildWord2vec(corpusPath, params, sentenceIterator, tokenizer);
    }

    private void buildWord2vec(String corpusPath, Word2vecTrainingParams params, SentenceIterator
            sentenceIterator, TokenizerFactory tokenizer) throws Word2vecBuildException,
            VocabularyBuildException {

        log.info("Building word2Vec model may take a while. Parameters: " + params.toString());
        word2vec = new Word2Vec.Builder()
                .useHierarchicSoftmax(params.isUseHierarchicSoftmax())
                .minWordFrequency(params.getMinWordFrequency())
                .windowSize(params.getWindowSize())
                .layerSize(params.getLayerSize())
                .iterations(params.getIterations())
                .epochs(params.getEpochs())
                .seed(params.getSeed())
                .learningRate(params.getLearningRate())
                .minLearningRate(params.getMinLearningRate())
                .iterate(sentenceIterator)
                .tokenizerFactory(tokenizer)
                .allowParallelTokenization(params.isAllowParallelTokenization())
                .workers(params.getWorkers())
                .negativeSample(params.getNegative())
                .sampling(params.getSampling())
                .build();

        word2vec.fit();
        log.info("Done fitting word2Vec model.");
    }

    // csv, binary ve dl4j compressed yüklüyor
    // loadStaticModel word vectorlere erişmek için sadece
    private void loadPretrainedModel(String corpusPath) {

        log.info("Starting to load word2vec from file: " + corpusPath + " This may take a while.");
        try {
            log.warn("" + Runtime.getRuntime().freeMemory());
            word2vec = WordVectorSerializer.readWord2VecModel(corpusPath);
        } catch (Throwable e) {
            log.error("Error occured while loading pretrained model!", e);
        }
        log.info("Done loading word2Vec model");
    }

    // model build edilirken ilk yapılan şey vocabulary yaratmak yani zaten hazırlanıyor
    private void buildVocabulary() throws VocabularyBuildException {

        word2vec.buildVocab();
        VocabCache<VocabWord> vocab = word2vec.getVocab();
        if (vocab == null) {
            throw new VocabularyBuildException("Couldn't build vocabulary for word2vec model!");
        } else {
            log.info("Done building vocabulary.");
        }
    }

    @Override
    public double getSimilarity(String firstWord, String secondWord) {

        double result = word2vec.similarity(firstWord, secondWord);
        if (debugEnabled) {
            log.debug("Similarity between " + firstWord + " - " + secondWord + ": " + result);
        }
        return result;
    }

    /**
     * liste halinde stringleri yollayınca son eleman için bir accuracy yolluyor test edilmeli
     *
     * @param questions
     */
    @Override
    public void getAccuracy(List<String> questions) {

        word2vec.accuracy(questions);
    }

    @Override
    public Collection<String> getNearestWords(String word, int number) {

        return word2vec.wordsNearest(word, number);
    }

    public Collection<String> koray(List<String> positives, List<String> negatives, int size) {

        return word2vec.wordsNearest(positives, negatives, size);
    }

    @Override
    public boolean hasWord(String word) {

        boolean result = word2vec.hasWord(word);
        if (!result) {
//            log.warn(word + " does not exist in word2vec model.");
        }
        return result;
    }

    @Override
    public boolean isWord2vecReady() {

        if (word2vec == null) {
            return false;
        } else {
            return true;
        }
    }


}

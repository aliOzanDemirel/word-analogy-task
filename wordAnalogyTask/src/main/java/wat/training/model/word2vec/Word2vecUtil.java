package wat.training.model.word2vec;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.exceptions.ModelBuildException;
import wat.helper.Constants;
import wat.training.model.BaseModel;
import wat.training.model.BaseModelInt;

import java.io.File;
import java.util.List;

public class Word2vecUtil extends BaseModel implements BaseModelInt {

    private static Logger log = LoggerFactory.getLogger(Word2vecUtil.class);
    private static boolean debugEnabled = log.isDebugEnabled();

    private Word2vecTrainingParams params = new Word2vecTrainingParams();
    private Word2Vec word2vec = null;

    public Word2vecUtil() {

    }

    @Override
    public String getName() {

        return "word2vec";
    }

    /**
     * @param corpusIsPretrained 1 to train text file, 2 to use already trained model.
     * @throws ModelBuildException
     */
    @Override
    public void createModel(int corpusIsPretrained) throws ModelBuildException {

        if (corpusIsPretrained == Constants.CORPUS_IS_PRETRAINED) {
            this.loadPretrainedModel();
        } else if (corpusIsPretrained == Constants.TRAIN_CORPUS) {
            this.buildWord2vecAfterCheckingParams();
        }
    }

    private void buildWord2vecAfterCheckingParams() throws ModelBuildException {

        params.validateCommonParams();

        this.validateCorpusPath();

        SentenceIterator sentenceIterator;
        try {
            sentenceIterator = new BasicLineIterator(corpusPath);
        } catch (Exception e) {
            throw new ModelBuildException("SentenceIterator could not be created."
                    + " Corpus path may be wrong: " + corpusPath);
        }

        // tokenların cümle olarak neyi aldığını belirlemek gerekebilir default olarak line
        TokenizerFactory tokenizer = new DefaultTokenizerFactory();
        tokenizer.setTokenPreProcessor(new CommonPreprocessor());

        this.buildWord2vec(sentenceIterator, tokenizer);
    }

    private void buildWord2vec(SentenceIterator sentenceIterator,
            TokenizerFactory tokenizer) throws ModelBuildException {

        log.info("Building word2vec with parameters: " + params.toString());
        word2vec = null;

        word2vec = new Word2Vec.Builder()
                .iterate(sentenceIterator)
                .tokenizerFactory(tokenizer)
                .minWordFrequency(params.getMinWordFrequency())
                .windowSize(params.getWindowSize())
                .layerSize(params.getLayerSize())
                .iterations(params.getIterations())
                .epochs(params.getEpochs())
                .seed(params.getSeed())
                .learningRate(params.getLearningRate())
                .minLearningRate(params.getMinLearningRate())
                .workers(params.getWorkers())
                .negativeSample(params.getNegative())
                .sampling(params.getSampling())
                // skip-gram or cbow
                .elementsLearningAlgorithm(params.getSkipGramOrCBOW())
                .useHierarchicSoftmax(params.isUseHierarchicSoftmax())
                .build();

        long start = System.currentTimeMillis();
        try {
            word2vec.fit();
        } catch (OutOfMemoryError e) {
            // release the memory if it could not be built properly
            word2vec = null;
            throw new ModelBuildException(e);
        }
        log.info("Done building word2vec model in "
                + (System.currentTimeMillis() - start) / 1000 + " seconds.");
    }

    // csv, binary ve dl4j compressed yüklüyor
    // loadStaticModel word vectorlere erişmek için sadece
    private void loadPretrainedModel() throws ModelBuildException {

        this.validateCorpusPath();

        log.info("Starting to load word2vec from: " + corpusPath);
        word2vec = null;

        long start = System.currentTimeMillis();
        try {
            // extendedModel: true olarak okusun
            word2vec = WordVectorSerializer.readWord2VecModel(new File(corpusPath),
                    true);
        } catch (OutOfMemoryError e) {
            // release the memory if it could not be loaded properly
            word2vec = null;
            throw new ModelBuildException(e);
        }
        log.info("Done loading word2vec model in "
                + (System.currentTimeMillis() - start) / 1000 + " seconds.");
    }

    /**
     * @param file represents a folder to save compressed model file in.
     * @return false if model could not be written to file.
     */
    @Override
    public boolean saveTrainedModel(File file) {

        try {
            WordVectorSerializer.writeWord2VecModel(word2vec, file);
            return true;
        } catch (Exception e) {
            log.error("Cannot find the directory: " + file.getAbsolutePath(), e);
            return false;
        }
    }

    /**
     * modifies corpus path if it is not a valid path.
     */
    private void validateCorpusPath() throws ModelBuildException {

        if (corpusPath == null || corpusPath.isEmpty()) {
            corpusPath = System.getenv("DEFAULT_CORPUS_PATH");
            if (corpusPath == null || corpusPath.isEmpty()) {
                throw new ModelBuildException("DEFAULT_CORPUS_PATH is not set!");
            }
            log.warn("Setting corpus path to default: " + corpusPath);
        }
    }

    /**
     * @return the number of the words in vocab cache of word2vec.
     */
    @Override
    public int getTotalWordSizeInVocab() {

        return word2vec.getVocab().numWords();
    }

    @Override
    public boolean hasWord(String word) {

        if (word2vec.hasWord(word)) {
            return true;
        } else {
            if (debugEnabled) {
                log.debug(word + " does not exist in word2vec model.");
            }
            return false;
        }
    }

    @Override
    public boolean isModelReady() {

        if (word2vec == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void resetParams() {

        params.reset();
        log.info("Parameters are reset to default values.\n" + params.toString());
    }

    /**
     * @return word2vec params for building model.
     */
    @Override
    public Word2vecTrainingParams getParams() {

        return params;
    }

    @Override
    public List<String> getClosestWords(List<String> positive, List<String> negative) {

        return (List<String>) word2vec.wordsNearest(positive, negative, closestWordSize);
    }

    /**
     * calls {@link Word2Vec#similarity(String, String)}. this method may return NaN if any of given words
     * does not exist in vocabulary.
     *
     * @param firstWord
     * @param secondWord
     * @return cosine value [-1,1] for proximity if both words exist.
     */
    @Override
    public double getSimilarity(final String firstWord, final String secondWord) {

        String message;
        double result;
        try {
            result = word2vec.similarity(firstWord, secondWord);
            message = firstWord + " - " + secondWord + " - similarity: " + result;
        } catch (Exception e) {
            result = Double.NaN;
            message = firstWord + " or " + secondWord + " does not exist in vocab!";
        }
        if (debugEnabled) {
            log.debug(message);
        }
        return result;
    }

    @Override
    public List<String> getNearestWords(final String word) {

        return (List<String>) word2vec.wordsNearest(word, closestWordSize);
    }

}

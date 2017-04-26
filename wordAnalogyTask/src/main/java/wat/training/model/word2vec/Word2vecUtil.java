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

import java.io.*;
import java.util.Collection;
import java.util.List;

public class Word2vecUtil extends BaseModel implements Word2vecUtilInt {

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

        this.createWord2vec(this.corpusPath, corpusIsPretrained, params);
    }

    private void createWord2vec(String corpusPath, int corpusIsPretrained, Word2vecTrainingParams params)
            throws ModelBuildException {

        if (corpusIsPretrained == Constants.CORPUS_IS_PRETRAINED) {
            this.loadPretrainedModel(corpusPath);
        } else if (corpusIsPretrained == Constants.TRAIN_CORPUS) {
            this.buildWord2vecAfterCheckingParams(corpusPath, params);
        }
    }

    private void buildWord2vecAfterCheckingParams(String corpusPath, Word2vecTrainingParams params)
            throws ModelBuildException {

        params.validate();

        if (corpusPath == null || corpusPath.isEmpty()) {
            corpusPath = System.getenv("DEFAULT_CORPUS_PATH");
            if (corpusPath == null || corpusPath.isEmpty()) {
                throw new ModelBuildException("DEFAULT_CORPUS_PATH is not set!");
            }
            log.warn("Setting corpus path to default: " + corpusPath);
        }

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

        this.buildWord2vec(params, sentenceIterator, tokenizer);
    }

    private void buildWord2vec(Word2vecTrainingParams params, SentenceIterator sentenceIterator,
            TokenizerFactory tokenizer) throws ModelBuildException {

        log.info("Building word2vec may take a while. Parameters: " + params.toString());
        word2vec = null;

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
                .workers(params.getWorkers())
                .negativeSample(params.getNegative())
                .sampling(params.getSampling())
                // skip-gram veya cbow
                .elementsLearningAlgorithm(params.getSkipGramOrCBOW())
                .build();

        long start = System.nanoTime();
        word2vec.fit();
        log.info("Done building word2vec model in " + (System.nanoTime() - start) / 60000 + " minutes.");
    }

    // csv, binary ve dl4j compressed yüklüyor
    // loadStaticModel word vectorlere erişmek için sadece
    private void loadPretrainedModel(String corpusPath) {

        log.info("Starting to load word2vec from file: "
                + corpusPath + " This may take a while. Total memory: ");
        word2vec = null;

        long start = System.nanoTime();
        word2vec = WordVectorSerializer.readWord2VecModel(corpusPath);
        log.info("Done loading word2vec model in " + (System.nanoTime() - start) / 60000 + " minutes.");
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
     * @return the number of the words in vocab cache of word2vec.
     */
    @Override
    public int getTotalWordNumberInModelVocab() {

        return word2vec.getVocab().numWords();
    }

    @Override
    public boolean hasWord(String word) {

        boolean result = word2vec.hasWord(word);
        if (!result && debugEnabled) {
            log.debug(word + " does not exist in word2vec model.");
        }
        return result;
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
    public Word2vecTrainingParams getWord2vecParams() {

        return params;
    }

    @Override
    public void resetParams() {

        params.reset();
    }

    @Override
    public List<String> getClosestWords(List<String> positive, List<String> negative) {

        return (List<String>) word2vec.wordsNearest(positive, negative, closestWordSize);
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
    public void getAccuracyByQuestions(List<String> questions) {

        word2vec.accuracy(questions);
    }

    public Collection<String> getNearestWords(String word) {

        // en yakın 3 kelime
        return word2vec.wordsNearest(word, 3);
    }

}
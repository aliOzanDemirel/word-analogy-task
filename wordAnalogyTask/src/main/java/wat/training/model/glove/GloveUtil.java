package wat.training.model.glove;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.glove.Glove;
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
import wat.training.model.BaseTrainingParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class GloveUtil extends BaseModel implements BaseModelInt {

    private static final Logger log = LoggerFactory.getLogger(GloveUtil.class);
    private static boolean debugEnabled = log.isDebugEnabled();

    private GloveTrainingParams params = new GloveTrainingParams();
    private Glove glove = null;

    @Override
    public String getName() {

        return "glove";
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
            this.buildGloveFromCorpus();
        }
    }

    private void buildGloveFromCorpus() throws ModelBuildException {

        params.validateCommonParams();

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
            throw new ModelBuildException("SentenceIterator cannot be created. " +
                    "Corpus path may be wrong:" + corpusPath);
        }

        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        log.info("Building glove with parameters: " + params.toString());
        Glove glove = new Glove.Builder()
                .iterate(sentenceIterator)
                .tokenizerFactory(t)
                .shuffle(params.isShuffle())
                .symmetric(params.isSymmetric())
                .xMax(params.getxMax())
                .alpha(params.getAlpha())
                .learningRate(params.getLearningRate())
                .minLearningRate(params.getMinLearningRate())
                .batchSize(params.getBatchSize())
                .epochs(params.getEpochs())
                .workers(params.getWorkers())
                .layerSize(params.getLayerSize())
                .windowSize(params.getWindowSize())
                .minWordFrequency(params.getMinWordFrequency())
                .seed(params.getSeed())
                .build();

        long start = System.currentTimeMillis();
        glove.fit();
        log.info("Done building glove model in "
                + (System.currentTimeMillis() - start) / 1000 + " seconds.");
    }

    private void loadPretrainedModel() throws ModelBuildException {

        log.info("Starting to load glove model from: " + corpusPath + " This may take a while.");
        glove = null;

        long start = System.currentTimeMillis();
        try {
            glove = (Glove) WordVectorSerializer.loadTxtVectors(new File(corpusPath));
        } catch (Exception e) {
            glove = null;
            throw new ModelBuildException(e);
        }
        log.info("Done loading glove model in "
                + (System.currentTimeMillis() - start) / 1000 + " seconds.");
    }

    /**
     * @param file represents a folder to save compressed model file in.
     * @return false if model could not be written to file.
     */
    @Override
    public boolean saveTrainedModel(File file) {

        try {
            WordVectorSerializer.writeWordVectors(glove, file);
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
    public int getTotalWordSizeInVocab() {

        return glove.getVocab().numWords();
    }

    @Override
    public boolean hasWord(String word) {

        if (glove.hasWord(word)) {
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

        if (glove == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void resetParams() {

        params.reset();
    }

    @Override
    public List<String> getClosestWords(List<String> positive, List<String> negative) {

        return (List<String>) glove.wordsNearest(positive, negative, closestWordSize);
    }

    @Override
    public double getSimilarity(String firstWord, String secondWord) {

        double result = glove.similarity(firstWord, secondWord);
        if (debugEnabled) {
            log.debug("Similarity between " + firstWord + " - " + secondWord + ": " + result);
        }
        return result;
    }

    @Override
    public List<String> getNearestWords(final String word) {

        return (List<String>) glove.wordsNearest(word, closestWordSize);

    }

    @Override
    public BaseTrainingParams getParams() {

        return params;
    }


}

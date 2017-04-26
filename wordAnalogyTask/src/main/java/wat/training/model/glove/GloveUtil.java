package wat.training.model.glove;

import org.deeplearning4j.models.glove.Glove;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.exceptions.ModelBuildException;
import wat.training.model.BaseModel;

import java.io.File;
import java.util.List;

public class GloveUtil extends BaseModel implements GloveUtilInt {

    private static final Logger log = LoggerFactory.getLogger(GloveUtil.class);

    private GloveTrainingParams params = new GloveTrainingParams();
    private Glove glove = null;

    @Override
    public String getName() {

        return "glove";
    }

    @Override
    public void createModel(int corpusType) throws ModelBuildException {

    }

    private void buildGloveFromCorpus() throws ModelBuildException {

        if (corpusPath == null || corpusPath.isEmpty()) {
            corpusPath = System.getenv("DEFAULT_CORPUS_PATH");
            log.warn("Corpus path is empty, setting to default: " + corpusPath);
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

        log.info("");

        glove.fit();
        log.info("");
    }

    @Override
    public double getSimilarity(String firstWord, String secondWord) {

        return glove.similarity(firstWord, secondWord);
    }

    @Override
    public List<String> getClosestWords(List<String> positive, List<String> negative) {

        return null;
    }

    @Override
    public void resetParams() {

        params.reset();
    }

    @Override
    public boolean hasWord(String word) {

        return glove.hasWord(word);
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
    public int getTotalWordNumberInModelVocab() {

        return 0;
    }

    @Override
    public boolean saveTrainedModel(File file) {

        return false;
    }


}

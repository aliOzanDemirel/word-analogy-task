package wat.model.glove;

import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.glove.Glove;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.exceptions.Word2vecBuildException;
import wat.helper.Constants;

import java.io.File;
import java.util.Collection;

public class GloveUtil implements GloveUtilInt {

    private static final Logger log = LoggerFactory.getLogger(GloveUtil.class);
    private Glove glove = null;

    @Override
    public void buildGloveFromCorpus(String corpusPath) throws Exception {

        if (corpusPath == null || corpusPath.isEmpty()) {
            corpusPath = System.getenv("DEFAULT_CORPUS_PATH");
            log.warn("Corpus path is empty, setting to default: " + corpusPath);
        }

        SentenceIterator sentenceIterator;
        try {
            sentenceIterator = new BasicLineIterator(corpusPath);
        } catch (Exception e) {
            throw new Word2vecBuildException("SentenceIterator cannot be created. Corpus path may be wrong: " +
                    corpusPath);
        }

        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());

        Glove glove = new Glove.Builder()
                .iterate(sentenceIterator)
                .tokenizerFactory(t)
                .alpha(0.75)
                .learningRate(0.1)
                .epochs(25)
                // cutoff for weighting function
                .xMax(100)
                // training is done in batches taken from training corpus
                .batchSize(1000)
                // if set to true, batches will be shuffled before training
                .shuffle(true)
                // if set to true word pairs will be built in both directions, LTR and RTL
                .symmetric(true)
                .build();

        log.info("");

        glove.fit();
        log.info("");
    }

    @Override
    public double getSimilarity(String firstWord, String secondWord) {
        double simD = glove.similarity(firstWord, secondWord);
        return simD;
    }

    public Collection<String> near() {
        return glove.wordsNearest("day", 10);
    }

    @Override
    public boolean hasWord(String word) {
        return glove.hasWord(word);
    }

    @Override
    public boolean isGloveReady() {
        if (glove == null) {
            return false;
        } else {
            return true;
        }
    }

}

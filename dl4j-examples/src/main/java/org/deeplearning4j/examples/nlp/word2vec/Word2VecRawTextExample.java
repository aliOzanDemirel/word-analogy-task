package org.deeplearning4j.examples.nlp.word2vec;

import org.datavec.api.util.ClassPathResource;
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

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by agibsonccc on 10/9/14.
 * <p>
 * Neural net that processes text into wordvectors. See below url for an in-depth explanation.
 * https://deeplearning4j.org/word2vec.html
 */
public class Word2VecRawTextExample {

    private static Logger log = LoggerFactory.getLogger(Word2VecRawTextExample.class);

    public static void main(String[] args) throws Exception {

        // Gets Path to Text file
        String filePath = new ClassPathResource("wolf.txt").getFile().getAbsolutePath();

        log.info("Load & Vectorize Sentences....");
        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(filePath);
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
// tokenların cümle olarak neyi aldığını belirlemek gerekibilir default olarak line
        /*
            CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
            So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
            Additionally it forces lower case for all tokens.
         */
        t.setTokenPreProcessor(new CommonPreprocessor());
//hugeModelExpected: true
        log.info("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(6)
                .iterations(1)
                .layerSize(300)
                .seed(42)
                .windowSize(6)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();
        log.info("Fitting Word2Vec model...");
        vec.fit();

        // log.info("Writing word vectors to text file....");
        // WordVectorSerializer.writeWordVectors(vec, "./dl4j-examples/src/main/resources/word_vectors.txt");

        log.info("Cosine similarity of words: " + vec.similarity("father", "mother"));
        log.info("Cosine similarity of words: " + vec.similarity("father", "son"));

        Collection<String> lst = vec.wordsNearest("youth", 10);
        System.out.println("10 words closest to: " + lst);

        lst = vec.wordsNearest(Arrays.asList("father", "son"), Arrays.asList("mother"), 1);
        System.out.println("Analogic counterpart: " + lst);

    }
}

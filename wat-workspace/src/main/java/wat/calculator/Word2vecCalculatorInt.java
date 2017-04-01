package wat.calculator;

import wat.exceptions.VocabularyBuildException;
import wat.exceptions.Word2vecBuildException;
import wat.model.wor2vec.Word2vecTrainingParams;

public interface Word2vecCalculatorInt extends AccuracyCalculatorInt {

    void prepareWord2vec(int corpusType) throws Word2vecBuildException, VocabularyBuildException;

    Word2vecTrainingParams getWord2vecParams();

}

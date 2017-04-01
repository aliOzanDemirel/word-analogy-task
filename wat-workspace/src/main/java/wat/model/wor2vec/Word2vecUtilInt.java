package wat.model.wor2vec;

import wat.exceptions.VocabularyBuildException;
import wat.exceptions.Word2vecBuildException;

import java.util.Collection;
import java.util.List;

public interface Word2vecUtilInt {

    public void createWord2vec(String corpusPath, int corpusType, Word2vecTrainingParams params) throws
            Word2vecBuildException, VocabularyBuildException;

    public double getSimilarity(String firstWord, String secondWord);

    public void getAccuracy(List<String> questions);

    public boolean hasWord(String word);

    public Collection<String> getNearestWords(String word, int number);

    public boolean isWord2vecReady();

}

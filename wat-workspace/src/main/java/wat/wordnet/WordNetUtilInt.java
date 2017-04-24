package wat.wordnet;

import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import wat.calculator.AccuracyCalculatorInt;

import java.io.IOException;

public interface WordNetUtilInt {

    void calculateSimilarityScoreForAllWords(final AccuracyCalculatorInt calculator, final boolean
            isAnalogyTest) throws IOException;

    void calculateSimilarityScoreForPOS(final AccuracyCalculatorInt calculator, final POS partOfSpeech,
            final boolean isAnalogyTest) throws IOException;

    void calculateAnalogyOfWordInput(final AccuracyCalculatorInt calculator, final String wordInput);

    void loadDictionaryIntoMemory();

    void closeDictionary();

    void listWordsSemanticPointers() throws IOException;

    void listNouns() throws IOException;

    void listVerbs() throws IOException;

    void listWordsLexicalPointers();

    void listPointerMap();

}

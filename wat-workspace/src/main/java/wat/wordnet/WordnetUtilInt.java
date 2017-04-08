package wat.wordnet;

import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import wat.calculator.AccuracyCalculatorInt;

import java.io.IOException;

public interface WordnetUtilInt {

    void calculateSimilarityAccuracyForAllWords(AccuracyCalculatorInt calculator) throws IOException;

    void calculateSimilarityAccuracyForGivenPOS(AccuracyCalculatorInt calculator, POS partOfSpeech) throws
            IOException;

    void loadDictionaryIntoMemory();

    void closeDictionary();

    void listWordsSemanticPointers() throws IOException;

    void listNouns() throws IOException;

    void listVerbs() throws IOException;

    void listWordsLexicalPointers();

    void calculateAnalogicalAccuracyOfOneWord(AccuracyCalculatorInt calculator, IWordID wordID);

    void listPointerMap();

}

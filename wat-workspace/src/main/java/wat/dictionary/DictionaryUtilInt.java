package wat.dictionary;

import edu.mit.jwi.item.POS;
import wat.calculator.AccuracyCalculatorInt;

import java.io.IOException;

public interface DictionaryUtilInt {

    void calculateAccuracyForAllWords(AccuracyCalculatorInt calculator) throws IOException;

    void calculateAccuracyForGivenPOS(AccuracyCalculatorInt calculator, POS partOfSpeech) throws IOException;

    void loadDictionaryIntoMemory();

    void closeDictionary();

    void listSenseKeyAndSynsetsOfAdjectives() throws IOException;

    void listNounsWithPointers() throws IOException;

    void listVerbs() throws IOException;

}

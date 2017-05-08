package wat.wordnet;

import edu.mit.jwi.item.POS;
import wat.calculator.Calculator;
import wat.training.model.BaseModelInt;

import java.io.IOException;

public interface WordNetUtilInt {

    void calculateScoreForAllWords(final BaseModelInt usedModel,
            final boolean isAnalogyTest) throws IOException;

    void calculateScoreForPOSFromController(final BaseModelInt usedModel, final POS partOfSpeech,
            final boolean isAnalogyTest) throws IOException;

    void calculateAnalogyScoreOfWordInput(final BaseModelInt usedModel, final String wordInput);

    void loadDictionaryIntoMemory();

    void closeDictionary();

    void listWordsSemanticPointers();

    void listWordsLexicalPointers();

    void listNouns();

    void listVerbs();

    void listAdjectives();

    void listAdverbs();

    void listPointerMap();

    Calculator getCalc();

    void resetIterationCapForPointer();

    void setIterationCapForPointer(int iterationCap);

}

package wat.wordnet;

import edu.mit.jwi.item.POS;
import wat.calculator.Calculator;
import wat.training.model.BaseModelInt;

import java.io.IOException;

public interface WordNetUtilInt {

    void calculateScoreForAllWords(final BaseModelInt usedModel,
            final boolean isAnalogyTest, final boolean onlySynsetComparison) throws IOException;

    void calculateScoreForPOSFromController(final BaseModelInt usedModel, final POS partOfSpeech,
            final boolean isAnalogyTest, final boolean onlySynsetComparison) throws IOException;

    void calculateAnalogyScoreOfWordInput(final BaseModelInt usedModel, final String wordInput,
            boolean onlySynsetComparison);

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

    void setIterationCapForPointer(int iterationCap);

    void setPhraseComparisonSetting(boolean dontComparePhrases);
}

package wat.helper;

import java.util.HashMap;
import java.util.List;

public class WordWithRelations {

    private String lemma;
    private List<WordWithRelations> wordsInSynset;
    private HashMap<WordNetPointers, List<WordWithRelations>> relatedSynsetWords;
    private HashMap<WordNetPointers, List<WordWithRelations>> lexicalRelatedWords;

}

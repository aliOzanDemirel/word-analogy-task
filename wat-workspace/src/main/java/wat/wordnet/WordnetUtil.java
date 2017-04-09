package wat.wordnet;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.item.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.calculator.AccuracyCalculatorInt;
import wat.helper.WordNetPointers;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class WordnetUtil implements WordnetUtilInt {

    // IndexWord içindeki wordID listesi Arrays$ArrayList tipinde bir liste yani array'le implement edili
    // Word veya Word'ün içineki Synset'in içindeki word listesi de ArrayList, yine arka tarafta array var

    private static final Logger log = LoggerFactory.getLogger(WordnetUtil.class);

    // TODO: bu liste semantic ve lexical için iki ayrı listeye bölünmeli
    private HashSet<WordNetPointers> analogyTypes = new HashSet<WordNetPointers>(25) {{
        add(WordNetPointers.ANTONYM);
        add(WordNetPointers.ATTRIBUTE);
        add(WordNetPointers.CAUSE);
        add(WordNetPointers.ENTAILMENT);
        add(WordNetPointers.HYPERNYM);
        add(WordNetPointers.HYPERNYM_INSTANCE);
        add(WordNetPointers.HYPONYM);
        add(WordNetPointers.HYPONYM_INSTANCE);
        add(WordNetPointers.HOLONYM_MEMBER);
        add(WordNetPointers.HOLONYM_PART);
        add(WordNetPointers.HOLONYM_SUBSTANCE);
        add(WordNetPointers.MEMBER);
        add(WordNetPointers.MERONYM_MEMBER);
        add(WordNetPointers.MERONYM_SUBSTANCE);
        add(WordNetPointers.MERONYM_PART);
        add(WordNetPointers.REGION);
        add(WordNetPointers.REGION_MEMBER);
        add(WordNetPointers.SIMILAR_TO);
        add(WordNetPointers.TOPIC);
        add(WordNetPointers.TOPIC_MEMBER);
        add(WordNetPointers.USAGE);
        add(WordNetPointers.USAGE_MEMBER);
        add(WordNetPointers.VERB_GROUP);
    }};
    private HashMap<IPointer, HashSet<IWord>> pointerToWordMap = null;
    protected IRAMDictionary dict = null;

    public WordnetUtil(String path, int loadPolicy) throws IOException {

        dict = new RAMDictionary(new URL("file", null, path), loadPolicy);
        this.openDictionary();
        log.info("WordNet (version " + dict.getVersion() + ") is loaded with policy: "
                + loadPolicy + " (NO_LOAD: 2, BACKGROUND_LOAD = 4, IMMEDIATE_LOAD = 8)");
    }

    /**
     * opens the wordnet if it is not already open.
     */
    private void openDictionary() throws IOException {
        // dict.isOpen() kontrolüne göre yok zira wordnet state'i zaten
        // açık veya düzgün bir şekilde açık yapılınca true dönüyor
        if (dict.open()) {
            log.info("Dictionary is opened.");
        } else {
            log.warn("Dictionary is already opened.");
        }
    }

    /**
     * closes the wordnet if it is open.
     */
    @Override
    public void closeDictionary() {
        // dict.isOpen() kontrolüne göre yok zira wordnet state'i zaten
        // kapalı veya o anda kapanıyorsa hiçbir şey yapmıyor
        dict.close();
        log.info("Dictionary is closed.");
    }

    @Override
    public void loadDictionaryIntoMemory() {
        // wordnet'i kapatmaya gerek olabilir
        log.info("Loading wordnet into memory.");
        try {
            // setLoadPolicy'ye gerek yok sanırım
            // true ile çağırınca loading'in bitmesini bekliyor method call
            dict.load(true);
            log.info("Loading is done.");
        } catch (InterruptedException e) {
            log.error("Dictionary load process is interrupted, it may not be loaded properly!", e);
        }
    }

    /**
     * @param calculator can be glove or word2vec calculator.
     * @throws IOException when WordNet file can't be opened.
     */
    @Override
    public void calculateSimilarityAccuracyForAllWords(final AccuracyCalculatorInt calculator)
            throws IOException {

        long started = System.currentTimeMillis();
        for (POS partOfSpeech : POS.values()) {
            this.calculateSimilarityAccuracyForGivenWords(calculator, dict.getIndexWordIterator
                    (partOfSpeech));
        }
        long timePassed = (System.currentTimeMillis() - started) / 1000;
        log.info(timePassed + " seconds passed while calculating similarity for all words.");
    }

    /**
     * @param calculator   can be glove or word2vec calculator.
     * @param partOfSpeech can be noun, verb, adjective or adverb.
     * @throws IOException
     */
    @Override
    public void calculateSimilarityAccuracyForGivenPOS(final AccuracyCalculatorInt calculator,
            final POS partOfSpeech) throws IOException {

        long started = System.currentTimeMillis();
        this.calculateSimilarityAccuracyForGivenWords(calculator, dict.getIndexWordIterator(partOfSpeech));
        long timePassed = (System.currentTimeMillis() - started) / 1000;
        log.debug(timePassed + " seconds passed while calculating similarity for POS.");
    }

    private void calculateSimilarityAccuracyForGivenWords(final AccuracyCalculatorInt calculator,
            final Iterator<IIndexWord> indexWordIterator) throws IOException {

        List<IWordID> wordIDs;
        IIndexWord iIndexWord;
        ISynset synset;
        // o anda process edilen kelime
        IWord rootWord;
        int totalWordsForWordID;
        while (indexWordIterator.hasNext()) {
            iIndexWord = indexWordIterator.next();
            if (!this.isPhrase(iIndexWord.getLemma())) {
                wordIDs = iIndexWord.getWordIDs();
                totalWordsForWordID = wordIDs.size();
                for (int i = 0; i < totalWordsForWordID; i++) {
                    // bir kelimenin farklı anlamları varsa farklı ID ile farklı Word objelerinde
                    // birden çok olabiliyor, buradaki counter farklı ID'deki aynı kelimeler için
                    IWordID wordID = wordIDs.get(i);
                    rootWord = dict.getWord(wordID);
                    synset = rootWord.getSynset();
                    this.calculateSimilarityOfWordWithItsSynset(calculator, rootWord.getLemma(), synset
                            .getWords());
                }
                log.info("Similarity accuracy: " + calculator.updateAndGetAccuracyPercentage());
            } else {
                log.info(iIndexWord.getLemma() + " is not a word.");
            }
        }
    }

    private void calculateSimilarityOfWordWithItsSynset(final AccuracyCalculatorInt calculator,
            final String rootWordLemma, final List<IWord> synWords) {
        // rootun synonimi olan kelime
        IWord synWord;
        for (int k = 0; k < synWords.size(); k++) {
            synWord = synWords.get(k);
            calculator.updateSimilarityAccuracy(rootWordLemma, synWord.getLemma());
        }
    }

    private void calculateAnalogicalAccuracyOfWordList(final AccuracyCalculatorInt calculator,
            final Iterator<IIndexWord> indexWordIterator) {

        while (indexWordIterator.hasNext()) {
            IIndexWord iIndexWord = indexWordIterator.next();
            if (!this.isPhrase(iIndexWord.getLemma())) {
                // bir kelimeyi bir kerede loglamak için
                List<IWordID> wordIDs = iIndexWord.getWordIDs();
                int totalWordsForWordID = wordIDs.size();
                for (int i = 0; i < totalWordsForWordID; i++) {
                    IWordID wordID = wordIDs.get(i);
                    this.calculateAnalogicalAccuracyOfOneWord(calculator, wordID);
                }
                log.info("Analogical accuracy: " + calculator.updateAndGetAccuracyPercentage());
            } else {
                log.info(iIndexWord.getLemma() + " is not a word.");
            }
        }
    }

    @Override
    public void calculateAnalogicalAccuracyOfOneWord(final AccuracyCalculatorInt calculator,
            final IWordID wordID) {

        // o anda process edilen kelime
        IWord rootWord = dict.getWord(wordID);
        this.calculateLexicalAnalogy(calculator, rootWord);

        this.calculateSemanticAnalogy(calculator, rootWord);

    }

    private void calculateLexicalAnalogy(final AccuracyCalculatorInt calculator, final IWord rootWord) {

        String rootWordLemma, relatedWordLemma;
        final Map<IPointer, List<IWordID>> relatedWordMap = rootWord.getRelatedMap();
        for (IPointer iPointer : relatedWordMap.keySet()) {
            // o anki pointer analojik kıyas için manalıysa
            if (analogyTypes.contains(WordNetPointers.valueOf(iPointer.getSymbol()))) {
                final List<IWordID> lexicallyRelatedWordIDs = relatedWordMap.get(iPointer);
                final HashSet<IWord> wordsOfPointer = pointerToWordMap.get(iPointer);
                int relatedWordSizeForPointer = lexicallyRelatedWordIDs.size();
                // rootWord + relatedWord - pointerToWordMap'den o anki pointer'ın kelimeleri çekilerek
                // hepsi için analoji hesabı
                for (int k = 0; k < relatedWordSizeForPointer; k++) {
                    IWord relatedWord = dict.getWord(lexicallyRelatedWordIDs.get(k));
                    rootWordLemma = rootWord.getLemma();
                    relatedWordLemma = relatedWord.getLemma();
                    // related kelime ile root aynı olmamalı
                    if (!rootWordLemma.equals(relatedWordLemma)) {
                        for (IWord wordToCheck : wordsOfPointer) {
//                            this.koke(rootWordLemma, relatedWordLemma, wordToCheck);
//

                        }
                    }
                }
            }
        }
    }

    private void koke(final AccuracyCalculatorInt calculator, final String rootWordLemma, final String
            relatedWordLemma, final IWord wordToCheck) {

        calculator.updateAnalogicalAccuracy(rootWordLemma, relatedWordLemma,
                wordToCheck);


    }

    private void calculateAnalogyByComparingAllWordsForAPointer(final AccuracyCalculatorInt calculator,
            final IWord rootWord, final List<IWordID> relatedWordIDs, final IPointer iPointer) {

    }

    private void calculateSemanticAnalogy(final AccuracyCalculatorInt calculator, final IWord rootWord) {

        final Map<IPointer, List<ISynsetID>> relatedSynsetMap = rootWord.getSynset().getRelatedMap();
    }

    /**
     * WordNet has phrases other than words. The words in phrases are connected to each other by '_', this
     * is how the given word to this method is being checked.
     *
     * @param wordLemma
     * @return false if the word is actually a phrase.
     */
    private boolean isPhrase(final String wordLemma) {

        return wordLemma.contains("_");
    }

    private void preparePointerToWordMap() {

        if (pointerToWordMap == null) {
            pointerToWordMap = new HashMap<IPointer, HashSet<IWord>>(15);
            for (POS partOfSpeech : POS.values()) {
                final Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(partOfSpeech);
                while (indexWordIterator.hasNext()) {
                    final IIndexWord iIndexWord = indexWordIterator.next();
                    final List<IWordID> wordIDs = iIndexWord.getWordIDs();
                    int totalWordsForWordID = wordIDs.size();
                    for (int i = 0; i < totalWordsForWordID; i++) {
                        // bir kelimenin farklı anlamları varsa farklı ID ile farklı Word objelerinde
                        // birden çok olabiliyor
                        final IWordID wordID = wordIDs.get(i);
                        // o anda process edilen kelime
                        final IWord rootWord = dict.getWord(wordID);
                        // lexical pointer'ın setine koy
                        this.addToPointerMap(rootWord, rootWord.getRelatedMap().keySet());
                        // semantic pointer'ın setine koy
                        this.addToPointerMap(rootWord, rootWord.getSynset().getRelatedMap().keySet());
                    }
                }
            }
        } else {
            log.info("Pointer and word mapping is already done.");
        }
    }

    private void addToPointerMap(IWord word, Set<IPointer> pointers) {

        for (IPointer relPtr : pointers) {
            HashSet<IWord> iWords = pointerToWordMap.get(relPtr);
            if (iWords == null) {
                iWords = new HashSet<IWord>(300) {{
                    add(word);
                }};
                pointerToWordMap.put(relPtr, iWords);
            } else {
                iWords.add(word);
            }
        }
    }

    @Override
    public void listPointerMap() {

        long start = System.currentTimeMillis();
        this.preparePointerToWordMap();
        for (IPointer iPointer : pointerToWordMap.keySet()) {
            StringBuilder stringBuilder = new StringBuilder(5000);
            HashSet<IWord> words = pointerToWordMap.get(iPointer);
            stringBuilder.append("*********** ").append(iPointer.getName()).append(" ***********\n");
            for (IWord word : words) {
                stringBuilder.append("Lemma: ").append(word.getLemma()).append(" Gloss: ").append(word
                        .getSynset().getGloss()).append("\n");
            }
            log.info(stringBuilder.toString());
        }
        log.info("Total Time Passed: " + (System.currentTimeMillis() - start) / 1000);
    }

    @Override
    public void listWordsSemanticPointers() {

        for (POS partOfSpeech : POS.values()) {
            StringBuilder strBuilder = new StringBuilder();
            final Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(partOfSpeech);
            while (indexWordIterator.hasNext()) {
                final IIndexWord iIndexWord = indexWordIterator.next();
                final List<IWordID> wordIDs = iIndexWord.getWordIDs();
                int totalWordsForWordID = wordIDs.size();
                for (int i = 0; i < totalWordsForWordID; i++) {
                    // bir kelimenin farklı anlamları varsa farklı ID ile farklı Word objelerinde
                    // birden çok olabiliyor
                    final IWordID wordID = wordIDs.get(i);
                    // o anda process edilen kelime
                    final IWord rootWord = dict.getWord(wordID);
                    strBuilder.append("\nWord: ").append(rootWord.getLemma());
                    final ISynset synset = rootWord.getSynset();
                    strBuilder.append(" Synset: ").append(synset.getGloss());
                    final Map<IPointer, List<ISynsetID>> relatedMap = synset.getRelatedMap();
                    for (IPointer relPtr : relatedMap.keySet()) {
                        strBuilder.append("\nPointer: ").append(relPtr.getName())
                                .append(", Semantically Related Set:\n");
                        for (ISynsetID relatedId : relatedMap.get(relPtr)) {
                            final ISynset relatedSynset = dict.getSynset(relatedId);
                            strBuilder.append(relatedSynset.getGloss()).append(" *** ");
                        }
                    }
                }
            }
            log.info(strBuilder.toString());
        }
    }

    @Override
    public void listWordsLexicalPointers() {

        for (POS partOfSpeech : POS.values()) {
            StringBuilder strBuilder = new StringBuilder();
            final Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(partOfSpeech);
            while (indexWordIterator.hasNext()) {
                final IIndexWord iIndexWord = indexWordIterator.next();
                final List<IWordID> wordIDs = iIndexWord.getWordIDs();
                int totalWordsForWordID = wordIDs.size();
                for (int i = 0; i < totalWordsForWordID; i++) {
                    // bir kelimenin farklı anlamları varsa farklı ID ile farklı Word objelerinde
                    // birden çok olabiliyor
                    final IWordID wordID = wordIDs.get(i);
                    // o anda process edilen kelime
                    final IWord rootWord = dict.getWord(wordID);
                    strBuilder.append("\nWord: ").append(rootWord.getLemma()).append(" - ").append(rootWord
                            .getSynset().getGloss()).append(" Lexical ID ").append(rootWord.getLexicalID());
                    final Map<IPointer, List<IWordID>> relatedMap = rootWord.getRelatedMap();
                    for (IPointer relPtr : relatedMap.keySet()) {
                        strBuilder.append("\nPointer: ").append(relPtr.getName())
                                .append(", Lexically Related Set:\n");
                        for (IWordID iWordID : relatedMap.get(relPtr)) {
                            final IWord related = dict.getWord(iWordID);
                            strBuilder.append(related.getLemma()).append(" - ").append(related.getSynset()
                                    .getGloss()).append(" *** ");
                        }
                    }
                }
            }
            log.info(strBuilder.toString());
        }
    }

    @Override
    public void listNouns() {

        final Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(POS.NOUN);
        while (indexWordIterator.hasNext()) {
            IIndexWord iIndexWord = indexWordIterator.next();
            if (iIndexWord.getWordIDs().get(0).getLemma().startsWith("b")) {
                throw new RuntimeException("B'de bitsin!");
            }
            List<IWordID> wordIDs = iIndexWord.getWordIDs();
            if (wordIDs.size() > 1) {
                log.debug(iIndexWord.getLemma() + " has " + wordIDs.size() +
                        " different meanings.");
            }
            for (IWordID wordID : wordIDs) {
                IWord word = dict.getWord(wordID);
                log.debug("Current word:");
                logWord(word);
                log.debug("Words of synset:");
                logWords(word.getSynset().getWords());
                log.debug("Related words:");
                this.logWordIDList(word.getRelatedWords());
            }
            log.debug("**********************************************************");
        }
    }

    @Override
    public void listVerbs() {

        Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(POS.VERB);
        while (indexWordIterator.hasNext()) {
            IIndexWord iIndexWord = indexWordIterator.next();
            List<IWordID> wordIDs = iIndexWord.getWordIDs();
            for (IWordID wordID : wordIDs) {
                IWord word = dict.getWord(wordID);
                logWord(word);
            }
            log.debug("**********************************************************");
        }
    }

    public void listAdjectives() {

    }

    public void listAdverbs() {

    }

    private void logWordIDList(List<IWordID> words) {

        for (IWordID wordId : words) {
            IWord word = dict.getWord(wordId);
            this.logWord(word);
        }
    }

    private void logWords(List<IWord> words) {

        words.forEach(iWord -> this.logWord(iWord));
    }

    private void logWord(IWord word) {

        log.info("Lemma: " + word.getLemma() + " Lexical ID: " + word.getLexicalID()
                + " Adjective Marker: " + word.getAdjectiveMarker() + " Verb Frames: "
                + word.getVerbFrames().toString());
    }

}

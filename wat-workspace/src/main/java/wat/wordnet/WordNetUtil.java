package wat.wordnet;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.item.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.calculator.Calculator;
import wat.calculator.CalculatorInt;
import wat.helper.WordNetPointers;
import wat.training.model.BaseModelInt;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class WordNetUtil implements WordNetUtilInt {

    // IndexWord içindeki wordID listesi Arrays$ArrayList tipinde bir liste yani array'le implement edili
    // Word veya Word'ün içineki Synset'in içindeki word listesi de ArrayList, yine arka tarafta array var

    private static final Logger log = LoggerFactory.getLogger(WordNetUtil.class);

    private HashMap<IPointer, HashSet<IWord>> pointerToWordMap = null;
    private IRAMDictionary dict = null;
    private CalculatorInt calc = new Calculator();

    private HashSet<WordNetPointers> semanticAnalogyTypes = new HashSet<WordNetPointers>(21) {{
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
        add(WordNetPointers.SIMILAR_TO);
//        add(WordNetPointers.ALSO_SEE);
//        add(WordNetPointers.VERB_GROUP);
//        add(WordNetPointers.TOPIC);
//        add(WordNetPointers.TOPIC_MEMBER);
//        add(WordNetPointers.USAGE);
//        add(WordNetPointers.USAGE_MEMBER);
//        add(WordNetPointers.REGION);
//        add(WordNetPointers.REGION_MEMBER);
    }};
    private HashSet<WordNetPointers> lexicalAnalogyTypes = new HashSet<WordNetPointers>(21) {{
        add(WordNetPointers.ANTONYM);
        add(WordNetPointers.PERTAINYM);
        add(WordNetPointers.PARTICIPLE);
        add(WordNetPointers.DERIVED_FROM_ADJ);
        add(WordNetPointers.DERIVATIONALLY_RELATED);
    }};

    public WordNetUtil(String path, int loadPolicy) throws IOException {

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
     * @param usedModel     can be glove or word2vec usedModel.
     * @param isAnalogyTest true to calculate analogical relationship.
     * @throws IOException when WordNet file can't be opened.
     */
    @Override
    public void calculateScoreForAllWords(final BaseModelInt usedModel,
            final boolean isAnalogyTest) throws IOException {

        long started = System.currentTimeMillis();
        for (POS partOfSpeech : POS.values()) {
            this.calculateScoreForPOS(usedModel, partOfSpeech, isAnalogyTest);
        }
        long timePassed = (System.currentTimeMillis() - started) / 1000;
        log.info(timePassed + " seconds passed while calculating similarity score for all words.");
    }

    /**
     * @param usedModel     can be glove or word2vec usedModel.
     * @param partOfSpeech  can be noun, verb, adjective or adverb.
     * @param isAnalogyTest true to calculate analogical relationship.
     * @throws IOException
     */
    @Override
    public void calculateScoreForPOS(final BaseModelInt usedModel,
            final POS partOfSpeech, final boolean isAnalogyTest) throws IOException {

        if (isAnalogyTest) {
            this.calculateAnalogicalScoreOfWordIterator(usedModel,
                    dict.getIndexWordIterator(partOfSpeech));
        } else {
            this.calculateSimilarityScoreOfWordIterator(usedModel,
                    dict.getIndexWordIterator(partOfSpeech));
        }
    }

    /**
     * calculates similarity score by summing word2vec's similarity score for given word and its synset's
     * words as pairs.
     *
     * @param usedModel
     * @param indexWordIterator
     * @throws IOException
     */
    private void calculateSimilarityScoreOfWordIterator(final BaseModelInt usedModel,
            final Iterator<IIndexWord> indexWordIterator) throws IOException {

        IIndexWord iIndexWord;
        // asıl işlenen kelime
        IWord rootWord;
        List<IWordID> wordIDs;
        int totalWordsForWordID;
        while (indexWordIterator.hasNext()) {
            iIndexWord = indexWordIterator.next();
            if (!this.validateWord(iIndexWord.getLemma())) {
                wordIDs = iIndexWord.getWordIDs();
                totalWordsForWordID = wordIDs.size();
                for (int i = 0; i < totalWordsForWordID; i++) {
                    // bir kelimenin farklı anlamları varsa farklı ID ile farklı Word objelerinde
                    // birden çok olabiliyor, buradaki counter farklı ID'deki aynı kelimeler için
                    rootWord = dict.getWord(wordIDs.get(i));
                    this.calculateSimilarityOfWordWithItsSynset(usedModel, rootWord.getLemma(), rootWord
                            .getSynset().getWords());
                }
                log.info("Similarity accuracy: " + calc.getSimilarityPercentage());
            } else {
                log.info(iIndexWord.getLemma() + " is not a word.");
            }
        }
    }

    private void calculateSimilarityOfWordWithItsSynset(final BaseModelInt usedModel,
            final String rootWordLemma, final List<IWord> synWords) {
        // rootun synonimi olan kelime
        IWord synWord;
        for (int k = 0; k < synWords.size(); k++) {
            synWord = synWords.get(k);
            if (usedModel.hasWord(rootWordLemma) && usedModel.hasWord(synWord.getLemma())) {
                calc.updateSimilarityAccuracy(usedModel.getSimilarity(rootWordLemma, synWord.getLemma()));
            }
        }
    }

    /**
     * input is validated before here.
     *
     * @param usedModel
     * @param wordInput
     */
    @Override
    public void calculateAnalogyOfWordInput(final BaseModelInt usedModel,
            final String wordInput) {

        IWord rootWord = null;
        IIndexWord indexWord = null;
        for (int i = 1; i < 4 && indexWord == null; i++) {
            indexWord = dict.getIndexWord(wordInput, POS.getPartOfSpeech(i));
        }

        if (indexWord == null) {
            log.warn(wordInput + " could not be found in WordNet.");
        } else {
            List<IWordID> wordIDs = indexWord.getWordIDs();
            int totalWordsForWordID = wordIDs.size();
            for (int i = 0; i < totalWordsForWordID; i++) {
                rootWord = dict.getWord(wordIDs.get(i));
                this.calculateAnalogicalAccuracyOfOneWord(usedModel, rootWord, wordInput);
            }
            log.info("Analogical accuracy score: " + calc.getAnalogicalPercentage());
        }
    }

    private void calculateAnalogicalScoreOfWordIterator(final BaseModelInt usedModel,
            final Iterator<IIndexWord> indexWordIterator) {

        while (indexWordIterator.hasNext()) {
            IIndexWord iIndexWord = indexWordIterator.next();

            // kelime word2vec'e yollanmaya uygun mu kontrolü
            if (!this.validateWord(iIndexWord.getLemma())) {
                // bir kelimeyi bir kerede loglamak için
                List<IWordID> wordIDs = iIndexWord.getWordIDs();
                int totalWordsForWordID = wordIDs.size();
                for (int i = 0; i < totalWordsForWordID; i++) {

                    // esas işlenen kelime
                    final IWord rootWord = dict.getWord(wordIDs.get(i));
                    this.calculateAnalogicalAccuracyOfOneWord(usedModel, rootWord, rootWord.getLemma());
                }
                log.info("Analogical accuracy score: " + calc.getAnalogicalPercentage());
            } else {
                log.info(iIndexWord.getLemma() + " is not a word.");
            }
        }
    }

    private void calculateAnalogicalAccuracyOfOneWord(final BaseModelInt usedModel,
            final IWord rootWord, final String rootWordLemma) {

        this.calculateLexicalAnalogy(usedModel, rootWord, rootWordLemma);
        this.calculateSemanticAnalogy(usedModel, rootWord, rootWordLemma);
    }

    private void calculateLexicalAnalogy(final BaseModelInt usedModel, final IWord rootWord,
            final String rootWordLemma) {

        String relatedWordLemma, comparedWordLemma;
        final Map<IPointer, List<IWordID>> relatedWordMap = rootWord.getRelatedMap();
        for (IPointer iPointer : relatedWordMap.keySet()) {

            // pointer analojik olarak manalıysa
            if (lexicalAnalogyTypes.contains(WordNetPointers.valueOf(iPointer.getSymbol()))) {
                final HashSet<IWord> wordsOfPointer = pointerToWordMap.get(iPointer);

                // root kelimenin lexical yakın kelimeleri
                final List<IWordID> lexicallyRelatedWordIDs = relatedWordMap.get(iPointer);
                int relatedWordSizeForPointer = lexicallyRelatedWordIDs.size();

                for (int k = 0; k < relatedWordSizeForPointer; k++) {
                    IWord relatedWord = dict.getWord(lexicallyRelatedWordIDs.get(k));
                    relatedWordLemma = relatedWord.getLemma();

                    // related kelime ile root aynı olmamalı
                    if (rootWordLemma.equals(relatedWordLemma)) {
                        log.warn(rootWordLemma + " is same as related word.");
                    } else {
                        for (IWord comparedWord : wordsOfPointer) {
                            comparedWordLemma = comparedWord.getLemma();

                            // kıyaslanan kelime root veya related'la aynı olamaz
                            if (comparedWordLemma.equals(rootWordLemma) || comparedWordLemma.equals
                                    (relatedWordLemma)) {
                                log.warn(comparedWordLemma + " is same as root or related word, it will not" +
                                        " be counted.");
                            } else {
                                final List<IWordID> relatedWordsOfCompared = comparedWord.getRelatedWords
                                        (iPointer);

                                this.compareWordPairWithGivenThird(usedModel,
                                        this.returnWordsFromWordIDs(relatedWordsOfCompared),
                                        rootWordLemma, relatedWordLemma, comparedWordLemma);
                            }
                        }
                    }
                }
            }
        }
    }

    private List<IWord> returnWordsFromWordIDs(final List<IWordID> relatedWordsOfCompared) {

        List<IWord> words = new ArrayList<>(relatedWordsOfCompared.size());
        for (IWordID iWordID : relatedWordsOfCompared) {
            words.add(dict.getWord(iWordID));
        }
        return words;
    }

    /**
     * rootWord + relatedWord - pointerToWordMap'den o anki pointer'ın kelimeleri çekilerek hepsi için
     * analoji hesabı
     *
     * @param usedModel
     * @param relatedWordsOfCompared
     * @param rootWordLemma
     * @param pairWordLemma
     * @param comparedWordLemma      third word to be compared.
     */
    private void compareWordPairWithGivenThird(final BaseModelInt usedModel,
            final List<IWord> relatedWordsOfCompared, final String rootWordLemma,
            final String pairWordLemma, final String comparedWordLemma) {

        if (relatedWordsOfCompared.isEmpty()) {

            // pointer ismi de loglanabilir
            log.error(comparedWordLemma + " does not have any related words. This shouldn't have happened!");
        } else if (usedModel.hasWord(rootWordLemma) && usedModel.hasWord(pairWordLemma) &&
                usedModel.hasWord(comparedWordLemma)) {

            // word2vec sorgusu, sorgulanan kelimenin tüm related kelimeleri için tekrar tekrar
            // yapılmasın diye burada
            final List<String> closestWords = usedModel.getClosestWords(Arrays.asList(rootWordLemma,
                    pairWordLemma), Arrays.asList(comparedWordLemma));

            for (IWord wrd : relatedWordsOfCompared) {
                calc.updateAnalogicalAccuracy(wrd.getLemma(), closestWords);
            }
        } else {
            log.warn("Word2vec vocabulary do not have one of those: " + rootWordLemma + "-" +
                    rootWordLemma + "-" + comparedWordLemma);
        }
    }

    private void calculateSemanticAnalogy(final BaseModelInt usedModel, final IWord rootWord,
            final String rootWordLemma) {

        // rootWordLemma + synsetWordLemma analojik pair
        String synsetWordLemma, comparedWordLemma;
        final ISynset rootSynset = rootWord.getSynset();
        final Map<IPointer, List<ISynsetID>> relatedSynsetMap = rootSynset.getRelatedMap();

        // kelimenin synsetindeki her ilişki için yap
        for (IPointer iPointer : relatedSynsetMap.keySet()) {

            // pointer karşılaştırma için uygunsa
            if (semanticAnalogyTypes.contains(WordNetPointers.valueOf(iPointer.getSymbol()))) {

                // o andaki pointer'a sahip bütün kelimeleri çek
                final HashSet<IWord> wordsOfPointer = pointerToWordMap.get(iPointer);

                // bu pointer için ilişkili olduğu synset'leri çek
                final List<ISynsetID> semanticallyRelatedSynsetIDs = relatedSynsetMap.get(iPointer);
                int relatedSynsetSizeForPointer = semanticallyRelatedSynsetIDs.size();

                for (int k = 0; k < relatedSynsetSizeForPointer; k++) {

                    // kelimeyi, kendi synset'indeki kelimelerle değil de ilişkili synset'teki kelimelerle
                    // (pair) ve synset'ler arasındaki ilişkiye sahip kelimelerle (third) sorgulamalı
                    ISynset relatedSynset = dict.getSynset(semanticallyRelatedSynsetIDs.get(k));
                    final List<IWord> relatedSynsetWords = relatedSynset.getWords();

                    for (IWord relatedSynsetWord : relatedSynsetWords) {
                        synsetWordLemma = relatedSynsetWord.getLemma();

                        // başka synset'teki kelime ile root aynı olmamalı
                        if (rootWordLemma.equals(synsetWordLemma)) {
                            log.warn(rootWordLemma + " is same as related word.");
                        } else {
                            for (IWord comparedWord : wordsOfPointer) {

                                comparedWordLemma = comparedWord.getLemma();
                                if (comparedWordLemma.equals(rootWordLemma) || comparedWordLemma.equals
                                        (synsetWordLemma)) {
                                    log.info(comparedWordLemma + " is same as root or related word, it will" +
                                            " not be counted.");
                                } else {
                                    // kelimenin synseti yukarıdan gelen pointer'a sahip mi iyi test edilmeli
                                    final List<IWord> synsetWordsOfCompared = comparedWord.getSynset()
                                            .getWords();

                                    this.compareWordPairWithGivenThird(usedModel, synsetWordsOfCompared,
                                            rootWordLemma, synsetWordLemma, comparedWordLemma);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * WordNet has phrases which are connected to each other by '_'. Also it has words as '.22' or '10'
     * that we don't want to check for analogy.
     *
     * @param wordLemma
     * @return false if the word is not valid for checking.
     */
    private boolean validateWord(final String wordLemma) {

        // regex checks if any number exists
        if (wordLemma.contains("_") || wordLemma.matches(".*\\d+.*")) {
            return false;
        } else {
            return true;
        }
    }

    private void preparePointerToWordMap() {

        if (pointerToWordMap == null) {
            // 29 tane pointer var
            pointerToWordMap = new HashMap<IPointer, HashSet<IWord>>(32);
            for (POS partOfSpeech : POS.values()) {
                final Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(partOfSpeech);
                while (indexWordIterator.hasNext()) {
                    final IIndexWord iIndexWord = indexWordIterator.next();
                    this.addWordIDsToPointerMap(iIndexWord.getWordIDs());
                }
            }
        } else {
            log.info("Pointer and word mapping is already done.");
        }
    }

    private void addWordIDsToPointerMap(final List<IWordID> wordIDs) {

        final int totalWordsForWordID = wordIDs.size();
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

    private void addToPointerMap(IWord word, Set<IPointer> pointers) {

        for (IPointer relPtr : pointers) {
            HashSet<IWord> iWords = pointerToWordMap.get(relPtr);
            if (iWords == null) {
                iWords = new HashSet<IWord>() {{
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
            for (IWord word : words) {
                stringBuilder.append(iPointer.getName()).append("\nWord: ").append(word.getLemma()).append
                        (" Gloss: ").append(word.getSynset().getGloss()).append("\n");
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
                    strBuilder.append("\n\nWord: ").append(rootWord.getLemma());
                    final ISynset synset = rootWord.getSynset();
                    strBuilder.append(" Synset: ").append(synset.getGloss()).append("\nRoot Words: ");
                    for (IWord synWord : synset.getWords()) {
                        strBuilder.append(synWord.getLemma()).append(" * ");
                    }
                    final Map<IPointer, List<ISynsetID>> relatedMap = synset.getRelatedMap();
                    for (IPointer relPtr : relatedMap.keySet()) {
                        strBuilder.append("\n***********\nPointer: ").append(relPtr.getName());
                        for (ISynsetID relatedId : relatedMap.get(relPtr)) {
                            final ISynset relatedSynset = dict.getSynset(relatedId);
                            strBuilder.append("\nRelated Synset: ").append(relatedSynset
                                    .getGloss()).append("\nWords: ");
                            for (IWord synWord : relatedSynset.getWords()) {
                                strBuilder.append(synWord.getLemma()).append(" * ");
                            }
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
                    strBuilder.append("\n\nWord: ").append(rootWord.getLemma()).append(" - ").append(rootWord
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

    @Override
    public void listAdjectives() {

    }

    @Override
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

    public CalculatorInt getCalc() {

        return calc;
    }

}

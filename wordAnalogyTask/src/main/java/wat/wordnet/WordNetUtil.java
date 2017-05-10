package wat.wordnet;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.item.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.calculator.Calculator;
import wat.helper.DefaultSettingValues;
import wat.helper.WordNetPointers;
import wat.training.model.BaseModelInt;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class WordNetUtil implements WordNetUtilInt {

    // IndexWord içindeki wordID listesi Arrays$ArrayList tipinde bir liste yani array'le implement edili
    // Word veya Word'ün içineki Synset'in içindeki word listesi de ArrayList, yine arka tarafta array var

    private static final Logger log = LoggerFactory.getLogger(WordNetUtil.class);
    private static final boolean debugEnabled = log.isDebugEnabled();

    // kullanılmıyor
    // private HashMap<Set<IPointer>, HashSet<IWord>> multiplePointersToWordMap = null;

    /**
     * holds mapping of pointers in {@link #analogyTypes} to words.
     */
    private HashMap<IPointer, HashSet<IWord>> pointerToWordMap = null;

    /**
     * while doing analogy test, this setting controls how many words of a root word's pointer are going
     * to be checked for a word pair. if it is set to a high number (like 200.000), every word of a pointer
     * will be sent to analogy test which is not very efficient computationally.
     */
    private int iterationCapForPointer = DefaultSettingValues.ITERATION_CAP_FOR_POINTER;

    /**
     * setting to include phrases (connected with '_') while calculating analogy and similarity scores.
     */
    private boolean dontComparePhrases = DefaultSettingValues.DONT_COMPARE_PHRASES;

    private IRAMDictionary dict = null;
    private Calculator calc = new Calculator();

    private HashSet<WordNetPointers> analogyTypes = new HashSet<WordNetPointers>(21) {{
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
        add(WordNetPointers.VERB_GROUP);
//        add(WordNetPointers.ALSO_SEE);
//        add(WordNetPointers.TOPIC);
//        add(WordNetPointers.TOPIC_MEMBER);
//        add(WordNetPointers.USAGE);
//        add(WordNetPointers.USAGE_MEMBER);
//        add(WordNetPointers.REGION);
//        add(WordNetPointers.REGION_MEMBER);
        // lexical relationships
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
     * opens the {@link #dict WordNet dictionary} if it is not already open.
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
     * closes the {@link #dict WordNet dictionary} if it is open.
     */
    @Override
    public void closeDictionary() {
        // dict.isOpen() kontrolüne göre yok zira wordnet state'i zaten
        // kapalı veya o anda kapanıyorsa hiçbir şey yapmıyor
        dict.close();
        log.info("Dictionary is closed.");
    }

    /**
     * loads the {@link #dict WordNet dictionary} into memory for querying words quicker.
     */
    @Override
    public void loadDictionaryIntoMemory() {
        // wordnet'i kapatmaya gerek yok sanırım LoadPolicy'yi değiştirmeyince
        if (!dict.isLoaded()) {
            log.info("Loading wordnet into memory...");
            long start = System.currentTimeMillis();
            try {
                // true ile çağırınca loading'in bitmesini bekliyor method call
                dict.load(true);
            } catch (InterruptedException e) {
                log.error("Dictionary load process is interrupted, it may not be loaded properly!", e);
            }
            log.info("Loading is done in " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
        } else {
            log.info("Dictionary is already loaded.");
        }
    }

    /**
     * this method calls {@link #preparePointerToWordMap()} before calculating analogy score.
     *
     * @param usedModel     can be glove or word2vec usedModel.
     * @param isAnalogyTest true to calculate analogical relationship.
     */
    @Override
    public void calculateScoreForAllWords(final BaseModelInt usedModel,
            final boolean isAnalogyTest) {

        this.preparePointerToWordMap();
        long started = System.currentTimeMillis();
        for (POS partOfSpeech : POS.values()) {
            this.calculateScoreForPOS(usedModel, partOfSpeech, isAnalogyTest);
        }
        log.info(((System.currentTimeMillis() - started) / 1000)
                + " seconds passed while calculating score for all words."
                + "\nAnalogy Score: " + calc.getAnalogicalPercentage());
    }

    /**
     * calls {@link #preparePointerToWordMap()} before
     * calling {@link #calculateScoreForPOS(BaseModelInt, POS, boolean)}.
     */
    @Override
    public void calculateScoreForPOSFromController(final BaseModelInt usedModel,
            final POS partOfSpeech, final boolean isAnalogyTest) {

        this.preparePointerToWordMap();
        long started = System.currentTimeMillis();
        this.calculateScoreForPOS(usedModel, partOfSpeech, isAnalogyTest);
        log.info(((System.currentTimeMillis() - started) / 1000)
                + " seconds passed while calculating score for " + partOfSpeech.toString()
                + "\nAnalogy Score: " + calc.getAnalogicalPercentage());
    }

    /**
     * @param usedModel     can be glove or word2vec usedModel.
     * @param partOfSpeech  can be noun, verb, adjective or adverb.
     * @param isAnalogyTest true to calculate analogical relationship.
     */
    private void calculateScoreForPOS(final BaseModelInt usedModel,
            final POS partOfSpeech, final boolean isAnalogyTest) {

        if (isAnalogyTest) {
            this.calculateAnalogyScoreOfWordIterator(usedModel,
                    dict.getIndexWordIterator(partOfSpeech));
        } else {
            this.calculateSimilarityScoreOfWordIterator(usedModel,
                    dict.getIndexWordIterator(partOfSpeech));
        }
    }

    /**
     * calculates similarity score by summing word2vec's similarity score
     * for given word and its synset's words as pairs.
     *
     * @param usedModel
     * @param indexWordIterator
     */
    private void calculateSimilarityScoreOfWordIterator(final BaseModelInt usedModel,
            final Iterator<IIndexWord> indexWordIterator) {

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
     * this method calls {@link #preparePointerToWordMap()} before calculating analogy score.
     *
     * @param usedModel
     * @param wordInput
     */
    @Override
    public void calculateAnalogyScoreOfWordInput(final BaseModelInt usedModel,
            final String wordInput) {

        long start = System.currentTimeMillis();
        if (this.validateWord(wordInput)) {
            if (usedModel.hasWord(wordInput)) {
                IIndexWord indexWord = null;
                final int totalPOS = POS.values().length;

                // verilen kelimenin karşılığı olan indexWord'ü bul
                for (int i = 1; i < totalPOS && indexWord == null; i++) {
                    indexWord = dict.getIndexWord(wordInput, POS.getPartOfSpeech(i));
                }

                if (indexWord == null) {
                    log.warn(wordInput + " could not be found in WordNet.");
                } else {
                    this.preparePointerToWordMap();
                    this.calculateAnalogyScoreOfIndexWord(usedModel, indexWord);
                    log.info("Took " + (System.currentTimeMillis() - start) / 1000
                            + " seconds for word: " + wordInput
                            + "\nAnalogy Score: " + calc.getAnalogicalPercentage());
                }
            } else if (!debugEnabled) {
                log.info(wordInput + " is not in model's vocabulary.");
            }
        } else if (!debugEnabled) {
            log.info(wordInput + " is not a valid word.");
        }
    }

    private void calculateAnalogyScoreOfWordIterator(final BaseModelInt usedModel,
            final Iterator<IIndexWord> indexWordIterator) {

        int i = 1;
        while (indexWordIterator.hasNext()) {
            if (i++ % 100 == 0) {
                log.info("Done iterating " + i + " words, analogy score: "
                        + calc.getAnalogicalPercentage());
            }
            final IIndexWord indexWord = indexWordIterator.next();

            // kelime word2vec'e yollanmaya uygun mu
            if (this.validateWord(indexWord.getLemma())) {
                // kelime word2vec sözlüğünde kayıtlı mı
                if (usedModel.hasWord(indexWord.getLemma())) {
                    this.calculateAnalogyScoreOfIndexWord(usedModel, indexWord);
                } else {
                    if (debugEnabled) {
                        log.debug(indexWord.getLemma() + " is not in model's vocabulary.");
                    }
                }
            } else {
                if (debugEnabled) {
                    log.debug(indexWord.getLemma() + " is not a valid word.");
                }
            }
        }
    }

    /**
     * indexWord should have been checked before coming to this method.
     *
     * @param usedModel
     * @param indexWord
     */
    private void calculateAnalogyScoreOfIndexWord(final BaseModelInt usedModel,
            final IIndexWord indexWord) {

        final List<IWordID> wordIDs = indexWord.getWordIDs();
        int totalWordsForWordID = wordIDs.size();
        for (int i = 0; i < totalWordsForWordID; i++) {
            // esas işlenen kelime
            final IWord rootWord = dict.getWord(wordIDs.get(i));
            this.calculateAnalogicalAccuracyOfOneWord(usedModel, rootWord, rootWord.getLemma());
        }
        if (debugEnabled) {
            log.debug("Analogy score after index word: " + calc.getAnalogicalPercentage());
        }
    }

    private void calculateAnalogicalAccuracyOfOneWord(final BaseModelInt usedModel,
            final IWord rootWord, final String rootWordLemma) {

        this.calculateLexicalAnalogy(usedModel, rootWord.getRelatedMap(), rootWordLemma);
        this.calculateSemanticAnalogy(usedModel, rootWord.getSynset().getRelatedMap(), rootWordLemma);
    }

    /**
     * analogic pair is root word + related word of root word.
     *
     * @param usedModel
     * @param relatedWordMap
     * @param rootWordLemma
     */
    private void calculateLexicalAnalogy(final BaseModelInt usedModel,
            final Map<IPointer, List<IWordID>> relatedWordMap, final String rootWordLemma) {

        String relatedWordLemma, comparedWordLemma;
        for (IPointer iPointer : relatedWordMap.keySet()) {

            final HashSet<IWord> wordsOfPointer = pointerToWordMap.get(iPointer);

            // pointer analojik olarak manalıysa
            if (wordsOfPointer != null) {
                // root kelimenin lexical yakın kelimeleri
                final List<IWordID> lexicallyRelatedWordIDs = relatedWordMap.get(iPointer);
                int relatedWordSizeForPointer = lexicallyRelatedWordIDs.size();

                for (int k = 0; k < relatedWordSizeForPointer; k++) {
                    final IWord relatedWord = dict.getWord(lexicallyRelatedWordIDs.get(k));
                    relatedWordLemma = relatedWord.getLemma();

                    // related kelime ile root aynı olmamalı
                    if (rootWordLemma.equalsIgnoreCase(relatedWordLemma)) {
                        if (debugEnabled) {
                            log.debug(relatedWordLemma + " is same as root word, it cannot be checked.");
                        }
                    } else if (this.validateWord(relatedWordLemma)
                            && usedModel.hasWord(relatedWordLemma)) {

                        int counter = 0;
                        final Iterator<IWord> iterator = wordsOfPointer.iterator();
                        while (iterator.hasNext() && counter < iterationCapForPointer) {
                            counter++;
                            final IWord comparedWord = iterator.next();
                            comparedWordLemma = comparedWord.getLemma();

                            if (usedModel.hasWord(comparedWordLemma)) {

                                // kıyaslanan kelime root veya related'la aynı olamaz
                                if (comparedWordLemma.equalsIgnoreCase(rootWordLemma)
                                        || comparedWordLemma.equalsIgnoreCase(relatedWordLemma)) {
                                    if (debugEnabled) {
                                        log.debug("Word to be compared: " + comparedWordLemma
                                                + " is same as one of the words in pair: "
                                                + rootWordLemma + " - " + relatedWordLemma);
                                    }
                                } else {
                                    final List<IWordID> relatedWordsOfCompared =
                                            comparedWord.getRelatedWords(iPointer);

                                    this.compareWordPairWithGivenThird(usedModel,
                                            this.getWordsFromWordIDs(relatedWordsOfCompared),
                                            rootWordLemma, relatedWordLemma, comparedWordLemma);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * analogic pair is root word + synset's word of root word.
     *
     * @param usedModel
     * @param relatedSynsetMap
     * @param rootWordLemma
     */
    private void calculateSemanticAnalogy(final BaseModelInt usedModel,
            final Map<IPointer, List<ISynsetID>> relatedSynsetMap, final String rootWordLemma) {

        // rootWordLemma + synsetWordLemma analojik pair
        String synsetWordLemma;

        // kelimenin synsetindeki her ilişki için yap
        for (IPointer iPointer : relatedSynsetMap.keySet()) {

            // o andaki pointer'a sahip bütün kelimeleri çek
            final HashSet<IWord> wordsOfPointer = pointerToWordMap.get(iPointer);
            // pointer karşılaştırma için uygunsa
            if (wordsOfPointer != null) {
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
                        if (rootWordLemma.equalsIgnoreCase(synsetWordLemma)) {
                            if (debugEnabled) {
                                log.debug(synsetWordLemma + " is same as root word, it cannot be checked.");
                            }
                        } else if (this.validateWord(synsetWordLemma)
                                && usedModel.hasWord(synsetWordLemma)) {

                            int counter = 0;
                            final Iterator<IWord> iterator = wordsOfPointer.iterator();
                            while (iterator.hasNext() && counter < iterationCapForPointer) {
                                counter++;
                                this.calculateSemanticAnalogyWithThirdWord(usedModel, iterator.next(),
                                        rootWordLemma, synsetWordLemma);
                            }
                        }
                    }
                }
            }
        }
    }

    private void calculateSemanticAnalogyWithThirdWord(final BaseModelInt usedModel,
            final IWord comparedWord, final String rootWordLemma, final String synsetWordLemma) {

        final String comparedWordLemma = comparedWord.getLemma();

        if (usedModel.hasWord(comparedWordLemma)) {

            if (comparedWordLemma.equalsIgnoreCase(rootWordLemma) || comparedWordLemma
                    .equalsIgnoreCase(synsetWordLemma)) {
                if (debugEnabled) {
                    log.debug("Word to be compared: " + comparedWordLemma
                            + " is same as one of the words in pair: "
                            + rootWordLemma + " - " + synsetWordLemma);
                }
            } else {
                // kelimenin synseti yukarıdan gelen pointer'a sahip mi
                // iyi test edilmeli
                final List<IWord> synsetWordsOfCompared = comparedWord.getSynset()
                        .getWords();

                this.compareWordPairWithGivenThird(usedModel, synsetWordsOfCompared,
                        rootWordLemma, synsetWordLemma, comparedWordLemma);
            }
        }
    }

    /**
     * @param wordIDs
     * @return IWord equivelant of given IWordIDs.
     */
    private List<IWord> getWordsFromWordIDs(final List<IWordID> wordIDs) {

        List<IWord> words = new ArrayList<>(wordIDs.size());
        wordIDs.forEach(wordID -> words.add(dict.getWord(wordID)));
        return words;
    }

    /**
     * all three words are for sure in model's vocabulary at the point
     * when there is a call to this method.
     *
     * @param usedModel
     * @param relatedWordsOfCompared
     * @param rootWordLemma          root word that has started iteration.
     * @param pairWordLemma          related word of root either lexically or semantically.
     * @param comparedWordLemma      third word to be compared.
     */
    private void compareWordPairWithGivenThird(final BaseModelInt usedModel,
            final List<IWord> relatedWordsOfCompared, final String rootWordLemma,
            final String pairWordLemma, final String comparedWordLemma) {

        if (relatedWordsOfCompared.isEmpty()) {

            log.error(comparedWordLemma + " does not have any related words when it should have!");
        } else {
            // word2vec sorgusu, sorgulanan kelimenin tüm related kelimeleri için tekrar tekrar
            // yapılmasın diye burada
            final List<String> closestWords = usedModel.getClosestWords(Arrays.asList(rootWordLemma,
                    pairWordLemma), Arrays.asList(comparedWordLemma));

            int relatedWordSize = relatedWordsOfCompared.size();
            for (int i = 0; i < relatedWordSize; i++) {

                // update analogy score and remove the word from closestWords list if there is a match
                calc.updateAnalogicalAccuracy(relatedWordsOfCompared.get(i).getLemma(), closestWords);
            }
        }
    }

    /**
     * creates and fills {@link #pointerToWordMap} by {@link #mapPointerToWord(IWord, Set)}.
     */
    public void preparePointerToWordMap() {

        if (pointerToWordMap == null) {
            log.info("Creating pointer-word map...");
            // map'teki toplam kelime sayısı
            int wordCounter = 0;
            // toplamda 29 tane pointer var
            pointerToWordMap = new HashMap<>(32);
            for (POS partOfSpeech : POS.values()) {
                final Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(partOfSpeech);
                while (indexWordIterator.hasNext()) {
                    final IIndexWord indexWord = indexWordIterator.next();
                    // word2vec'de kontrol etmek istemediğimiz kelimeleri eklemesin, gerek yok.
                    if (this.validateWord(indexWord.getLemma())) {
                        wordCounter += this.addWordIDsToPointerMap(indexWord.getWordIDs());
                    }
                }
            }
            log.info(wordCounter + " words are mapped to pointers.");
        } else {
            log.info("Pointer to word mapping is already done.");
        }
    }

    private int addWordIDsToPointerMap(final List<IWordID> wordIDs) {

        final int totalWordsForWordID = wordIDs.size();
        for (int i = 0; i < totalWordsForWordID; i++) {
            // bir kelimenin farklı anlamları varsa farklı ID ile farklı Word objelerinde
            // birden çok olabiliyor
            final IWord rootWord = dict.getWord(wordIDs.get(i));
            this.addToOnePointerMap(rootWord);
        }
        return totalWordsForWordID;
    }

    private void addToOnePointerMap(IWord rootWord) {
        // lexical pointer'ın setine koy
        this.mapPointerToWord(rootWord, rootWord.getRelatedMap().keySet());
        // semantic pointer'ın setine koy
        this.mapPointerToWord(rootWord, rootWord.getSynset().getRelatedMap().keySet());
    }

    private void mapPointerToWord(IWord word, Set<IPointer> pointers) {

        for (IPointer pointer : pointers) {
            if (analogyTypes.contains(WordNetPointers.getByCode(pointer.getSymbol()))) {
                HashSet<IWord> iWords = pointerToWordMap.get(pointer);
                if (iWords == null) {
                    iWords = new HashSet<IWord>() {{
                        add(word);
                    }};
                    pointerToWordMap.put(pointer, iWords);
                } else {
                    iWords.add(word);
                }
            }
        }
    }

    /**
     * creates and fills {@link #multiplePointersToWordMap} by {@link #mapWordWithItsPointers(IWord, Set)}.
     */
/*    public void prepareMultiplePointersToWordMap() {

        if (multiplePointersToWordMap == null) {
            log.info("Creating pointers of word to word map...");
            // map'teki toplam kelime sayısı
            int wordCounter = 0;

            // toplamda 27 tane pointer kaydediliyor
            multiplePointersToWordMap = new HashMap<>(512);
            for (POS partOfSpeech : POS.values()) {
                final Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(partOfSpeech);
                while (indexWordIterator.hasNext()) {
                    final IIndexWord indexWord = indexWordIterator.next();
                    // word2vec'de kontrol etmek istemediğimiz kelimeleri eklemesin, gerek yok.
                    if (this.validateWord(indexWord.getLemma())) {

                        final List<IWordID> wordIDs = indexWord.getWordIDs();
                        final int totalWordsForWordID = wordIDs.size();

                        for (int i = 0; i < totalWordsForWordID; i++) {
                            // bir kelimenin farklı anlamları varsa farklı ID
                            // ile farklı Word objelerinde birden çok olabiliyor
                            final IWord rootWord = dict.getWord(wordIDs.get(i));
                            // lexical pointer'ın setine koy
                            this.mapWordWithItsPointers(rootWord, rootWord.getRelatedMap().keySet());
                            // semantic pointer'ın setine koy
                            this.mapWordWithItsPointers(rootWord,
                                    rootWord.getSynset().getRelatedMap().keySet());
                        }
                        wordCounter += totalWordsForWordID;
                    }
                }
            }
            log.info(wordCounter + " words are mapped.");
        }
    }

    private void mapWordWithItsPointers(IWord word, Set<IPointer> pointers) {

        HashSet<IWord> iWords = multiplePointersToWordMap.get(pointers);
        if (iWords == null) {
            iWords = new HashSet<IWord>() {{
                add(word);
            }};
            multiplePointersToWordMap.put(pointers, iWords);
        } else {
            iWords.add(word);
        }
    } */

    /**
     * WordNet has phrases which are connected to each other by '_'. Also it has words as '.22' or '10'
     * that we don't want to check for analogy. (method is public for testing)
     *
     * @param wordLemma
     * @return false if the word is not valid to send to model.
     */
    public boolean validateWord(final String wordLemma) {

        // regex checks if a number exists
        if ((dontComparePhrases && wordLemma.contains("_"))
                || wordLemma.matches(".*\\d+.*")) {
            if (debugEnabled) {
                log.debug(wordLemma + " is not a valid word.");
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void listPointerMap() {

        int counter = 1;
        StringBuilder strBuilder = new StringBuilder(10000);
        long start = System.currentTimeMillis();
        this.preparePointerToWordMap();
        log.info("Seconds passed to prepare map: " + (System.currentTimeMillis() - start) / 1000);
        for (IPointer iPointer : pointerToWordMap.keySet()) {
            HashSet<IWord> words = pointerToWordMap.get(iPointer);
            for (IWord word : words) {
                strBuilder.append("\n\n").append(iPointer.getName()).append("\nWord: ")
                        .append(word.getLemma()).append(" - ").append(word.getSynset().getGloss());
                if ((counter++) % 4000 == 1) {
                    log.info(strBuilder.toString());
                    strBuilder = new StringBuilder(10000);
                }
            }
        }
        log.info(strBuilder.toString());
    }

    @Override
    public void listWordsSemanticPointers() {

        int counter = 1;
        StringBuilder strBuilder = new StringBuilder(10000);
        for (POS partOfSpeech : POS.values()) {
            final Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(partOfSpeech);
            while (indexWordIterator.hasNext()) {
                if ((counter++) % 4000 == 1) {
                    log.info(strBuilder.toString());
                    strBuilder = new StringBuilder(10000);
                }
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
        }
        log.info(strBuilder.toString());
    }

    @Override
    public void listWordsLexicalPointers() {

        int counter = 1;
        StringBuilder strBuilder = new StringBuilder(10000);
        for (POS partOfSpeech : POS.values()) {
            final String pos = partOfSpeech.name();
            final Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(partOfSpeech);
            while (indexWordIterator.hasNext()) {
                if ((counter++) % 4000 == 1) {
                    log.info(strBuilder.toString());
                    strBuilder = new StringBuilder(10000);
                }
                final IIndexWord iIndexWord = indexWordIterator.next();
                final List<IWordID> wordIDs = iIndexWord.getWordIDs();
                int totalWordsForWordID = wordIDs.size();
                for (int i = 0; i < totalWordsForWordID; i++) {
                    // bir kelimenin farklı anlamları varsa farklı ID ile farklı Word objelerinde
                    // birden çok olabiliyor
                    final IWordID wordID = wordIDs.get(i);
                    // o anda process edilen kelime
                    final IWord rootWord = dict.getWord(wordID);
                    strBuilder.append("\n\n(").append(pos).append(") Word: ").append(rootWord.getLemma())
                            .append(" - ").append(rootWord.getSynset().getGloss())
                            .append(" - Lexical ID: ").append(rootWord.getLexicalID());
                    final Map<IPointer, List<IWordID>> relatedMap = rootWord.getRelatedMap();
                    for (IPointer relPtr : relatedMap.keySet()) {
                        strBuilder.append("\nPointer: ").append(relPtr.getName());
                        for (IWordID iWordID : relatedMap.get(relPtr)) {
                            final IWord related = dict.getWord(iWordID);
                            strBuilder.append("\nRelated Word: ").append(related.getLemma())
                                    .append(" - ").append(related.getSynset().getGloss());
                        }
                    }
                }
            }
        }
        log.info(strBuilder.toString());
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
                log.info(iIndexWord.getLemma() + " has " + wordIDs.size() +
                        " different meanings.");
            }
            for (IWordID wordID : wordIDs) {
                IWord word = dict.getWord(wordID);
                log.info("Current word:");
                logWord(word);
                log.info("Words of synset:");
                logWords(word.getSynset().getWords());
                log.info("Related words:");
                this.logWordIDList(word.getRelatedWords());
            }
            log.info("**********************************************************");
        }
    }

    @Override
    public void listVerbs() {

        Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(POS.VERB);
        while (indexWordIterator.hasNext()) {
            IIndexWord iIndexWord = indexWordIterator.next();
            this.logWordIDList(iIndexWord.getWordIDs());
            log.info("**********************************************************");
        }
    }

    @Override
    public void listAdjectives() {

        Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(POS.ADJECTIVE);
        while (indexWordIterator.hasNext()) {
            IIndexWord iIndexWord = indexWordIterator.next();
            this.logWordIDList(iIndexWord.getWordIDs());
            log.info("**********************************************************");
        }
    }

    @Override
    public void listAdverbs() {

        Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(POS.VERB);
        while (indexWordIterator.hasNext()) {
            IIndexWord iIndexWord = indexWordIterator.next();
            this.logWordIDList(iIndexWord.getWordIDs());
            log.info("**********************************************************");
        }
    }

    private void logWordIDList(List<IWordID> words) {

        words.forEach(wordID -> this.logWord(dict.getWord(wordID)));
    }

    private void logWords(List<IWord> words) {

        words.forEach(iWord -> this.logWord(iWord));
    }

    private void logWord(IWord word) {

        log.info("Lemma: " + word.getLemma() + " Lexical ID: " + word.getLexicalID()
                + " Adjective Marker: " + word.getAdjectiveMarker()
                + " Verb Frames: " + word.getVerbFrames().toString()
                + " Synset Type: " + word.getSenseKey().getSynsetType());
    }

    @Override
    public Calculator getCalc() {

        return calc;
    }

    @Override
    public void setIterationCapForPointer(int iterationCap) {

        if (iterationCap > DefaultSettingValues.ITERATION_CAP_FOR_POINTER) {
            log.warn("Iteration cap for a pointer is set too high: " + iterationCap);
        }
        this.iterationCapForPointer = iterationCap;
    }

    @Override
    public void setPhraseComparisonSetting(boolean dontComparePhrases) {

        if (dontComparePhrases) {
            log.info("Phrases are excluded while calculating scores.");
        } else {
            log.info("Phrases are now allowed while calculating scores.");
        }
        this.dontComparePhrases = dontComparePhrases;
    }

}

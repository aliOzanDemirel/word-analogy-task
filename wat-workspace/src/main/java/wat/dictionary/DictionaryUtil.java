package wat.dictionary;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IPointer;
import edu.mit.jwi.item.ISenseEntry;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wat.calculator.AccuracyCalculatorInt;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class DictionaryUtil implements DictionaryUtilInt {

    // IndexWord içindeki wordID listesi Arrays$ArrayList tipinde bir liste yani array'le implement edili
    // Word veya Word'ün içineki Synset'in içindeki word listesi de ArrayList, yine arka tarafta array var

    private static final Logger log = LoggerFactory.getLogger(DictionaryUtil.class);
    private static boolean debugEnabled = log.isDebugEnabled();
    protected IRAMDictionary dict = null;

    public DictionaryUtil(String path, int loadPolicy) throws MalformedURLException {
        if (debugEnabled) {
            log.debug("NO_LOAD: 2, BACKGROUND_LOAD = 4, IMMEDIATE_LOAD = 8 - " +
                    "Dictionary is being loaded with policy: " + loadPolicy);
        }
        dict = new RAMDictionary(new URL("file", null, path), loadPolicy);
    }

    /**
     * opens the dictionary if it is not already open.
     */
    protected void openDictionary() throws IOException {
        // dict.isOpen() kontrolüne göre yok zira dictionary state'i zaten
        // açık veya düzgün bir şekilde açık yapılınca true dönüyor
        if (dict.open()) {
            log.info("Dictionary is opened.");
        } else {
            log.warn("Dictionary is already opened.");
        }
    }

    /**
     * closes the dictionary if it is open.
     */
    @Override
    public void closeDictionary() {
        // dict.isOpen() kontrolüne göre yok zira dictionary state'i zaten
        // kapalı veya o anda kapanıyorsa hiçbir şey yapmıyor
        dict.close();
        log.info("Dictionary is closed.");
    }

    @Override
    public void loadDictionaryIntoMemory() {
        // setLoadPolicy'ye gerek yok sanırım
        // true ile çağırınca loading'in bitmesini bekliyor method call
        log.info("Loading dictionary into memory.");
        try {
            dict.load(true);
            log.info("Loading is done.");
        } catch (InterruptedException e) {
            log.error("Dictionary load process is interrupted, it may not be loaded properly!", e);
        }
    }

    /**
     * iterates through all parts of speeches.
     *
     * @param calculator can be glove or word2vec calculator.
     * @throws IOException when WordNet file can't be opened.
     */
    @Override
    public void calculateAccuracyForAllWords(AccuracyCalculatorInt calculator) throws IOException {
        this.openDictionary();
        long started = System.currentTimeMillis();
        for (POS partOfSpeech : POS.values()) {
            this.calculateAccuracyForGivenWords(calculator, dict.getIndexWordIterator(partOfSpeech));
        }
        long timePassed = (System.currentTimeMillis() - started) / 1000;
        if (debugEnabled) {
            log.debug("Time passed in seconds while iterating through all words: " + timePassed);
        }
    }

    /**
     * @param calculator   can be glove or word2vec calculator.
     * @param partOfSpeech can be noun, verb, adjective or adverb.
     * @throws IOException
     */
    @Override
    public void calculateAccuracyForGivenPOS(AccuracyCalculatorInt calculator, POS partOfSpeech) throws IOException {
        this.openDictionary();
        long started = System.currentTimeMillis();
        this.calculateAccuracyForGivenWords(calculator, dict.getIndexWordIterator(partOfSpeech));
        long timePassed = (System.currentTimeMillis() - started) / 1000;
        if (debugEnabled) {
            log.debug("Time passed in seconds while iterating through given POS: " + timePassed);
        }
        this.closeDictionary();
    }

    private void calculateAccuracyForGivenWords(AccuracyCalculatorInt calculator,
            Iterator<IIndexWord> indexWordIterator) throws IOException {
        this.openDictionary();
        // bir kelimeyi bir kerede loglamak için
//        StringBuilder logMsg = new StringBuilder(120);
        List<IWordID> wordIDs;
        IIndexWord iIndexWord;
        ISynset synset;
        // o anda process edilen kelime
        IWord rootWord;

        int totalWordsForWordID;
        while (indexWordIterator.hasNext()) {
            iIndexWord = indexWordIterator.next();
//            if (iIndexWord.getWordIDs().get(0).getLemma().startsWith("c")) {
//                throw new RuntimeException("C ile başlayanlarda bitsin diye");
//            }
            wordIDs = iIndexWord.getWordIDs();
            totalWordsForWordID = wordIDs.size();
//            logMsg.append(iIndexWord.getLemma()).append(" has ")
//                    .append(totalWordsForWordID).append(" different meaning(s)");

            for (int i = 0; i < totalWordsForWordID; i++) {
                // bir kelimenin farklı anlamları varsa farklı ID ile farklı Word objelerinde
                // birden çok olabiliyor, buradaki counter farklı ID'deki aynı kelimeler için
                IWordID wordID = wordIDs.get(i);
                rootWord = dict.getWord(wordID);
                synset = rootWord.getSynset();
//                logMsg.append("\nSynset gloss: " + synset.getGloss() + ", type: " + synset.getType());
                this.calculateWordAccuracyWithItsSynset(calculator, rootWord.getLemma(), synset.getWords());
            }
//            log.debug(logMsg.toString());
            log.info("Updated accuracy: " + calculator.updateAndGetAccuracyPercentage());
        }
        this.closeDictionary();
    }

    private void calculateWordAccuracyWithItsSynset(AccuracyCalculatorInt calculator, String rootWordLemma,
            List<IWord> synWords) {
        // rootun synonimi olan kelime
        IWord synWord;
        for (int k = 0; k < synWords.size(); k++) {
            synWord = synWords.get(k);
            calculator.updateAccuracy(rootWordLemma, synWord.getLemma());
        }
    }

    private void calculateWordAccuracy(AccuracyCalculatorInt calculator, String rootWordLemma,
            List<IWord> synWords) {
        // rootun synonimi olan kelime

        IWord synWord;
        for (int k = 0; k < synWords.size(); k++) {
            synWord = synWords.get(k);
            calculator.updateAccuracy(rootWordLemma, synWord.getLemma());
        }
    }

    @Override
    public void listSenseKeyAndSynsetsOfAdjectives() throws IOException {
        this.openDictionary();
        Iterator<ISenseEntry> senseEntryIterator = dict.getSenseEntryIterator();
        StringBuilder strBuilder = new StringBuilder();
        for (ISenseEntry iSenseEntry = senseEntryIterator.next(); senseEntryIterator.hasNext(); iSenseEntry =
                senseEntryIterator.next()) {
            strBuilder.append("Lemma: ").append(iSenseEntry.getSenseKey().getLemma()).append(" Synset Type: ").append
                    (iSenseEntry.getSenseKey().getSynsetType()).append(" Head Word: ").append(iSenseEntry.getSenseKey()
                    .getHeadWord()).append(" Sense Number ").append(iSenseEntry.getSenseNumber());
        }
        log.info(strBuilder.toString());
        log.info("Adjective Synsets: ");
        final Iterator<ISynset> synsetIterator = dict.getSynsetIterator(POS.ADJECTIVE);
        while (synsetIterator.hasNext()) {
            ISynset iSynset = synsetIterator.next();
            log.info(iSynset.getGloss());
            for (IWord word : iSynset.getWords()) {
                log.info(word.getLemma() + " " + word.getAdjectiveMarker());
            }
        }
        this.closeDictionary();
    }

    @Override
    public void listNounsWithPointers() throws IOException {
        this.openDictionary();
        List<IWordID> wordIDs;
        IIndexWord iIndexWord;
        final Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(POS.NOUN);
        while (indexWordIterator.hasNext()) {
            iIndexWord = indexWordIterator.next();
//            if (iIndexWord.getWordIDs().get(0).getLemma().startsWith("c")) {
//                throw new RuntimeException("C ile başlayanlarda bitsin diye");
//            }
            log.debug("Pointers:");
            for (IPointer iPointer : iIndexWord.getPointers()) {
                log.debug(iPointer.getName());
            }
            wordIDs = iIndexWord.getWordIDs();
            if (wordIDs.size() > 1) {
                log.debug(iIndexWord.getLemma() + " has " + iIndexWord.getWordIDs().size() + " different meanings.");
            }
            for (IWordID wordID : wordIDs) {
                IWord word = dict.getWord(wordID);
                logWord(word);
                log.debug("Words of synset:");
                logWords(word.getSynset().getWords());
                log.debug("Related words:");
                this.logWordIDList(word.getRelatedWords());
            }
            log.debug("********************************");
        }
        this.closeDictionary();
    }

    @Override
    public void listVerbs() throws IOException {
        this.openDictionary();
        Iterator<IIndexWord> indexWordIterator = dict.getIndexWordIterator(POS.VERB);
        while (indexWordIterator.hasNext()) {
            IIndexWord iIndexWord = indexWordIterator.next();
//            if (iIndexWord.getWordIDs().get(0).getLemma().startsWith("c")) {
//                throw new RuntimeException("C ile başlayanlarda bitsin diye");
//            }
            List<IWordID> wordIDs = iIndexWord.getWordIDs();
            if (wordIDs.size() > 1) {
                log.debug(iIndexWord.getLemma() + " has " + iIndexWord.getWordIDs().size() + " different meanings.");
            }
            for (IWordID wordID : wordIDs) {
                IWord word = dict.getWord(wordID);
                logWord(word);
            }
            log.debug("**********************************************************");
        }
        this.closeDictionary();
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
        log.info("Lemma: " + word.getLemma() + " Lexical ID: " + word.getLexicalID() + " Adjective Marker: " + word
                .getAdjectiveMarker() + " Verb Frames: " + word.getVerbFrames().toString());
    }

}

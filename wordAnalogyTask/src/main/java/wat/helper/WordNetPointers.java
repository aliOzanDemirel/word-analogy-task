package wat.helper;

public enum WordNetPointers {

    // commenttekiler jwi manual'den alınma eski olabilir
    // both:
    // ALSO_SEE, USAGE, USAGE_MEM
    // VERB_GROUP (2 lexical)
    // TOPIC (11 lexical), TOPIC_MEM (11 lexical)
    // REGION (15 lexical), REGION_MEM (15 lexical)
    // lexical:
    // ANTONYM, PERTAINYM, PARTICIBLE
    // DERIVED, DERIVED_ADJ
    // semantic:
    // HYPERNYM, HYPERNYM_IN
    // HYPONYM, HYPONYM_INS
    // HOLONYM_MEM, HOLONYM_PRT, HOLONYM_SUB
    // MERONYM_MEM, MERONYM_PRT, MERONYM_SUB
    // SIMILAR_TO, CAUSE, ENTAILMENT, ATTRIBUTE

    ALSO_SEE("^"),
    ANTONYM("!"),
    ATTRIBUTE("="),
    CAUSE(">"),
    DERIVATIONALLY_RELATED("+"),
    DERIVED_FROM_ADJ("\\"),
    DOMAIN(";"),
    ENTAILMENT("*"),
    HYPERNYM("@"),
    HYPERNYM_INSTANCE("@i"),
    HYPONYM("~"),
    HYPONYM_INSTANCE("~i"),
    HOLONYM_MEMBER("#m"),
    HOLONYM_SUBSTANCE("#s"),
    HOLONYM_PART("#p"),
    MEMBER("-"),
    MERONYM_MEMBER("%m"),
    MERONYM_SUBSTANCE("%s"),
    MERONYM_PART("%p"),
    PARTICIPLE("<"),
    PERTAINYM("\\"),
    REGION(";r"),
    REGION_MEMBER("-r"),
    SIMILAR_TO("&"),
    TOPIC(";c"),
    TOPIC_MEMBER("-c"),
    USAGE(";u"),
    USAGE_MEMBER("-u"),
    VERB_GROUP("$");

    String value;

    /**
     * @param value
     */
    WordNetPointers(String value) {

        this.value = value;
    }

    public static WordNetPointers getByCode(final String code) {

        for (WordNetPointers pointer : WordNetPointers.values()) {
            if (pointer.value.equals(code)) {
                return pointer;
            }
        }
        throw new IllegalArgumentException(code + " is not a valid WordNetPointers!");
    }

}

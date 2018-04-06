public class NounPhrase {
    String nounPhrase;
    int sentenceNumber;

    public NounPhrase(String np, int sentNum) {
        nounPhrase = np;
        sentenceNumber =sentNum;
    }

    public int getSentenceNumber() {
        return sentenceNumber;
    }

    public String getNounPhrase() {
        return nounPhrase;
    }
}

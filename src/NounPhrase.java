public class NounPhrase {
    private String nounPhrase;
    private String sentenceNumber;
    private String chainNumber = "-1";

    public NounPhrase(String sentNum, String np) {
        nounPhrase = np;
        sentenceNumber = sentNum;
    }

    public NounPhrase(String chNum, String sentNum, String np) {
        nounPhrase = np;
        sentenceNumber =sentNum;
        chainNumber = chNum;
    }

    public String getSentenceNumber() {
        return sentenceNumber;
    }

    public String getNounPhrase() {
        return nounPhrase;
    }

    public String getChainNumber() {
        return chainNumber;
    }

    public void setChainNumber(String chainNumber) {
        this.chainNumber = chainNumber;
    }
}

public class NounPhrase {
    private String nounPhrase;
    private String sentenceNumber;
    private String chainNumber = "-1";
    private String categoria;

    public NounPhrase(String sentNum, String np) {
        nounPhrase = np;
        sentenceNumber = sentNum;
    }

    public NounPhrase(String chNum, String sentNum, String np, String cat) {
        nounPhrase = np;
        sentenceNumber =sentNum;
        chainNumber = chNum;
        categoria = cat;
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

    public String getCategoria() {
        return categoria;
    }

    public void setChainNumber(String chainNumber) {
        this.chainNumber = chainNumber;
    }
}

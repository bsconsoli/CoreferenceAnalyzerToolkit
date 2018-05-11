public class NounPhrase {
    private String nounPhrase;
    private String sentenceNumber;
    private String chainNumber = "-1";
    private String nucleus;
    private String categoria;

    public NounPhrase(String sentNum, String np, String nuc) {
        nounPhrase = np;
        sentenceNumber = sentNum;
        nucleus = nuc;
    }

    //public NounPhrase(String chNum,String sentNum, String np) {
    //    nounPhrase = np;
    //    sentenceNumber = sentNum;
    //    chainNumber = chNum;
    //}

    public NounPhrase(String chNum, String sentNum, String np, String cat, String nuc) {
        nounPhrase = np;
        sentenceNumber = sentNum;
        chainNumber = chNum;
        categoria = cat;
        nucleus = nuc;
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

    public String getNucleus() {
        return nucleus;
    }

    public void setChainNumber(String chainNumber) {
        this.chainNumber = chainNumber;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setNucleus(String nucleus) {
        this.nucleus = nucleus;
    }
}

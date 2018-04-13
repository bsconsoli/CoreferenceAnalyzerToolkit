import java.util.ArrayList;

public class NPChain {

    private String chainNumber;
    private ArrayList<String> uniqueMentions;
    private ArrayList<String> sentenceNums;

    public NPChain (String chNum, ArrayList<String> unMen, ArrayList<String> senNums){
        chainNumber = chNum;
        uniqueMentions = unMen;
        sentenceNums = senNums;
    }

    public ArrayList<String> getSentenceNums() {
        return sentenceNums;
    }

    public ArrayList<String> getUniqueMentions() {
        return uniqueMentions;
    }

    public String getChainNumber() {
        return chainNumber;
    }
}

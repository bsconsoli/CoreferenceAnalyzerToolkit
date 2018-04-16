import java.util.ArrayList;

public class NPChain {

    private String chainNumber;
    private ArrayList<NounPhrase> uniqueMentions;
    private ArrayList<Integer> uniqueMentionFreq;
    private ArrayList<String> sentenceNums;
    private Integer size;

    public NPChain (String chNum, ArrayList<NounPhrase> unMen, ArrayList<String> senNums, ArrayList<Integer> unMenFreq){
        chainNumber = chNum;
        uniqueMentions = unMen;
        sentenceNums = senNums;
        uniqueMentionFreq = unMenFreq;
        size = sentenceNums.size();
    }

    public ArrayList<String> getSentenceNums() {
        return sentenceNums;
    }

    public ArrayList<NounPhrase> getUniqueMentions() {
        return uniqueMentions;
    }

    public String getChainNumber() {
        return chainNumber;
    }

    public ArrayList<Integer> getUniqueMentionFreq() {
        return uniqueMentionFreq;
    }

    public Integer getSize() {
        return size;
    }

    public String getMostMentionedCategory(){
        int max = 0;
        for (int i:uniqueMentionFreq){
            if(i > max){
                max = i;
            }
        }
        return uniqueMentions.get(uniqueMentionFreq.indexOf(max)).getCategoria();
    }
}

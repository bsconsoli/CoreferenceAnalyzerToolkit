public class Token {
    int number;
    int sentenceNumber;
    String word;
    String pos;

    public Token(int num, int sentNum, String tokenWord, String posTag){
        number = num;
        sentenceNumber = sentNum;
        word = tokenWord;
        pos = posTag;
    }

    public int getNumber() {
        return number;
    }

    public int getSentenceNumber() {
        return sentenceNumber;
    }

    public String getPos() {
        return pos;
    }

    public String getWord() {
        return word;
    }
}

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

class SFNLPFrenchParser {

    public static void frenchParser(String frenchText) {
        LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/frenchFactored.ser.gz");

        ArrayList<NounPhrase> frenchNPs = parser(lp, frenchText);

        txtWriter.writeNounPhraseList("outputParser.txt", frenchNPs);

    }

    private static ArrayList<NounPhrase> parser(LexicalizedParser lp, String filename) {

        //ArrayList<Token> tokens = new ArrayList<>();
        ArrayList<NounPhrase> nounPhrases = new ArrayList<>();
        //int tokenNumber = 0;
        int sentenceNumber = 1;
        for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
            Tree parse = lp.apply(sentence);
            ArrayList<Tree> nps = npExtractor(parse);
            for (Tree np : nps) {
                ArrayList<Word> nounPhrase = np.yieldWords();
                StringBuilder npstr = new StringBuilder();
                for (int j = 0; j < nounPhrase.size(); j++) {
                    if (j + 1 == nounPhrase.size()) {
                        npstr.append(nounPhrase.get(j));
                    } else {
                        npstr.append(nounPhrase.get(j)).append(" ");
                    }
                }
                nounPhrases.add(new NounPhrase(String.valueOf(sentenceNumber), npstr.toString()));
            }
            //ArrayList<TaggedWord> words = parse.taggedYield();
            //for(TaggedWord t: words) {
            //    tokens.add(tokenize(tokenNumber, sentenceNumber, t));
            //    tokenNumber++;
            //}
            sentenceNumber++;
        }

        //for(NounPhrase np: nounPhrases){
        //    System.out.println("Sentence " + np.getSentenceNumber() + ": " + np.getNounPhrase());
        //}
        return nounPhrases;
    }

    private static ArrayList<Tree> npExtractor(Tree parsedTree){
        ArrayList<Tree> nps = new ArrayList();
        Tree[] sent = parsedTree.children();
        for (Tree aSent : sent) {
            if (aSent.value().equalsIgnoreCase("NP")) {
                nps.add(aSent);
                nps.addAll(npExtractor(aSent));
            } else {
                nps.addAll(npExtractor(aSent));
            }
        }
        return nps;
    }

    private static Token tokenize(int num, int sentNum, TaggedWord t){
        String word = t.toString();
        int wordBoundary = word.indexOf('/');
        return new Token(num, sentNum,word.substring(0,wordBoundary), word.substring(wordBoundary));
    }

    private SFNLPFrenchParser() {} // static methods only

}
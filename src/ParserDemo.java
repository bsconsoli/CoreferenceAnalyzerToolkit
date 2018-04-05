import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.StringReader;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

class ParserDemo {

    /**
     * The main method demonstrates the easiest way to load a parser.
     * Simply call loadModel and specify the path of a serialized grammar
     * model, which can be a file, a resource on the classpath, or even a URL.
     * For example, this demonstrates loading a grammar from the models jar
     * file, which you therefore need to include on the classpath for ParserDemo
     * to work.
     *
     * Usage: {@code java ParserDemo [[model] textFile]}
     * e.g.: java ParserDemo edu/stanford/nlp/models/lexparser/chineseFactored.ser.gz data/chinese-onesent-utf8.txt
     *
     */
    public static void main(String[] args) {
        LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/frenchFactored.ser.gz");

        if (args.length == 0) {
            demoAPI(lp);
        } else {
            String textFile = (args.length > 1) ? args[1] : args[0];
            demoDP(lp, textFile);
        }
    }

    public static void demoDP(LexicalizedParser lp, String filename) {
        TreebankLanguagePack tlp = lp.treebankLanguagePack();
        GrammaticalStructureFactory gsf = null;
        
        if (tlp.supportsGrammaticalStructures()) {
            gsf = tlp.grammaticalStructureFactory();
        }

        for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
            Tree parse = lp.apply(sentence);
            String parsedString = parse.toString();
            System.out.println(parsedString);

            if (gsf != null) {
                GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
                Collection tdl = gs.typedDependenciesCCprocessed();
                System.out.println(tdl);
                System.out.println();
            }
        }
    }

    public static void demoAPI(LexicalizedParser lp) {
        String sent2 = "Le gouvernement se résout donc à renvoyer la balle dans le camp de partenaires qui ont amplement fait la preuve de leur incapacité à gérer le système de santé.";
        TokenizerFactory<CoreLabel> tokenizerFactory =
                PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        Tokenizer<CoreLabel> tok =
                tokenizerFactory.getTokenizer(new StringReader(sent2));
        List<CoreLabel> rawWords = tok.tokenize();
        Tree parse = lp.apply(rawWords);
        String parsedString = parse.toString();
        System.out.println(parsedString);
    }

    private ParserDemo() {} // static methods only

}

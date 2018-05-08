import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class writeTxts {

    public static void main(String[] args) {

        SFNLPFrenchParser.frenchParser(args[3]);
        //Parser.parserCoreNLP(args[0]);
        //Parser.parseCorpXML(args[1]);

        //ArrayList<NounPhrase> npCoreEn = parseNPDoc(args[2], true); //Stanford Inglês

        //txtWriter.writeNounPhraseforTL("CorNPforTL.txt", npCoreEn);
        //txtWriter.writeNounPhraseInfoforTL("CorNPInfoforTL.txt", npCoreEn);
        //txtWriter.joinTLNPInfo("CorNPforTL.txt", "CorNPInfoforTL.txt");

    }
    private static ArrayList<NounPhrase> parseNPDoc(String filename, boolean annotatedForCoreference){
        ArrayList<NounPhrase> npList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            System.out.println("Parsing NPs...");
            while ((line = br.readLine()) != null) {
                if (line.equalsIgnoreCase("")) continue;
                String[] splitNP = line.split(";");
                if (annotatedForCoreference) npList.add(new NounPhrase(splitNP[0],splitNP[1],splitNP[2],splitNP[3],splitNP[4]));
                else npList.add(new NounPhrase(splitNP[1],splitNP[2]));
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
        return npList;
    }
}

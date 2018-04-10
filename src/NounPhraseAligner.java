import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NounPhraseAligner {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Arg1 = french NPs; Arg2 = CORP NPs");
            System.exit(-1);
        }
        //CorpParser.parseCorpXML(args[2]);
        //SFNLPFrenchParser.frenchParser(args[3]);

        ArrayList<NounPhrase> npCorp = new ArrayList<>();
        ArrayList<NounPhrase> npFrench = new ArrayList<>();
        ArrayList<NounPhrase> npCorpChainFrench = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String line;

            System.out.println("Parsing CORP NPs...");
            while ((line = br.readLine()) != null) {
                if (line.equalsIgnoreCase("")) continue;
                String[] splitNP = line.split(";");
                npCorp.add(new NounPhrase(splitNP[0],splitNP[1],splitNP[2]));
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(args[1]))) {
            String line;
            System.out.println("Parsing Raw French Text NPs...");
            while ((line = br.readLine()) != null) {
                if (line.equalsIgnoreCase("")) continue;
                String[] splitNP = line.split(";");
                npFrench.add(new NounPhrase(splitNP[0],splitNP[1],splitNP[2]));
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }

        for(NounPhrase npF:npFrench){
            for(NounPhrase npC:npCorp){
                if(npF.getNounPhrase().equalsIgnoreCase(npC.getNounPhrase())){
                    npCorpChainFrench.add(new NounPhrase(npC.getChainNumber(),npF.getSentenceNumber(),npF.getNounPhrase()));
                    break;
                }
            }
        }
        Collections.sort(npCorpChainFrench, Comparator.comparing(NounPhrase::getChainNumber));
        txtWriter.writeNounPhraseList("outputAligner.txt",npCorpChainFrench);
    }
}

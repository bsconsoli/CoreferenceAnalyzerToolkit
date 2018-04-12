import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

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
        if (npCorpChainFrench.size() == 0){
            System.out.println("No coreference chain matches");
            System.exit(-1);
        }

        ArrayList<String> npAnalysis = new ArrayList<>();
        HashSet<String> alreadyAdded = new HashSet<>();
        ArrayList<String> sentenceNums = new ArrayList<>();

        for(int i = 0; i < npCorpChainFrench.size(); i++){
            StringBuilder line = new StringBuilder();
            if (i == 0 || i > 0 && !(npCorpChainFrench.get(i-1).getChainNumber().equalsIgnoreCase(npCorpChainFrench.get(i).getChainNumber()))){
                npCorpChainFrench.get(i);
                if (i > 0 && !(npCorpChainFrench.get(i-1).getChainNumber().equalsIgnoreCase(npCorpChainFrench.get(i).getChainNumber()) || i+1 == npCorpChainFrench.size())) {
                    line.append("Appears in following sentences: ");
                    for (String senNum : sentenceNums) {
                        line.append(senNum + " ");
                    }
                    sentenceNums = new ArrayList<>();
                    npAnalysis.add(line.toString());
                }
                if (i != 0){
                    line = new StringBuilder();
                    npAnalysis.add(line.toString());
                }
                line.append("Cadeia " + npCorpChainFrench.get(i).getChainNumber());
                npAnalysis.add(line.toString());
                line = new StringBuilder();
                line.append("Head: ");
            }
            if (!alreadyAdded.contains(npCorpChainFrench.get(i).getNounPhrase())) {
                line.append(npCorpChainFrench.get(i).getNounPhrase());
                alreadyAdded.add(npCorpChainFrench.get(i).getNounPhrase());
                npAnalysis.add(line.toString());
            }
            if (i+1 == npCorpChainFrench.size()) {
                line = new StringBuilder();
                line.append("Appears in following sentences: ");
                for (String senNum : sentenceNums) {
                    line.append(senNum + " ");
                }
                npAnalysis.add(line.toString());
            }
            sentenceNums.add(npCorpChainFrench.get(i).getSentenceNumber());
        }
        txtWriter.summary("sumario.txt", npAnalysis);
    }
}

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
                npCorp.add(new NounPhrase(splitNP[0],splitNP[1],splitNP[2],splitNP[3]));
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
                npFrench.add(new NounPhrase(splitNP[1],splitNP[2]));
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }

        for(NounPhrase npF:npFrench){
            for(NounPhrase npC:npCorp){
                if(npF.getNounPhrase().equalsIgnoreCase(npC.getNounPhrase())){
                    npCorpChainFrench.add(new NounPhrase(npC.getChainNumber(),npF.getSentenceNumber(),npF.getNounPhrase(),npC.getCategoria()));
                    break;
                }
            }
        }

        Collections.sort(npCorpChainFrench, Comparator.comparing(NounPhrase::getChainNumber));
        txtWriter.writeNounPhraseList("outputAligner.txt",npCorpChainFrench);
        if (npCorpChainFrench.size() == 0){
            System.out.println("No co-reference chain matches");
            System.exit(-1);
        }

        //int corpChainNum = 0;
        //for (int i = 0; i < npCorp.size(); i++){
        //    if (i == 0 || i > 0 && !(npCorp.get(i-1).getChainNumber().equalsIgnoreCase(npCorp.get(i).getChainNumber()))){
        //        corpChainNum++;
        //    }
        //}
        //System.out.println("Número de Cadeias CORP: " + corpChainNum);

        ArrayList<NPChain> npChains = new ArrayList<>();
        HashSet<String> alreadyAdded = new HashSet<>();
        ArrayList<String> sentenceNums = new ArrayList<>();
        ArrayList<NounPhrase> uniqueMentions = new ArrayList<>();
        ArrayList<Integer> uniqueMentionFrequency = new ArrayList<>();

        for(int i = 0; i < npCorpChainFrench.size(); i++){

            if (i == 0 || i > 0 && !(npCorpChainFrench.get(i-1).getChainNumber().equalsIgnoreCase(npCorpChainFrench.get(i).getChainNumber()))){
                npCorpChainFrench.get(i);
                if (i > 0 && !(npCorpChainFrench.get(i-1).getChainNumber().equalsIgnoreCase(npCorpChainFrench.get(i).getChainNumber()) || i+1 == npCorpChainFrench.size())) {
                    String chainNumber = npCorpChainFrench.get(i-1).getChainNumber();
                    npChains.add(new NPChain(chainNumber, uniqueMentions, sentenceNums, uniqueMentionFrequency));
                    sentenceNums = new ArrayList<>();
                    uniqueMentions = new ArrayList<>();
                    uniqueMentionFrequency = new ArrayList<>();
                }
            }
            if (!alreadyAdded.contains(npCorpChainFrench.get(i).getNounPhrase())) {
                uniqueMentions.add(npCorpChainFrench.get(i));
                uniqueMentionFrequency.add(1);
                alreadyAdded.add(npCorpChainFrench.get(i).getNounPhrase());
            } else {
                int indx = 0;
                for (int j = 0; j < uniqueMentions.size();j++){
                    if (uniqueMentions.get(j).getNounPhrase().equalsIgnoreCase(npCorpChainFrench.get(i).getNounPhrase())) indx = j;
                }
                uniqueMentionFrequency.set(indx, uniqueMentionFrequency.get(indx)+1);
            }
            if (i+1 == npCorpChainFrench.size()) {
                String chainNumber = npCorpChainFrench.get(i).getChainNumber();
                npChains.add(new NPChain(chainNumber, uniqueMentions, sentenceNums, uniqueMentionFrequency));
            }
            sentenceNums.add(npCorpChainFrench.get(i).getSentenceNumber());
        }

        //System.out.println("Número de Cadeias Projetadas Para o Texto em Francês: " + npChains.size());

        Collections.sort(npChains, Comparator.comparing(NPChain::getSize).reversed());

        ArrayList<String> summary = new ArrayList<>();
        for(NPChain npc: npChains){
            summary.add("Cadeia: " + npc.getChainNumber() + " | Tamanho: " + npc.getSize() + " | Categoria da Menção com Maior Frequência: " + npc.getMostMentionedCategory());
            for (int i = 0; i < npc.getUniqueMentions().size(); i++){
                summary.add("Menção: " + npc.getUniqueMentions().get(i).getNounPhrase() + " | Frenquência: " + npc.getUniqueMentionFreq().get(i) + " | Categoria: " + npc.getUniqueMentions().get(i).getCategoria());
            }
            summary.add("");
        }
        txtWriter.summary("sumario.txt", summary);
    }
}

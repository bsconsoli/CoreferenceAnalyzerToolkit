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
        //Parser.parseCorpXML(args[2]);
        //SFNLPFrenchParser.frenchParser(args[3]);
        //txtWriter.latin1Converter(args[5]);
        Parser.parserCoreNLP(args[6]);

        ArrayList<NounPhrase> npCorp = parseNPDoc(args[0], true);
        ArrayList<NounPhrase> npCorppt = parseNPDoc(args[4], true);
        ArrayList<NounPhrase> npFrench = parseNPDoc(args[1], false);
        //ArrayList<NounPhrase> npCore = parseNPDoc(args[7], false);
        ArrayList<NounPhrase> npCorpChainFrench = new ArrayList<>();


        for(NounPhrase npF:npFrench){
            for(NounPhrase npC:npCorp){
                if(npF.getNounPhrase().equalsIgnoreCase(npC.getNounPhrase())){
                    npCorpChainFrench.add(new NounPhrase(npC.getChainNumber(),npF.getSentenceNumber(),npF.getNounPhrase(),npC.getCategoria()));
                    break;
                }
            }
        }

        Collections.sort(npCorpChainFrench, Comparator.comparing(NounPhrase::getChainNumber));
        //Collections.sort(npCorp, Comparator.comparing(NounPhrase::getChainNumber));

        txtWriter.writeNounPhraseList("outputAligner.txt",npCorpChainFrench);

        if (npCorpChainFrench.size() == 0){
            System.out.println("No co-reference chain matches");
            System.exit(-1);
        }

        ArrayList<NPChain> npChainsfr = buildNPChainList(npCorpChainFrench);
        ArrayList<NPChain> npChainspt = buildNPChainList(npCorp);
        ArrayList<NPChain> npChainspt2 = buildNPChainList(npCorppt);

        Collections.sort(npChainsfr, Comparator.comparing(NPChain::getSize).reversed());
        Collections.sort(npChainspt, Comparator.comparing(NPChain::getSize).reversed());
        Collections.sort(npChainspt2, Comparator.comparing(NPChain::getSize).reversed());

        npChainsfr = sortByCategoria(npChainsfr);
        npChainspt = sortByCategoria(npChainspt);
        npChainspt2 = sortByCategoria(npChainspt2);

        ArrayList<String> summaryfr = prepareSummary(npChainsfr);
        ArrayList<String> summarypt = prepareSummary(npChainspt);
        ArrayList<String> summarypt2 = prepareSummary(npChainspt2);

        txtWriter.summary("sumario-fr.txt", summaryfr);
        txtWriter.summary("sumario-pt-fr.txt", summarypt);
        txtWriter.summary("sumario-pt.txt", summarypt2);
    }

    private static ArrayList<NPChain> sortByCategoria(ArrayList<NPChain> npChainsfr) {
        HashSet<NPChain> unique = new HashSet<>();
        ArrayList<NPChain> npChainsSorted = new ArrayList<>();
        for (int i = 0; i < npChainsfr.size(); i++){
            for (int j = i; j < npChainsfr.size(); j++) {
                if (npChainsfr.get(j).getMostMentionedCategory().equalsIgnoreCase("OUTRO")){
                    unique.add(npChainsfr.get(j));
                }
                if ((j == 0 || j > 0 && npChainsfr.get(j).getMostMentionedCategory().equalsIgnoreCase(npChainsfr.get(i).getMostMentionedCategory()))) {
                    if (!unique.contains(npChainsfr.get(j))) {
                        npChainsSorted.add(npChainsfr.get(j));
                        unique.add(npChainsfr.get(j));
                    }
                }
            }
        }
        for (int i = 0; i < npChainsfr.size(); i++){
            if (npChainsfr.get(i).getMostMentionedCategory().equalsIgnoreCase("OUTRO")){
                npChainsSorted.add(npChainsfr.get(i));
            }
        }
        return npChainsSorted;
    }

    private static ArrayList<NPChain> buildNPChainList(ArrayList<NounPhrase> nps){
        ArrayList<NPChain> npChains = new ArrayList<>();
        HashSet<String> alreadyAdded = new HashSet<>();
        ArrayList<String> sentenceNums = new ArrayList<>();
        ArrayList<NounPhrase> uniqueMentions = new ArrayList<>();
        ArrayList<Integer> uniqueMentionFrequency = new ArrayList<>();

        for(int i = 0; i < nps.size(); i++){

            if (i == 0 || i > 0 && !(nps.get(i-1).getChainNumber().equalsIgnoreCase(nps.get(i).getChainNumber()))){
                nps.get(i);
                if (i > 0 && !(nps.get(i-1).getChainNumber().equalsIgnoreCase(nps.get(i).getChainNumber()) || i+1 == nps.size())) {
                    String chainNumber = nps.get(i-1).getChainNumber();
                    npChains.add(new NPChain(chainNumber, uniqueMentions, sentenceNums, uniqueMentionFrequency));
                    sentenceNums = new ArrayList<>();
                    uniqueMentions = new ArrayList<>();
                    uniqueMentionFrequency = new ArrayList<>();
                }
            }
            if (!alreadyAdded.contains(nps.get(i).getNounPhrase())) {
                uniqueMentions.add(nps.get(i));
                uniqueMentionFrequency.add(1);
                alreadyAdded.add(nps.get(i).getNounPhrase());
            } else {
                int indx = 0;
                for (int j = 0; j < uniqueMentions.size();j++){
                    if (uniqueMentions.get(j).getNounPhrase().equalsIgnoreCase(nps.get(i).getNounPhrase())) indx = j;
                }
                uniqueMentionFrequency.set(indx, uniqueMentionFrequency.get(indx)+1);
            }
            sentenceNums.add(nps.get(i).getSentenceNumber());
            if (i+1 == nps.size()) {
                String chainNumber = nps.get(i).getChainNumber();
                npChains.add(new NPChain(chainNumber, uniqueMentions, sentenceNums, uniqueMentionFrequency));
            }
        }

        return npChains;
    }

    private static ArrayList<String> prepareSummaryVerbose(ArrayList<NPChain> npCs){
        ArrayList<String> summary = new ArrayList<>();
        for(NPChain npc: npCs){
            summary.add("Cadeia: " + npc.getChainNumber() + " | Tamanho: " + npc.getSize() + " | Categoria da Menção com Maior Frequência: " + npc.getMostMentionedCategory());
            for (int i = 0; i < npc.getUniqueMentions().size(); i++){
                summary.add("Menção: " + npc.getUniqueMentions().get(i).getNounPhrase() + " | Frenquência: " + npc.getUniqueMentionFreq().get(i) + " | Categoria: " + npc.getUniqueMentions().get(i).getCategoria());
            }
            summary.add("");
        }
        return summary;
    }

    private static ArrayList<String> prepareSummary(ArrayList<NPChain> npCs){
        ArrayList<String> summary = new ArrayList<>();
        StringBuilder sent = new StringBuilder();
        int numChains = 0;
        int padding = 0;
        for (int i = 0; i < npCs.size(); i++) {
            NPChain npc = npCs.get(i);
            if (i == 0 || i > 0 && !(npCs.get(i-1).getMostMentionedCategory().equalsIgnoreCase(npc.getMostMentionedCategory()))) {
                if (i != 0){
                    summary.set((i-numChains+padding), summary.get(i-numChains+padding) + "  (" + numChains + ")");
                    summary.add("");
                    numChains = 0;
                    padding += 2;
                }
                summary.add("Cadeias de " + npc.getMostMentionedCategory());
            }
            for (int j = 0; j < npc.getUniqueMentions().size(); j++) {
                sent.append(npc.getUniqueMentions().get(j).getNounPhrase() + " - ");
            }
            sent.deleteCharAt(sent.lastIndexOf("-"));
            sent.append("(" + npc.getSize() + ")");
            summary.add(sent.toString());
            numChains++;
            sent = new StringBuilder();
            if (i+1 == npCs.size()){
                summary.set((i-numChains+padding+1), summary.get(i-numChains+padding+1) + "  (" + numChains + ")");
                summary.add("");
            }
        }
        return summary;
    }

    private static ArrayList<NounPhrase> parseNPDoc(String filename, boolean annotatedForCoreference){
        ArrayList<NounPhrase> npList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            System.out.println("Parsing NPs...");
            while ((line = br.readLine()) != null) {
                if (line.equalsIgnoreCase("")) continue;
                String[] splitNP = line.split(";");
                if (annotatedForCoreference) npList.add(new NounPhrase(splitNP[0],splitNP[1],splitNP[2],splitNP[3]));
                else npList.add(new NounPhrase(splitNP[1],splitNP[2]));
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
        return npList;
    }
}

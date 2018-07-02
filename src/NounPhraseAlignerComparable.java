import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class NounPhraseAlignerComparable {
    public static void main(String[] args) {

        ArrayList<NounPhrase> npCorpfrPtTL = parseNPDoc(args[1], true); //CORP em Francês (1) ou inglês (4)
        ArrayList<NounPhrase> npCorppt = parseNPDoc(args[0], true); //CORP em Português
        ArrayList<NounPhrase> npCoreFr = parseNPDoc(args[2], false); //Stanford Francês
        ArrayList<NounPhrase> npCorefrEnTL = parseNPDoc(args[3], true); //Stanford Inglês - TL para Francês

        ArrayList<NounPhrase> npCorpChainProjectPT = projectChain(npCorpfrPtTL, npCoreFr);
        ArrayList<NounPhrase> npCorpChainProjectEN = projectChain(npCorefrEnTL, npCoreFr);

        Collections.sort(npCorpChainProjectPT, Comparator.comparing(NounPhrase::getChainNumber));
        Collections.sort(npCorpChainProjectEN, Comparator.comparing(NounPhrase::getChainNumber));
        Collections.sort(npCorpfrPtTL, Comparator.comparing(NounPhrase::getChainNumber));

        txtWriter.writeNounPhraseList("outputAligner.txt",npCorpChainProjectPT);

        if (npCorpChainProjectPT.size() == 0){
            System.out.println("No coreference chain matches");
            System.exit(-1);
        }

        ArrayList<NPChain> npChainsfr = buildNPChainList(npCorpChainProjectPT);
        ArrayList<NPChain> npChainspt = buildNPChainList(npCorpfrPtTL);
        ArrayList<NPChain> npChainspt2 = buildNPChainList(npCorppt);
        ArrayList<NPChain> npChainsen = buildNPChainList(npCorpChainProjectEN);

        System.out.println("Numero Cadeias CORP: " + npChainspt.size());
        System.out.println("Numero Cadeias Projetadas-PT: " + npChainsfr.size());
        System.out.println("Numero Cadeias Projetadas-EN: " + npChainsen.size());

        Collections.sort(npChainsfr, Comparator.comparing(NPChain::getSize).reversed());
        Collections.sort(npChainspt, Comparator.comparing(NPChain::getSize).reversed());
        Collections.sort(npChainspt2, Comparator.comparing(NPChain::getSize).reversed());
        Collections.sort(npChainsen, Comparator.comparing(NPChain::getSize).reversed());

        npChainsfr = sortByCategoria(npChainsfr);
        npChainspt = sortByCategoria(npChainspt);
        npChainspt2 = sortByCategoria(npChainspt2);
        npChainsen = sortByCategoria(npChainsen);

        ArrayList<String> summaryfr = prepareSummary(npChainsfr);
        ArrayList<String> summarypt = prepareSummary(npChainspt);
        ArrayList<String> summarypt2 = prepareSummary(npChainspt2);
        ArrayList<String> summaryen = prepareSummary(npChainsen);

        txtWriter.summary("sumario-fr-pt.txt", summaryfr);
        txtWriter.summary("sumario-pt-fr.txt", summarypt);
        txtWriter.summary("sumario-pt.txt", summarypt2);
        txtWriter.summary("sumario-fr-en.txt", summaryen);
    }

    private static ArrayList<NPChain> sortByCategoria(ArrayList<NPChain> npChainsfr){
        HashSet<NPChain> unique = new HashSet<>();
        ArrayList<NPChain> npChainsSorted = new ArrayList<>();
        for (int i = 0; i < npChainsfr.size(); i++){
            for (int j = i; j < npChainsfr.size(); j++) {
                if (npChainsfr.get(j).getMostMentionedCategory().equalsIgnoreCase("OUTRO") || npChainsfr.get(j).getMostMentionedCategory().equalsIgnoreCase("null")){
                    unique.add(npChainsfr.get(j));
                }
                if ((j == 0 || j > 0 && npChainsfr.get(j).getMostMentionedCategory().equalsIgnoreCase(npChainsfr.get(i).getMostMentionedCategory()))){
                    if (!unique.contains(npChainsfr.get(j))) {
                        npChainsSorted.add(npChainsfr.get(j));
                        unique.add(npChainsfr.get(j));
                    }
                }
            }
        }
        for (int i = 0; i < npChainsfr.size(); i++){
            if (npChainsfr.get(i).getMostMentionedCategory().equalsIgnoreCase("OUTRO") || npChainsfr.get(i).getMostMentionedCategory().equalsIgnoreCase("null")){
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
                    alreadyAdded = new HashSet<>();
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
                summary.add("Menção: " + npc.getUniqueMentions().get(i).getNounPhrase() + " | Frenquência: " + npc.getUniqueMentionFreq().get(i) + " | Categoria: " + npc.getUniqueMentions().get(i).getCategoria() + " | Núcleo: " + npc.getUniqueMentions().get(i).getNucleus());
            }
            summary.add("");
        }
        return summary;
    }

    private static ArrayList<String> prepareSummaryVerboseCORE(ArrayList<NPChain> npCs){
        ArrayList<String> summary = new ArrayList<>();
        for(NPChain npc: npCs){
            summary.add("Cadeia: " + npc.getChainNumber() + " | Tamanho: " + npc.getSize());
            for (int i = 0; i < npc.getUniqueMentions().size(); i++){
                summary.add("Menção: " + npc.getUniqueMentions().get(i).getNounPhrase() + " | Frenquência: " + npc.getUniqueMentionFreq().get(i));
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
                sent.append("["+npc.getUniqueMentions().get(j).getNounPhrase() + "] (" + npc.getUniqueMentionFreq().get(j) + ")" + " - ");
            }
            sent.deleteCharAt(sent.lastIndexOf("-"));
            sent.append(" " + npc.getSize() + "");
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
                if (annotatedForCoreference) npList.add(new NounPhrase(splitNP[0],splitNP[1],splitNP[2],splitNP[3],splitNP[4]));
                else npList.add(new NounPhrase(splitNP[1],splitNP[2],splitNP[3]));
            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
        return npList;
    }

    private static ArrayList<NounPhrase> projectChain(ArrayList<NounPhrase> projector, ArrayList<NounPhrase> projectee){
        ArrayList<NounPhrase> npCorpChainProject = new ArrayList<>();
        NounPhrase chosen = null;
        for(NounPhrase npF:projectee){
            for(NounPhrase npC:projector){
                if(npF.getNounPhrase().equalsIgnoreCase(npC.getNounPhrase())){
                    chosen = new NounPhrase(npC.getChainNumber(),npF.getSentenceNumber(),npF.getNounPhrase(),npC.getCategoria(), npF.getNucleus());
                    break;
                }
                if(npF.getNucleus().equalsIgnoreCase(npC.getNucleus())){
                    chosen = new NounPhrase(npC.getChainNumber(),npF.getSentenceNumber(),npF.getNounPhrase(),npC.getCategoria(), npF.getNucleus());
                }
            }
            if (chosen != null) {
                npCorpChainProject.add(chosen);
                chosen = null;
            }
        }
        return npCorpChainProject;
    }
}
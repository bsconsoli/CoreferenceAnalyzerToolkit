import java.util.ArrayList;

public class NounPhraseAlignerParallel {
    public static void main(String[] args) {

    }

    private static ArrayList<NounPhrase> projectChain(ArrayList<NounPhrase> projector, ArrayList<NounPhrase> projectee) {
        ArrayList<NounPhrase> npCorpChainProject = new ArrayList<>();
        for(NounPhrase npF:projectee){
            for(NounPhrase npC:projector) {
                if(npF.getSentenceNumber().equalsIgnoreCase(npC.getSentenceNumber())){

                }
            }
        }


        return npCorpChainProject;
    }
}

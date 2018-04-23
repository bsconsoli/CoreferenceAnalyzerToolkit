import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Parser {
    public static void parseCorpXML(String corpXML) {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(corpXML), "ISO-8859-1"))) {
            String line;
            String numCadeia = "0";
            ArrayList<NounPhrase> CorpNPs = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                if(line.contains("Mencoes_Unicas")){
                    break;
                }

                if (line.contains("Cadeia_")){
                    int indexCad = line.indexOf("Cadeia");
                    int indexCadNumInit = line.indexOf('_', indexCad);
                    int indexCadNumFin = line.indexOf('>', indexCad);
                    numCadeia = line.substring(indexCadNumInit+1, indexCadNumFin);
                }

                if (line.contains("sn id=")){
                    int indexSent = line.indexOf("sentenca=");
                    int indexSentNumInit = line.indexOf('"', indexSent);
                    int indexSentNumFin = line.indexOf('"', indexSentNumInit+1);
                    String sent = line.substring(indexSentNumInit+1, indexSentNumFin);
                    int indexSint = line.indexOf("sintagma=");
                    int indexSintNumInit = line.indexOf('"', indexSint);
                    int indexSintNumFin = line.indexOf('"', indexSintNumInit+1);
                    String sint = line.substring(indexSintNumInit+1, indexSintNumFin);
                    int indexCat = line.indexOf("Categoria=");
                    int indexCatNumInit = line.indexOf('"', indexCat);
                    int indexCatSecondCat = line.indexOf('/', indexCat);
                    int indexCatNumFin = line.indexOf('"', indexCatNumInit+1);
                    String cat;
                    if (indexCatSecondCat != -1){
                        cat = line.substring(indexCatNumInit+1, indexCatSecondCat);
                    }
                    else cat = line.substring(indexCatNumInit+1, indexCatNumFin);
                    CorpNPs.add(new NounPhrase(numCadeia,sent,sint,cat));
                }
            }
            txtWriter.writeNounPhraseList("outputCORP.txt", CorpNPs);
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void parserCoreNLP(String coreOut) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(coreOut), "UTF-8"));
            String line;
            int numChain = 0;
            boolean newChain = false;
            ArrayList<NounPhrase> CoreNPs = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (line.contains("Coreference set:")) {
                    newChain = true;
                    numChain++;
                }
                if (newChain) {
                    if (line.contains(", that is: ")) {
                        int indexSint = line.lastIndexOf("->");
                        int indexSintNumInit = line.indexOf('"', indexSint);
                        int indexSintNumFin = line.indexOf('"', indexSintNumInit + 1);
                        String sint = line.substring(indexSintNumInit + 1, indexSintNumFin);
                        int indexSent = line.indexOf("->");
                        int indexSentNumInit = line.indexOf('(', indexSent);
                        int indexSentNumFin = line.indexOf(',', indexSentNumInit + 1);
                        String sent = line.substring(indexSentNumInit + 1, indexSentNumFin);
                        CoreNPs.add(new NounPhrase(String.valueOf(numChain), sent, sint));
                        newChain = false;
                    }
                }
                if (line.contains(", that is: ")) {
                    int indexSint = line.indexOf("that is:");
                    int indexSintNumInit = line.indexOf('"', indexSint);
                    int indexSintNumFin = line.indexOf('"', indexSintNumInit + 1);
                    String sint = line.substring(indexSintNumInit + 1, indexSintNumFin);
                    int indexSentNumInit = line.indexOf('(');
                    int indexSentNumFin = line.indexOf(',', indexSentNumInit + 1);
                    String sent = line.substring(indexSentNumInit + 1, indexSentNumFin);
                    CoreNPs.add(new NounPhrase(String.valueOf(numChain), sent, sint));
                }
            }

            br = new BufferedReader(new InputStreamReader(new FileInputStream(coreOut), "UTF-8"));
            while ((line = br.readLine()) != null) {
                if (line.contains("Extracted the following NER entity mentions:")) {
                    while(!line.isEmpty()){
                        line = br.readLine();
                        //System.out.println(line);
                        String[] splits = line.split("\\t");
                        if (splits.length > 0) {
                            for (NounPhrase np : CoreNPs) {
                                System.out.println(line);
                                if (splits[0].equalsIgnoreCase(np.getNounPhrase())) {
                                    np.setCategoria(splits[1]);
                                }
                            }
                        }
                    }
                }
            }

            txtWriter.writeNounPhraseList("outputCore.txt", CoreNPs);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static String translate(String text) throws IOException{
        // fetch
        URL url = new URL("translate.google.com/#pt/fr/" + URLEncoder.encode(text, "UTF-8"));
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Something Else");
        BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String result = br.readLine();
        br.close();
        // parse
        // System.out.println(result);
        result = result.substring(2, result.indexOf("]]") + 1);
        StringBuilder sb = new StringBuilder();
        String[] splits = result.split("(?<!\\\\)\"");
        for(int i = 1; i < splits.length; i += 8)
            sb.append(splits[i]);
        return sb.toString().replace("\\n", "\n").replaceAll("\\\\(.)", "$1");
    }
}

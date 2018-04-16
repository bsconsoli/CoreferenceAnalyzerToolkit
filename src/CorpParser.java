import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class CorpParser {
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
                    int indexCatNumFin = line.indexOf('"', indexCatNumInit+1);
                    String cat = line.substring(indexCatNumInit+1, indexCatNumFin);
                    CorpNPs.add(new NounPhrase(numCadeia,sent,sint,cat));
                }
            }
            txtWriter.writeNounPhraseList("outputCORP.txt", CorpNPs);
        } catch (IOException e){
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

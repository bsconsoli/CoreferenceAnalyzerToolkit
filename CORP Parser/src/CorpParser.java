import java.io.*;
import java.util.ArrayList;

public class CorpParser {
    public static void main(String[] args) {
        if (args.length == 0){
            System.out.println("Arg1 = CORP XML file");
            System.exit(-1);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "ISO-8859-1"))) {
            String line;
            PrintWriter output = new PrintWriter("output.txt", "UTF-8");
            //BufferedWriter output = new BufferedWriter(new FileWriter("output.txt"));
            while ((line = br.readLine()) != null) {
                if (line.contains("sn id=")){
                    int indexSent = line.indexOf("sentenca=");
                    int indexSentNumInit = line.indexOf('"', indexSent);
                    int indexSentNumFin = line.indexOf('"', indexSentNumInit+1);
                    String sent = line.substring(indexSentNumInit+1, indexSentNumFin);
                    int indexSint = line.indexOf("sintagma=");
                    int indexSintNumInit = line.indexOf('"', indexSint);
                    int indexSintNumFin = line.indexOf('"', indexSintNumInit+1);
                    String sint = line.substring(indexSintNumInit+1, indexSintNumFin);
                    output.println(sent + ";" + sint);
                    System.out.println("Sentença: " + sent + " | Sintagma: " + sint);
                }
            }
            output.close();
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

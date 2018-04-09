import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CorpParser {
    public static void main(String[] args) {
        if (args.length == 0){
            System.out.println("Arg1 = CORP XML file");
            System.exit(-1);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String line;
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
                    System.out.println("Sentença: " + sent + " | Sintagma: " + sint);
                }

            }
        } catch (IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

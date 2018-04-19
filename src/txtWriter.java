import java.io.*;
import java.util.ArrayList;

public class txtWriter {

    public static void writeNounPhraseList(String txtName, ArrayList<NounPhrase> npList){
        PrintWriter output;
        try {
            output = new PrintWriter(txtName, "UTF-8");
            for(NounPhrase np:npList){
                if (np.getChainNumber().equalsIgnoreCase("-1")) output.println(";"+np.getSentenceNumber()+";"+np.getNounPhrase());
                else output.println(np.getChainNumber()+";"+np.getSentenceNumber()+";"+np.getNounPhrase()+";"+np.getCategoria());
            }
            output.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void summary(String txtName, ArrayList<String> strList){
        PrintWriter output;
        try {
            output = new PrintWriter(txtName, "UTF-8");
            for(String np:strList){
                output.println(np);
            }
            output.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void latin1Converter(String txtName) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(txtName), "UTF-8"))) {
            String line;
            PrintWriter output;
            output = new PrintWriter(txtName + ".out", "ISO-8859-1");
            while ((line = br.readLine()) != null) {
                output.println(line);
            }
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

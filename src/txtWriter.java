import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class txtWriter {

    public static void writeNounPhraseList(String txtName, ArrayList<NounPhrase> npList){
        PrintWriter output;
        try {
            output = new PrintWriter(txtName, "UTF-8");
            for(NounPhrase np:npList){
                if (np.getChainNumber().equalsIgnoreCase("-1")) output.println(";"+np.getSentenceNumber()+";"+np.getNounPhrase());
                else output.println(np.getChainNumber()+";"+np.getSentenceNumber()+";"+np.getNounPhrase());
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
}

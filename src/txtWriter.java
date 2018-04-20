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

    public static void writeNounPhraseforTL(String txtName, ArrayList<NounPhrase> npList){
        PrintWriter output;
        try {
            output = new PrintWriter(txtName, "UTF-8");
            for(NounPhrase np:npList){
                output.println(np.getNounPhrase());
            }
            output.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void writeNounPhraseInfoforTL(String txtName, ArrayList<NounPhrase> npList){
        PrintWriter output;
        try {
            output = new PrintWriter(txtName, "UTF-8");
            for(NounPhrase np:npList){
                output.println(np.getChainNumber()+";"+np.getSentenceNumber()+";"+np.getCategoria());
            }
            output.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void joinTLNPInfo(String npList, String npInfoList){
        PrintWriter output;
        try {
            output = new PrintWriter(npList+".out", "UTF-8");
            BufferedReader npL = new BufferedReader(new InputStreamReader(new FileInputStream(npList), "UTF-8"));
            BufferedReader npIL = new BufferedReader(new InputStreamReader(new FileInputStream(npInfoList), "UTF-8"));
            String lineL;
            String lineIL;
            while ((lineL = npL.readLine()) != null) {
                lineIL = npIL.readLine();
                String[] splitIL = lineIL.split(";");
                output.println(splitIL[0] + ";" + splitIL[1] + ";" + lineL + ";" + splitIL[2]);
            }
            output.close();

        } catch (IOException e) {
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

import java.io.*;

public class Score {

    public static int highestScore;
    public static int highScore;
    public static int score;

    private String fileName = "90210EData";
    private String fileAbsolutePath = System.getProperty("java.io.tmpdir") + fileName + ".txt";

    private static File file;

    public Score() throws IOException {
        file = new File(fileAbsolutePath);

        if (!file.exists()){
            createAndInitialize(file);
        }else{
            //creates reader and gets info from file
            BufferedReader in = new BufferedReader(new FileReader(fileAbsolutePath));
            String encryptedText = in.readLine();

            //if nothing in file, create a new. Else, decode and load contents
            if (encryptedText != null){
                String decodedString = XOREncryption.encryptDecrypt(encryptedText);
                if (stringHasValue(decodedString))
                    highestScore = Integer.parseInt(decodedString.replaceAll("[\\D]", ""));
                else
                    createAndInitialize(file);
            }else{
                createAndInitialize(file);
            }
        }

        highScore = 0;
        score = 0;
    }

    private boolean stringHasValue(String s){
        if (s == null)
            return false;
        String onlyInts = s.replaceAll("[\\D]", "");
        return !onlyInts.equals("");
    }

    private void createAndInitialize(File f) throws IOException {
        //if it doesn't exist, create a new file
        f.createNewFile();

        //initialization of default score, 0
        writeScoreToFile(0);

        highestScore = 0;
    }

    public static void writeScoreToFile(int score){

        //create writer to write to file
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String textToFile = "Highest Score: " + score;

        //writes encrypted message to file
        writer.println(XOREncryption.encryptDecrypt(textToFile));

        writer.close();
    }

}

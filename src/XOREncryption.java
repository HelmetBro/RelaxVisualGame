public class XOREncryption {

    private static String KEY = "Taeyeon<3";

    public static String encryptDecrypt(String input) {
        char[] key = stringToUpperCharArray(KEY); //can be any chars, and any length array
        StringBuilder output = new StringBuilder();

        for(int i = 0; i < input.length(); i++) {
            output.append((char) (input.charAt(i) ^ key[i % key.length]));
        }

        return output.toString();
    }

    private static char[] stringToUpperCharArray(String s){
        s = s.toUpperCase();
        char[] arrayKey = new char[s.length()];
        for (byte i = 0; i < s.length(); i++)
            arrayKey[i] += s.charAt(i);
        return arrayKey;
    }

}

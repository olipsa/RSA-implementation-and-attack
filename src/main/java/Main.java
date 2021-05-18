import java.math.BigInteger;

public class Main {

    public static void main(String[] args) {
        Entity A=new Entity(false);
        Entity B=new Entity();
        String s = "RSA";

        //Convert the plaintext to integer between 0 and n
        BigInteger text=Main.convertStringToInteger(s);
        Entity.printBigInteger(text,"text");
        BigInteger cipherText=B.encrypt(text,A.e,A.n);
        Entity.printBigInteger(cipherText,"ciphertext");

        BigInteger decryptedText1=A.decrypt(cipherText);
        System.out.println("Decrypted text is: "+convertIntegerToString(decryptedText1));
        BigInteger decryptedText2=A.decryptWithChineseTheorem(cipherText);
        System.out.println("Decrypted text is: "+convertIntegerToString(decryptedText2));

        Attack Wiener=new Attack(A.n,A.e,cipherText);
        Wiener.start();


    }
    public static BigInteger convertStringToInteger(String text){
        StringBuilder binaryText=new StringBuilder();
        StringBuilder binaryChar;
        int n;
        text=text.toLowerCase();
        for (int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            if (ch >='a'&&ch<='z') {
                n = (int) ch - (int) 'a';
                binaryChar = new StringBuilder();
                binaryChar.append(Integer.toString(n,2));
                //each letter will be represented with 6 bits
                while (binaryChar.length() != 6)
                    binaryChar.insert(0, 0);
                binaryText.append(binaryChar);
            }
        }
        //In order to reverse this algorithm, we have to add 1 at the beginning of the String
        //if it starts with 0
        if(binaryText.charAt(0)=='0')
            binaryText.insert(0,'1');
        return new BigInteger(String.valueOf(binaryText),2);
    }
    public static StringBuilder convertIntegerToString(BigInteger text){
        StringBuilder binaryText=new StringBuilder();
        StringBuilder binaryLetter;
        StringBuilder stringText=new StringBuilder();
        binaryText.append(text.toString(2));
        if(binaryText.length()%6!=0)
            binaryText.deleteCharAt(0);
        for(int i=0;i<binaryText.length();i+=6){
            binaryLetter=new StringBuilder();
            binaryLetter.append(binaryText,i,i+6);
            int letter=Integer.parseInt(binaryLetter.toString(),2);
            char charLetter= (char) (letter+'a');
            stringText.append(charLetter);
        }
        return stringText;
    }

}

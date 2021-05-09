import java.math.BigInteger;
import java.security.SecureRandom;

public class Entity {
    private BigInteger d,p,q;
    public BigInteger n,e;

    public Entity() {
        SecureRandom random=new SecureRandom();
        p=BigInteger.probablePrime(512,random);
        q=BigInteger.probablePrime(512,random);
        n=p.multiply(q);

        //Compute φ = (p − 1)(q − 1)
        BigInteger phi=p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        //Choose e such that 1<e<φ
        /*do{
            e=new BigInteger(phi.bitLength(),random);
        }while(!e.gcd(phi).equals(BigInteger.ONE) || e.compareTo(phi)>=0); // gcd(e, φ) must be 1*/

        //Choose a random e with max 32 bits
        do{
            e=new BigInteger(32,random);
        }while(!e.gcd(phi).equals(BigInteger.ONE)); // gcd(e, φ) must be 1

        // ed ≡ 1 (mod φ).
        d = e.modInverse(phi); //private key
    }
    public BigInteger encrypt(BigInteger plainText,BigInteger exponent,BigInteger modulus){
        return plainText.modPow(exponent,modulus);

    }
    public BigInteger decrypt(BigInteger ciphertext){
        BigInteger decryptedText=BigInteger.ZERO;
        float avgTime=0;
        int sampleSize=300;
        for(int i=0;i<sampleSize;i++){
            long startTime=System.nanoTime();
            decryptedText= ciphertext.modPow(d,n);
            long endTime=System.nanoTime();
            float millis = (endTime - startTime)/1000F ;
            avgTime+=millis;
            //System.out.println("normal decryption has a execution time of "+millis/1000F + "s");
        }
        avgTime/=sampleSize;
        System.out.println("Average time for normal decryption is "+avgTime/1000F+"s with sample size "+sampleSize);
        return decryptedText;

    }
    public BigInteger decryptWithChineseTheorem(BigInteger ciphertext){
        //pre-computation: n1 = n mod (m1 − 1), n2 = n mod (m2 − 1),
        //m^(−1) mod m2 -> the var inversePmodQ
        BigInteger plaintext=BigInteger.ZERO;
        float avgTime=0;
        int sampleSize=300;
        for(int i=0;i<sampleSize;i++){
            long startTime=System.nanoTime();
            BigInteger n1=d.mod(p.subtract(BigInteger.ONE));
            BigInteger n2=d.mod(q.subtract(BigInteger.ONE));
            BigInteger inversePmodQ=p.modInverse(q);
            //x1 = (a mod p)^n1 mod p;
            BigInteger x1=(ciphertext.mod(p).modPow(n1,p));
            //x2 = (a mod q)^n1 mod q;
            BigInteger x2=(ciphertext.mod(q).modPow(n2,q));
            //x := x1 + p((x2 − x1)*inversePmodQ mod q);
            plaintext=x1.add(p.multiply(x2.subtract(x1).multiply(inversePmodQ).mod(q)));
            long endTime=System.nanoTime();
            float millis = (endTime - startTime)/1000F ;
            avgTime+=millis;
            //System.out.println("Decryption with Chinese Remainder Theorem has a execution time of "+millis/1000F + "s");
        }
        avgTime/=sampleSize;
        System.out.println("Average time for decryption with Chinese Remainder Theorem is "+avgTime/1000F+"s with sample size "+sampleSize);
        return plaintext;

    }
    public static void printBigInteger(BigInteger number,String varName){
        System.out.println(varName+"="+number+"\nwith "+number.bitLength()+" bits.\n");
    }
}

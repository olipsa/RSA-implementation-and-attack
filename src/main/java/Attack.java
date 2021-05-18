import com.google.common.math.BigIntegerMath;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Attack {
    private BigInteger n,e,d;
    public Attack(BigInteger n, BigInteger e, BigInteger cipherText){
        this.n=n;
        this.e=e;
    }
    public void start(){
        BigInteger result,remainder;
        List<BigInteger> qArray=new ArrayList<>();

        BigInteger a = e.add(BigInteger.ZERO); //create a copy of e
        BigInteger b = n.add(BigInteger.ZERO); //create a copy of n

        do {
            result = a.divide(b);
            qArray.add(result);
            remainder = a.mod(b);
            a=b;
            b=remainder;
        } while (!remainder.equals(BigInteger.ZERO));

        List<BigInteger>alphaArray=new ArrayList<>();
        List<BigInteger>bethaArray=new ArrayList<>();

        alphaArray.add(qArray.get(0));  //α1 = q1
        alphaArray.add((qArray.get(0).multiply(qArray.get(1))).add(BigInteger.ONE));  //α2 = q1 · q2 + 1

        bethaArray.add(BigInteger.ONE); //β1 = 1
        bethaArray.add(qArray.get(1));   //β2 = q2

        for(int i=2;i<qArray.size();i++) {
            alphaArray.add((qArray.get(i).multiply(alphaArray.get(i - 1))).add(alphaArray.get(i - 2))); //αi = qi · αi−1 + αi−2
            bethaArray.add((qArray.get(i).multiply(bethaArray.get(i - 1))).add(bethaArray.get(i - 2))); // βi = qi · βi−1 + βi−2
        }

        int i=0;
        //System.out.println("Numbers found: "+alphaArray.size());
        BigInteger l;
        boolean keyFound;
        do{
            i++;
            //System.out.println("Trying again.");
            l=alphaArray.get(i);
            d=bethaArray.get(i);
            keyFound=getCriterion(l,d);
        }while(!keyFound && i<alphaArray.size()-1);
        if(!keyFound)
            System.out.println("\nKey not found. d is too large.");





    }
    public boolean getCriterion(BigInteger l, BigInteger d){
        BigInteger result=e.multiply(d).subtract(BigInteger.ONE); //e*d-1
        //System.out.println("l= "+l);
        //System.out.println("d= "+d+" with "+d.bitLength()+" bits");
        //System.out.println("e= "+e);
        if(l.equals(BigInteger.ZERO)){
            //System.out.println("l is 0");
            return false;
        }

        if(!result.mod(l).equals(BigInteger.ZERO)) //if (e*d-1)%l!=0
        {
            //System.out.println("(e*d-1)%l!=0");
            return false;
        }
        BigInteger result2=result.divide(l); //(e*d-1)/l

        //delta=(n-result2+1)^2-4*n
        BigInteger delta=n.subtract(result2).add(BigInteger.ONE).pow(2).subtract(BigInteger.valueOf(4).multiply(n));
        //System.out.println("delta= "+delta);
        try{
            BigInteger deltaSqrt= BigIntegerMath.sqrt(delta, RoundingMode.UNNECESSARY);
        }catch(ArithmeticException |IllegalArgumentException e){
            //System.out.println("Delta is not a square root.");
            return false;
        }
        System.out.println("\nPrivate key found with Wiener Attack: "+d);
        return true;




    }

    public BigInteger getD() {
        return d;
    }
}

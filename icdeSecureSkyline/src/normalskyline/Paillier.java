package normalskyline;

import java.math.BigInteger;
import java.util.Random;

public class Paillier {

    /*
    * p,q是两个大素数，lambda = lcm(p-1,q-1) = (p-1)*(q-1)/gcd(p-1,q-1);lcm最小公倍数,gcd最大公约数
    * */
    private BigInteger p,q,lambda;
    /*
    * n=p*q
    * */
    private BigInteger n;
    /*
    nsquare = n * n;
     */
    private BigInteger nsquare;
    /*
    a random integer in Z*_{n^2} where gcd (L(g^lambda mod n^2), n) = 1
     */
    private BigInteger g;
    /*
    number of bits of modulus
     */
    private int bitLength;

    public Paillier(){
        KeyGeneration(512,64);
    }
    /*
    第一个参数是模的位数，第二个参数是新的大素数将会超过（1-2^(-certainty))的概率，这个函数的构造时间与参数值的大小成正比。
     */
    public Paillier(int bitLengthVal, int certainty){
        KeyGeneration(bitLengthVal,certainty);
    }
    /*
    生成公钥(g,n)和私钥lambda
     */
    public void KeyGeneration(int bitLengthVal, int certainty){
        bitLength = bitLengthVal;
        p = new BigInteger(bitLength/2,certainty,new Random());
        q = new BigInteger(bitLength/2,certainty,new Random());
        n = p.multiply(q);
        nsquare = n.multiply(n);
        //g应该是个随机数，此处选2 可能是为了测试方便选了一个满足条件的结果。
        g = new BigInteger(bitLength/2,certainty,new Random());
        lambda = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))
                .divide(p.subtract(BigInteger.ONE).gcd(q.subtract(BigInteger.ONE)));

        if(g.modPow(lambda,nsquare).subtract(BigInteger.ONE).divide(n).gcd(n).intValue()!=1){
            System.exit(1);
        }


    }

    /*
      加密函数,m是明文，r是随机明文，帮助加密。
      返回一个大整数作为密文
       */
    public BigInteger Enc(BigInteger m,BigInteger r){
        return g.modPow(m,nsquare).multiply(r.modPow(n,nsquare)).mod(nsquare);
    }
    public BigInteger Enc(BigInteger m){
        BigInteger r = new BigInteger(bitLength,new Random());

        return g.modPow(m,nsquare).multiply(r.modPow(n,nsquare)).mod(nsquare);
    }
    /*
    解密函数
     */
    public BigInteger Dec(BigInteger c){
        BigInteger u = g.modPow(lambda,nsquare).subtract(BigInteger.ONE).divide(n).modInverse(n);
        return c.modPow(lambda,nsquare).subtract(BigInteger.ONE).divide(n).multiply(u).mod(n);
    }
    /*
    求两个密文的和
     */
    public BigInteger cipher_add(BigInteger em1 , BigInteger em2){
        return em1.multiply(em2).mod(nsquare);
    }
    public static void main(String[] args){
        Paillier paillier = new Paillier();
        BigInteger m1 = new BigInteger("0");
        BigInteger m2 = new BigInteger("0");

        BigInteger em1 = paillier.Enc(m1);
        BigInteger em2 = paillier.Enc(m2);
        System.out.println("密文1: "+em1);
        System.out.println("密文2: "+em2);
        System.out.println("解密密文1："+paillier.Dec(em1).toString());
        System.out.println("解密密文2："+paillier.Dec(em2).toString());

        BigInteger sum_m1m2 = m1.add(m2).mod(paillier.n);
        System.out.println("原始m1+m1 = "+sum_m1m2.toString());

        BigInteger product_em1em2 = em1.multiply(em2).mod(paillier.nsquare);
        System.out.println("encrypted sum: "+product_em1em2.toString());
        System.out.println("decrypted sum: "+paillier.Dec(product_em1em2).toString());

        BigInteger prod_m1m2 = m1.multiply(m2).mod(paillier.n);
        System.out.println("原始m1*m2 = "+prod_m1m2.toString());
        BigInteger expo_m1m2 = em1.modPow(m2,paillier.nsquare);
        System.out.println("encrypted product: "+expo_m1m2.toString());
        System.out.println("decrypted product: "+paillier.Dec(expo_m1m2).toString());

        //sum 测试
        System.out.println("--------------------------------------");
        Paillier p = new Paillier();
        BigInteger t1 = new BigInteger("0");
        BigInteger t2 = new BigInteger("0");
        BigInteger t3 = new BigInteger("0");
        BigInteger et1 = p.Enc(t1);
        BigInteger et2 = p.Enc(t2);
        BigInteger et3 = p.Enc(t3);
        BigInteger sum = new BigInteger("1");
        sum = p.cipher_add(sum,et1);
        sum = p.cipher_add(sum,et2);
        sum = p.cipher_add(sum,et3);
        System.out.println("sum: "+sum.toString());
        System.out.println("decrypted sum : "+p.Dec(sum).toString());

        System.out.println("--------------------------------------");

    }




































}

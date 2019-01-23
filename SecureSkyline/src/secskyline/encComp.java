package secskyline;

import security.Paillier;
import security.PaillierPK;
import security.PaillierSK;

import java.math.BigInteger;
import java.util.Random;

public class encComp {

    //S1 a,b  S2 sk
//    public static Paillier paillier = new Paillier();
    public static void main(String[] args){
        BigInteger big1 = new BigInteger("1");
        BigInteger big2 = new BigInteger("7688");
        PaillierPK pk = new PaillierPK(1024);
        PaillierSK sk = new PaillierSK(1024);
        Paillier.keyGen(sk,pk);
        BigInteger a = Paillier.encrypt(big1,pk);
        BigInteger b = Paillier.encrypt(big2,pk);
        System.out.println("---------------newSecureLess------------");
        System.out.println(newSecLess(a, b, pk,sk));
        System.out.println("---------------encMul------------");
        BigInteger encMul = SM(a,b, pk, sk);
        System.out.println(encMul);
        System.out.println(Paillier.decrypt(encMul,sk));
        System.out.println("--------------SBD--------------");
        BigInteger aa = Paillier.encrypt(BigInteger.ZERO,pk).multiply(Paillier.encrypt(BigInteger.ONE,pk).modPow(pk.n.subtract(BigInteger.ONE),pk.modulus));
        BigInteger[] sbd = SBD(aa, pk, sk);
        for(int i = 0 ; i < sbd.length ; i++)
            System.out.print(Paillier.decrypt(sbd[i],sk));
//        System.out.println("---------SecureLessEqual-----------"); 需要修改一下
//        System.out.println("b<=a "+Paillier.decrypt(SecLessEqual(a,b,pk,sk),sk));
        System.out.println("---------SecureEqual---------------");
        BigInteger res = SecEqual(a,b,pk,sk);
        System.out.println(Paillier.decrypt(res,sk));
//        System.out.println("---------------SecureLess------------");
//        BigInteger less = SecLess(a,b,pk,sk,dgk);
//        System.out.println(Paillier.decrypt(less,sk));
//        System.out.println("--------------SecMin--------------");
//        BigInteger min = SecMin(pk,sk,a,b);
//        System.out.println("最小值： "+Paillier.decrypt(min,sk));
    }
    //a,b是已经加密的结果
    public static BigInteger SM(BigInteger a, BigInteger b, PaillierPK pk , PaillierSK sk){
        //S1接受a,b
//        long start = System.currentTimeMillis();
        BigInteger randA = new BigInteger(2,new Random());
        BigInteger randB = new BigInteger(2,new Random());
        BigInteger a_lambda = a.multiply(Paillier.encrypt(randA, pk));
        BigInteger b_lambda = b.multiply(Paillier.encrypt(randB,pk));
        //S2:接受a_lambda,b_lambda
        BigInteger ha = Paillier.decrypt(a_lambda,sk);
        BigInteger hb = Paillier.decrypt(b_lambda,sk);
        BigInteger h = Paillier.encrypt(ha.multiply(hb).mod(pk.n),pk);
        //return new BigInteger[]{a_lambda,b_lambda};
        //S1:接受h
        BigInteger s =  h.multiply(a.modPow(pk.n.subtract(randB),pk.modulus));
        BigInteger s_lambda =  s.multiply(b.modPow(pk.n.subtract(randA),pk.modulus));
        BigInteger result = s_lambda.multiply(Paillier.encrypt(randA.multiply(randB),pk).modPow(pk.n.subtract(BigInteger.ONE),pk.modulus));
//        long end = System.currentTimeMillis();
//        System.out.println(end - start);
        return result;

    }

    // 位分解
    public static BigInteger[] SBD(BigInteger z , PaillierPK pk, PaillierSK sk){
//        long start = System.currentTimeMillis();
        int m = 32;
        BigInteger l = new BigInteger("2").modInverse(pk.n);
        BigInteger T = z;
        BigInteger[] x = new BigInteger[m];
        BigInteger Z;
        for(int i = 0 ; i < m ; i++){
            x[i] = Encrypted_LSB(T,i, pk, sk);
            Z = T.multiply(x[i].modPow(pk.n.subtract(BigInteger.ONE),pk.modulus));
            T = Z.modPow(l,pk.n.multiply(pk.n));
        }
//        long end = System.currentTimeMillis();
//        System.out.println("sbd");
//        System.out.println(end-start);
        return x;
    }

    // SBD调用函数
    public static BigInteger Encrypted_LSB(BigInteger t, int i, PaillierPK pk, PaillierSK sk){
        //S1：
        BigInteger r = new BigInteger(2,new Random());
        BigInteger y = t.multiply(Paillier.encrypt(r, pk)).mod(pk.modulus);
//        System.out.println("y" + paillier.Dec(y));
        //S2:
        BigInteger alpha;
        BigInteger y_dec = Paillier.decrypt(y, sk);
        if(y_dec.mod(new BigInteger("2")).compareTo(new BigInteger("0"))==0){
            alpha = Paillier.encrypt(BigInteger.ZERO, pk);
        }else {
            alpha = Paillier.encrypt(BigInteger.ONE, pk);
        }
        //S1:
        BigInteger Exi;
        if(r.mod(new BigInteger("2")).compareTo(BigInteger.ZERO)==0){
            Exi = alpha;
        }else {
            Exi = Paillier.encrypt(BigInteger.ONE, pk).multiply(alpha.modPow(pk.n.subtract(BigInteger.ONE),pk.modulus));
        }
        return Exi;
    }
    // 需要修改
//    //b<=a 返回1
//    public static BigInteger SecLessEqual(BigInteger a , BigInteger b, PaillierPK pk, PaillierSK sk, DGKOperations dgk) {
//    //    long startTime = System.currentTimeMillis();
//        BigInteger re = dgk.Protocol2(a,b,dgk.getPublicKey(),dgk.getPrivateKey(),pk,sk);
//        BigInteger res = Paillier.encrypt(BigInteger.ONE,pk).multiply(re.modPow(pk.n.subtract(BigInteger.ONE),pk.modulus));
//     //   long endTime = System.currentTimeMillis();
//    //    System.out.println(startTime - endTime);
//        return res;
//    }
    //a=b 返回1

    // 判断加密数是否相等
    public static BigInteger SecEqual(BigInteger a, BigInteger b, PaillierPK pk, PaillierSK sk){
//        Server1：
   //     long start = System.currentTimeMillis();
        BigInteger res = a.multiply(b.modPow(pk.n.subtract(BigInteger.ONE),pk.modulus));
//        S2：
//        long end = System.currentTimeMillis();
       // System.out.println(end - start);
        BigInteger decRes = Paillier.decrypt(res,sk);
        //如果两个数相等，S2返回给s1  1，否则给0作为标志。
        if(decRes.compareTo(BigInteger.ZERO)==0){
            return Paillier.encrypt(BigInteger.ONE,pk);
        }
        else
            return Paillier.encrypt(BigInteger.ZERO,pk);
    }

//    public static BigInteger SecLess(BigInteger a, BigInteger b, PaillierPK pk, PaillierSK sk, DGKOperations dgk){
//        //b<a,返回1
////        long start = System.currentTimeMillis();
//        if(Paillier.decrypt(SecLessEqual(a,b,pk,sk,dgk),sk).compareTo(BigInteger.ONE)==0&&
//                Paillier.decrypt(SecEqual(a,b,pk,sk),sk).compareTo(BigInteger.ZERO)==0){
//        //    System.out.println(System.currentTimeMillis() - start);
//            return Paillier.encrypt(BigInteger.ONE,pk);
//        }
//
//      //  System.out.println(System.currentTimeMillis() - start);
//            return Paillier.encrypt(BigInteger.ZERO,pk);
//    }
    //p是加密过的数组
    public static BigInteger SecMin(PaillierPK pk, PaillierSK sk, BigInteger... p){
   //     long start = System.currentTimeMillis();
        //S1：
        BigInteger[] r = new BigInteger[new Random().nextInt(64) + p.length];
        r[0] = new BigInteger(r.length,new Random());
        BigInteger tmin = r[0];
        for(int i = 0 ; i < r.length ; i++){
            r[i] = new BigInteger(r.length,new Random());
            if(r[i].compareTo(tmin)==-1){
                tmin = r[i];
                r[i] = r[0];
                r[0] = tmin;
            }
        }
        BigInteger[] EncR = new BigInteger[r.length];
        for(int i = 0 ; i < r.length ; i++)
             EncR[i] = Paillier.encrypt(r[i],pk);
        BigInteger[] EncCan = new BigInteger[p.length+r.length];
        for(int i = 0 ; i < p.length ; i++){
            EncCan[i]  = p[i].multiply(EncR[0]).mod(pk.modulus);
        }
        for(int i = p.length ; i < EncCan.length ; i++){
            EncCan[i] = EncR[i-p.length].multiply(p[(int)(Math.random()*p.length)]).mod(pk.modulus);
        }
        //S2:收到EncCan数组
        BigInteger[] candidates = new BigInteger[EncCan.length];
        for(int i = 0 ; i < EncCan.length ; i++){
            candidates[i] = Paillier.decrypt(EncCan[i],sk);
        }
        BigInteger decmin = candidates[0];
        for(int i = 1 ; i < candidates.length ; i++){
            if(candidates[i].compareTo(decmin)==-1)
                decmin = candidates[i];
        }
        BigInteger encMin = Paillier.encrypt(decmin,pk);

        // S1:收到加密的最小值 encMin
        //计算Enc(m-rmin) ,,,m->encMin;rmin->r[0],加密之后是EncR[0];
        BigInteger res = encMin.multiply(EncR[0].modPow(pk.n.subtract(BigInteger.ONE),pk.modulus));
    //    System.out.println(System.currentTimeMillis() - start);
        return res;
    }

    // 首先计算Enc(a-b)
    // alpha = Enc[(a-b)*random] = Enc(a-b)^random mod N^2
    // s2 判断alpha大于0还是小于0
    // 由于BigInteger没有负数，但是如果BigInteger的计算结果是负数，则结果会是一个非常大的数，猜测是补码的原因
    // 由于现实数据不可能有那么大，所以如果相减的结果是一个非常大的BigInteger，则说明a<b
    // a<b return 1 else return 0
    public static BigInteger newSecLess(BigInteger a, BigInteger b, PaillierPK pk, PaillierSK sk){
        BigInteger sub = a.multiply(b.modPow(pk.n.subtract(BigInteger.ONE), pk.modulus));
        BigInteger random = new BigInteger(String.valueOf(new Random().nextInt(9999)));
        BigInteger alpha = sub.modPow(random, pk.modulus);
        // s2
        BigInteger res = Paillier.decrypt(alpha, sk);
        BigInteger result;
        if (res.compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) > 0)
            result = Paillier.encrypt(BigInteger.ONE, pk);
        else result = Paillier.encrypt(BigInteger.ZERO, pk);
        return result;
    }

}















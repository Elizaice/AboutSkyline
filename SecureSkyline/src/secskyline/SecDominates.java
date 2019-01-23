package secskyline;

import security.Paillier;
import security.PaillierPK;
import security.PaillierSK;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by fubin on 2019/1/17.
 */
public class SecDominates {
    public static int[][] f = {
            {4,400},
            {24,380},
            {14,340},
            {36,300},
            {26,280},
            {8,260},
            {40,200},
            {20,180},
            {34,140},
            {28,120},
            {16,60}
    };
    public static void main(String[] args){
        BigInteger[] a = new BigInteger[3];
        BigInteger[] b = new BigInteger[3];
        PaillierPK pk = new PaillierPK(1024);
        PaillierSK sk = new PaillierSK(1024);
        Paillier.keyGen(sk,pk);
        for(int i = 0 ; i < a.length ;i++){
            a[i]= Paillier.encrypt(new BigInteger("3"),pk);
            b[i] = Paillier.encrypt(new BigInteger("4"),pk);
        }
        System.out.println("b支配a返回 "+ Paillier.decrypt(newSecDom(a,b,pk,sk),sk));
        BigInteger[][] BigF = new BigInteger[f.length][f[0].length];
        for(int i = 0 ; i< f.length ; i++){
            for(int j = 0 ; j < f[i].length ; j++){
                BigF[i][j] = Paillier.encrypt(new BigInteger(Integer.toString(f[i][j])),pk);
            }
        }
        BigInteger[] query = new BigInteger[f[0].length];
        for(int i = 0 ; i < query.length ; i++){
            query[i] = new BigInteger(Integer.toString(i+1));
        }
        BigInteger[][] encF = preProcessing(BigF,query,pk,sk);

    }
    //如果a被结果集中的点支配返回加密的0，否则返回加密的1
    public static BigInteger secIsDom(BigInteger[] a , List<BigInteger[]> res, PaillierPK pk, PaillierSK sk){
        long start = System.currentTimeMillis();
        for(int i = 0 ; i < res.size() ; i++){
            //S2：从secDom收到结果进行解密：
//            BigInteger beta = Paillier.decrypt(secDom(a,res.get(i),pk,sk,dgk),sk);
            BigInteger beta = Paillier.decrypt(newSecDom(a, res.get(i), pk, sk), sk);
            if(beta.compareTo(BigInteger.ONE)==0){
                //a被结果集中点支配。
                return Paillier.encrypt(BigInteger.ZERO,pk);
            }
        }
        System.out.println("支配所需时间：");
        System.out.println(System.currentTimeMillis() - start);
        return Paillier.encrypt(BigInteger.ONE,pk);
    }
    // ICDE原文中的secDom
//    //b支配a，返回结果为E(1）.
//    public static BigInteger secDom(BigInteger[] a, BigInteger[] b, PaillierPK pk, PaillierSK sk){
//        BigInteger[] delta = new BigInteger[a.length];
//        //B<=A DELTA= 1;
//        for(int i = 0 ; i < a.length ; i++){
//            delta[i] = encComp.SecLessEqual(a[i],b[i],pk,sk,dgk);
//        }
//        BigInteger fai = Paillier.encrypt(new BigInteger("1"),pk);
//        for(int i = 0 ; i < a.length;i++){
//            fai = encComp.SM(fai, encComp.SM(fai,delta[i],pk,sk), pk, sk);
//        }
//        //s1:
//        BigInteger alpha = new BigInteger("1");
//        BigInteger beta = new BigInteger("1");
//        for(int i = 0 ; i < a.length ; i++){
//            alpha = alpha.multiply(a[i]);
//        }
//        for(int i = 0 ; i < b.length ; i++){
//            beta = beta.multiply(b[i]);
//        }
//        //S1,S2:
//        //S1:
//        BigInteger theata = encComp.newSecLess(alpha,beta,pk,sk);
//        BigInteger phai = encComp.SM(fai,theata,pk,sk);
//        return phai;
//    }


    //P是加密的数据集，客户端的q是未加密的
    public static BigInteger[][] preProcessing(BigInteger[][] p, BigInteger[] q, PaillierPK pk, PaillierSK sk){
        BigInteger[][] encP  = new BigInteger[p.length][p[0].length];
        //Client给S1：encq
        BigInteger[] encq = new BigInteger[q.length];
        for(int i = 0 ; i < q.length ; i++){
            System.out.println("未加密的Q  "+q[i]);
            encq[i] = Paillier.encrypt(q[i],pk);
        }
        //S1:
        BigInteger[][] temp = new BigInteger[p.length][p[0].length];
        for(int i = 0 ; i < p.length; i ++){
            for(int j = 0 ; j < p[i].length ; j++){
                temp[i][j] = p[i][j].multiply(encq[j].modPow(pk.n.subtract(BigInteger.ONE),pk.modulus));
            }
        }
        //S1,S2:
        for(int i = 0 ; i < p.length ; i++){
            for(int j = 0 ; j < p[i].length ; j++){
                encP[i][j] = encComp.SM(temp[i][j],temp[i][j],pk,sk);
            }
        }
//        for(int i = 0 ; i < p.length ; i++){
//            for(int j = 0 ; j < p[i].length ; j++){
//                System.out.println("dec "+Paillier.decrypt(encP[i][j],sk));
//            }
//        }
        return encP;
    }

    // b支配a，返回E(1)
    public static BigInteger newSecDom(BigInteger[] a, BigInteger[] b, PaillierPK pk, PaillierSK sk){
        BigInteger fai = Paillier.encrypt(BigInteger.ZERO, pk);
        for(int i = 0; i < a.length; i++) {
            fai = fai.multiply(encComp.SecEqual(a[i], b[i], pk, sk));
        }
        // 全都等于0，不支配
        if(Paillier.decrypt(fai, sk).compareTo(new BigInteger(String.valueOf(a.length))) == 0){
            return Paillier.encrypt(BigInteger.ZERO, pk);
        }


        for(int i = 0; i < a.length; i++) {
            // 只要有一个a[i]比b[i]小，则a就没被b支配
            if(Paillier.decrypt(encComp.newSecLess(a[i], b[i], pk, sk), sk).compareTo(BigInteger.ONE) == 0)
                return Paillier.encrypt(BigInteger.ZERO, pk);
        }
        return Paillier.encrypt(BigInteger.ONE, pk);

    }
}

































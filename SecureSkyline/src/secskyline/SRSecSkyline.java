package secskyline;

import MyRtree.Key;
import MyRtree.MBR;
import MyRtree.Rtree;
import security.Paillier;
import security.PaillierPK;
import security.PaillierSK;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static secskyline.SRTree.createSRtree;
import static secskyline.SRTree.printTree;

/**
 * Created by fubin on 2019/1/19.
 */
public class SRSecSkyline {
    public static int[][] f = {
            {4,400},    //0
            {24,380},   //1
            {14,340},   //2
            {36,300},   //3
            {26,280},   //4
            {8,260},    //5
            {40,200},   //6
            {20,180},   //7
            {34,140},   //8
            {28,120},   //9
            {16,60},    //10
    };
    public static MBR mbr;
    public static Key query;
    public static void main(String[] args) {
        PaillierPK pk = new PaillierPK(512);
        PaillierSK sk = new PaillierSK(512);
        Paillier.keyGen(sk, pk);
        BigInteger[][] BigF = new BigInteger[f.length][f[0].length];
        for(int i = 0 ; i< f.length ; i++){
            for(int j = 0 ; j < f[i].length ; j++){
                BigF[i][j] = new BigInteger(Integer.toString(f[i][j]));
            }
        }
        Rtree tree = new Rtree(3,1);
        for(int i = 0 ; i < f.length;i++){
            tree.insert(i,BigF[i]);
        }
        createSRtree(tree.getRoot(),pk);
        printTree(tree.getRoot(), sk, pk);
        BigInteger[] q = new BigInteger[2];
        q[0] = Paillier.encrypt(new BigInteger("0"),pk);
        q[1] = Paillier.encrypt(new BigInteger("0"),pk);
        mbr = new MBR(q,q);
        query = new Key(-1,mbr);
        long start = System.currentTimeMillis();
        List<BigInteger[]> skyline = secbbs(tree,query,pk,sk);
        System.out.println("查询时间：" + (System.currentTimeMillis() - start));
        for(int i = 0 ; i < skyline.size() ; i++){
            System.out.println("skyline "+ Paillier.decrypt(skyline.get(i)[0],sk)+","+ Paillier.decrypt(skyline.get(i)[1],sk));
        }
    }

    //srtree是加密，q也是
    public static List<BigInteger[]> secbbs(Rtree srTree, Key q, PaillierPK pk, PaillierSK sk) {
        List<BigInteger[]> secResult = new ArrayList<>();
        PriorityQueue<Key> secHeap = new PriorityQueue<>(new Comparator<Key>() {
            @Override
            public int compare(Key o1, Key o2) {
//                return (int)(o1.getMbr().priority(q).subtract(o2.getMbr().priority(q)));
                // o1 = o2 secequal return 1
                if(Paillier.decrypt(encComp.SecEqual(o1.getMbr().secPriority(q, pk), o2.getMbr().secPriority(q, pk), pk, sk), sk).compareTo(BigInteger.ONE) == 0) {
                    return 0;
                }
                // o1 < o2 secless return 0
//                BigInteger alpha = encComp.SecLess(o1.getMbr().secPriority(q, pk), o2.getMbr().secPriority(q, pk), pk, sk,dgk);
                // new o1 < o2 return 1
                BigInteger alpha = encComp.newSecLess(o1.getMbr().secPriority(q, pk), o2.getMbr().secPriority(q, pk), pk, sk);
                //S1 把 alpha发送给S2：
                //S2:
                BigInteger delta = Paillier.decrypt(alpha, sk);
                if (delta.compareTo(BigInteger.ONE) == 0)
                    return -1;
                //S2把beta返回给S1：
                //o1<o2：返回-1，
                return 1;

            }
        });
        for (int i = 0; i < srTree.getRoot().getKeys().size(); i++) {
            secHeap.add(srTree.getRoot().getKeys().get(i));
        }
        while (!secHeap.isEmpty()) {
            Key e = secHeap.poll();
            //S2解密
//            System.out.println(e.getId());
//            for(int i = 0; i < e.getMbr().getMinDim().length; i++){
//                System.out.print(Paillier.decrypt(e.getMbr().getMinDim()[i], sk) + ",");
//            }
//            System.out.println();
            BigInteger delta = Paillier.decrypt(SecDominates.secIsDom(e.getMbr().getMinDim(), secResult, pk, sk), sk);
            if (delta.compareTo(BigInteger.ZERO) == 0)
                continue;
            if (e.getNode().getNodeType() == 1 || e.getNode().getNodeType() == 0) {
                for (Key child : e.getChildNode().getKeys()) {
                    //s2解密：
                    BigInteger beta = Paillier.decrypt(SecDominates.secIsDom(child.getMbr().getMinDim(), secResult, pk, sk), sk);
                    if (beta.compareTo(BigInteger.ONE) == 0) {
                        secHeap.add(child);
                    }
                }
            } else {
                secResult.add(e.getMbr().getMinDim());
            }
        }
        return secResult;
    }
}

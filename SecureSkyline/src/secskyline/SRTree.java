package secskyline;

import MyRtree.Key;
import MyRtree.MBR;
import MyRtree.Node;
import MyRtree.Rtree;

import security.Paillier;
import security.PaillierPK;
import security.PaillierSK;

import java.math.BigInteger;

/**
 * Created by fubin on 2019/1/20.
 */
public class SRTree {
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
        PaillierPK pk = new PaillierPK(1024);
        PaillierSK sk = new PaillierSK(1024);
        Paillier.keyGen(sk,pk);
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
        simplePrintTree(tree.getRoot());
        createSRtree(tree.getRoot(),pk);
        System.out.println("--------------");
        printTree(tree.getRoot(), sk, pk);

    }
    public static void createSRtree(Node node, PaillierPK pk){
        if(node == null)
            return;

        for(Key k : node.getKeys()){
            BigInteger[] minDim = k.getMbr().getMinDim();
            BigInteger[] maxDim = k.getMbr().getMaxDim();
            BigInteger[] encMin = new BigInteger[minDim.length];
            BigInteger[] encMax = new BigInteger[maxDim.length];
            for(int i = 0 ; i < k.getMbr().getMinDim().length ; i++){
                encMin[i] = Paillier.encrypt(minDim[i],pk);
            }
            for(int i = 0 ; i < maxDim.length ; i++){
                encMax[i] = Paillier.encrypt(maxDim[i],pk);
            }
            MBR encMBR = new MBR(encMin, encMax);
            k.setMbr(encMBR);
            createSRtree(k.getChildNode(),pk);
        }


    }
    private static void simplePrintTree(Node node){
        if(node.getNodeType()==2){
            System.out.println("node mbr:");
            System.out.println(node.mbr().getMinDim()[0] + " " + node.mbr().getMinDim()[1] + "," + node.mbr().getMaxDim()[0] + " " + node.mbr().getMaxDim()[1]);
            for(Key k: node.getKeys()){
                System.out.println("leaf node:");
                System.out.println(k.getId()+" ,"+k.getMbr().getMinDim()[0] + " "  + k.getMbr().getMinDim()[1]);
            }
        }else {
            for(Key key:node.getKeys()){
                if(node.getNodeType() == 0) {
                    System.out.println("root node:");
                } else {
                    System.out.println("simple node:");
                }
                System.out.println(node.mbr().getMinDim()[0] + " " + node.mbr().getMinDim()[1] + "," + node.mbr().getMaxDim()[0] + " " + node.mbr().getMaxDim()[1]);
                simplePrintTree(key.getChildNode());
            }
        }
    }

    public static void printTree(Node node, PaillierSK sk, PaillierPK pk){
        MBR nodeMbr = node.secMbr(pk, sk);
        if(node.getNodeType()==2){
            System.out.println("node mbr:");
            for(int i = 0; i < nodeMbr.getMinDim().length; i++) {
                System.out.print(Paillier.decrypt(nodeMbr.getMinDim()[i], sk) + ",");
            }
            for(int i = 0; i < node.mbr().getMinDim().length; i++) {
                System.out.print(Paillier.decrypt(nodeMbr.getMaxDim()[i], sk) + ",");
            }
            System.out.println();
            for(Key k: node.getKeys()){
                System.out.println("leaf node:");
                for(int i = 0; i < k.getMbr().getMinDim().length; i++) {
                    System.out.print(Paillier.decrypt(k.getMbr().getMinDim()[i], sk) + ",");
                }
                System.out.println();
            }
        }else {
            for(Key key:node.getKeys()){
                if(node.getNodeType() == 0) {
                    System.out.println("root node:");
                } else {
                    System.out.println("simple node:");
                }
                for(int i = 0; i < node.mbr().getMinDim().length; i++) {
                    System.out.print(Paillier.decrypt(nodeMbr.getMinDim()[i], sk) + ",");
                }
                for(int i = 0; i < node.mbr().getMinDim().length; i++) {
                    System.out.print(Paillier.decrypt(nodeMbr.getMaxDim()[i], sk) + ",");
                }
                System.out.println();
                printTree(key.getChildNode(), sk, pk);
            }
        }
    }
}

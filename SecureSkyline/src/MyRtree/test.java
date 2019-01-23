package MyRtree;


import java.math.BigInteger;

public class test {
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
            {16,60},
    };
    public static void main(String[] args){
        Rtree tree = new Rtree(3,1);
        BigInteger[][] BigF = new BigInteger[f.length][f[0].length];
        for(int i = 0 ; i< f.length ; i++){
            for(int j = 0 ; j < f[i].length ; j++){
                BigF[i][j] = new BigInteger(Integer.toString(f[i][j]));
            }
        }
        for(int i = 0 ; i < f.length;i++){
            tree.insert(i,BigF[i]);
        }
        printTree(tree.getRoot());

    }
    private static void printTree(Node node){
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
                printTree(key.getChildNode());
            }
        }
    }
}

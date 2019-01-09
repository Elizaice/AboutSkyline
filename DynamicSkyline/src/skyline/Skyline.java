package skyline;

import MyRtree.Key;
import MyRtree.MBR;
import MyRtree.Node;
import MyRtree.Rtree;
import com.alibaba.fastjson.JSONArray;
import Data.Data;

import java.io.*;
import java.util.*;


public class Skyline {
    public static float[][] f = {
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
    public static MBR mbr;
    public static Key query;
    public static void main(String[] args) throws Exception{
        f = Data.getData();
        float[][] q = Data.getQuery();
        mbr = new MBR(q[1], q[1]);
        query = new Key(-1, mbr);
        Rtree tree = new Rtree(3,1);
        for(int i = 0 ; i < f.length;i++){
            tree.insert(i,f[i]);
        }
        printTree(tree.getRoot());



        List<Integer> skyline = bbs(tree,query);
        for(int i = 0 ; i < skyline.size() ; i++){
           for(int j = 0 ; j < f[i].length ; j++)
                System.out.print(f[skyline.get(i)][j]+",");
           System.out.println();
        }
        System.out.println("Skyline结果id：");
        System.out.println(skyline);

        System.out.println("treejson");
        String jsonstr = tree.toJson();
        System.out.println(jsonstr);
        tree.fromJson(jsonstr);
        printTree(tree.getRoot());
        skyline = bbs(tree,query);
        System.out.println("Skyline结果id：");
        System.out.println(skyline);
    }

    private static float[][] readFile(String points_file, String query_file) throws FileNotFoundException,IOException{
        float[][] points;
        File p_file = new File(points_file);
        BufferedReader in = new BufferedReader(new FileReader(points_file));
        String line;
        line = in.readLine();
        System.out.println(line);
        JSONArray a = JSONArray.parseArray(line);

        return null;

    }
    private static List<Integer> bbs(Rtree rtree, Key q) {
        List<Integer> result = new ArrayList<>();
        PriorityQueue<Key> heap = new PriorityQueue<>(new Comparator<Key>() {
            @Override
            public int compare(Key o1, Key o2) {
                return (int)(o1.getMbr().priority(q)-o2.getMbr().priority(q));
            }
        });
        for(int i = 0 ; i < rtree.getRoot().getKeys().size();i++)
            heap.add(rtree.getRoot().getKeys().get(i));
        while (!heap.isEmpty()){
            Key e = heap.poll();
            if(isDom(e,result))
                continue;

            if(e.getNode().getNodeType()==1||e.getNode().getNodeType()==0){

                for(Key child : e.getChildNode().getKeys()){
                    if(!isDom(child,result))
                        heap.add(child);
                }
            }  else{
                result.add(e.getId());
            }
        }

        return result;
    }

    //key被结果集支配返回true
    private static boolean isDom(Key key, List<Integer> res){
        boolean calRelative = true;
        if(key.getNode().getNodeType() != 2)
            calRelative = false;
        for(int i = 0 ; i < res.size(); i++){
            int index = res.get(i);
            MBR mbr = new MBR(f[index],f[index]);
            if(mbr.dominates(key.getMbr(), query, calRelative)){

                return true;
            }
        }
        return false;
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

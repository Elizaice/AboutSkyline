package MyRtree;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private Key parent;
    private List<Key> keys;     //
    private int nodeType;   //节点类型，0——根节点，1——中间节点，2——叶子节点

    public Node() {
        this.parent = null;
        this.keys = new ArrayList<>();
        this.nodeType = 2;
    }

    public Node(int nodeType) {
        this.parent = null;
        this.keys = new ArrayList<>();
        this.nodeType = nodeType;
    }


    public Key getParent() {
        return parent;
    }

    public void setParent(Key parent) {
        this.parent = parent;
    }

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(ArrayList<Key> keys) {
        this.keys = new ArrayList<>(keys);
    }

    public void addKey(Key key) {
        this.keys.add(key);
    }

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    //把一个节点分裂成两个
    public Node[] split(int m){
        Key[] seeds = pickSeeds();
        Key K1 = seeds[0];
        Key K2 = seeds[1];
        this.keys.remove(K1);
        this.keys.remove(K2);
        Node N1 = new Node(this.nodeType);
        N1.keys.add(K1);
        N1.parent = this.parent; //用N1替换当前节点
        if(this.parent != null) {
            this.parent.setChildNode(N1);
        }

        Node N2 = new Node(this.nodeType);
        N2.keys.add(K2);
        // 检查是否已经分配完毕，如果一个组中的条目太少，为避免下溢，将剩余的所有条目全部分配到这个组中，算法终止
        while(this.keys.size() > 0) {
            if(N1.keys.size() + this.keys.size() < m) {
                for(Key key : this.keys) {
                    N1.keys.add(key);
                }
                this.keys.clear();
                break;
            }
            else if(N2.keys.size() + this.keys.size() < m) {
                for(Key key : this.keys) {
                    N2.keys.add(key);
                }
                this.keys.clear();
                break;
            }
            // 调用pickNext来选择下一个进行分配的条目
            // 计算把每个条目加入每个组之后面积的增量，选择两个组面积增量差最大的条目索引，如果面积增量相等则选择面积较小的组，若面积也相等则选择条目数更少的组
            Key nextKey = pickNext(N1, N2);

            MBR mbrN1 = N1.mbr();
            MBR mbrN2 = N2.mbr();

            float enlargementInN1 = mbrN1.combine(nextKey.getMbr()).getArea();
            float enlargementInN2 = mbrN2.combine(nextKey.getMbr()).getArea();
            if(enlargementInN1 <= enlargementInN2) {
                nextKey.setNode(N1);
                N1.keys.add(nextKey);
            } else {
                nextKey.setNode(N2);
                N2.keys.add(nextKey);
            }
            this.keys.remove(nextKey);
        }
        for(Key key : N1.keys) {
            key.setNode(N1);
        }
        for(Key key : N2.keys) {
            key.setNode(N2);
        }
        return new Node[]{N1, N2} ;
    }

    //
    public Key[] pickSeeds(){
        Key k1 = null;
        Key k2 = null;
        //最大冗余面积
        float maxExpandableArea = -1;
        List<Key> keys = this.keys;
        int count = keys.size();
        for(int i = 0 ; i < count - 1; i++){
            Key tmp_k1 = keys.get(i);
            for(int j = i+1; j < count ; j++){
                Key tmp_k2 = keys.get(j);
                MBR combineRegion = tmp_k1.getMbr().combine(tmp_k2.getMbr());
                float expandableArea = combineRegion.getArea()-tmp_k1.getMbr().getArea()-tmp_k2.getMbr().getArea();
                if(expandableArea>maxExpandableArea){
                    maxExpandableArea = expandableArea;
                    k1 = tmp_k1;
                    k2 = tmp_k2;
                }
            }
        }
        return new Key[]{k1,k2};
    }
    // 计算把每个条目加入每个组之后面积的增量，选择两个组面积增量差最大的条目索引
    public Key pickNext(Node N1, Node N2){
        float maxExpandableArea = -1;
        Key K1 = null;

        MBR mbrN1 = N1.mbr();
        MBR mbrN2 = N2.mbr();

        for(Key key : this.keys) {
            MBR mbrExpandN1 = mbrN1.combine(key.getMbr());
            MBR mbrExpandN2 = mbrN2.combine(key.getMbr());
            float diff1 = mbrExpandN1.getArea() - mbrN1.getArea();
            float diff2 = mbrExpandN2.getArea() - mbrN2.getArea();
            float expandableArea = Math.abs(diff1 - diff2);
            if(expandableArea > maxExpandableArea) {
                maxExpandableArea = expandableArea;
                K1 = key;
            }
        }
        return K1;
    }

    //Node是否满了
    public boolean isFull(int M){
//        if(this.parent==null){
//            if(this.keys.size()==2)
//                return true;
//            else
//                return false;
//        }
        if(this.keys.size()==M)
            return true;
        return false;
    }
    //得到节点的MBR
    public MBR mbr(){
        if(keys.size() > 0) {
            MBR totMbr = keys.get(0).getMbr();
            for(int i = 1 ; i < keys.size();i++){
                totMbr = totMbr.combine(keys.get(i).getMbr());
            }
            return totMbr;
        }
        return null;
    }

    public boolean isLeaf(){
        if(this.nodeType == 2) {
            return true;
        }
        return false;
    }
    public boolean isRoot(){
        if(this.nodeType == 0) {
            return true;
        }
        return  false;
    }


}


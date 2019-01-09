package MyRtree;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Rtree {
    private int M;  //最大节点个数
    private int m;  //最小节点个数
    private Node root;  //根节点
    public int getM() {
        return M;
    }

    public void setM(int m) {
        M = m;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }


    public Rtree(int M, int m) {
        this.M = M;
        this.m = m;
        this.root = new Node();
    }

    public void insert(int id, float[] point){
        MBR mbr = new MBR(point,point);
        Key key = new Key(id,mbr);
        Node leafNode = chooseLeaf(root,key);
        key.setNode(leafNode);
        Node[] nodes = null;
        if(!leafNode.isFull(M)){
            leafNode.addKey(key);
            nodes = new Node[]{leafNode, null};
        }else {
            leafNode.addKey(key);
            nodes = leafNode.split(m);
        }
        adjust(nodes);
    }
    public Node chooseLeaf(Node node, Key key){
        if(node.getNodeType()==2)
            return node;
        float minExpandedArea = Float.MAX_VALUE;
        Node l = null;
        for(Key k : node.getKeys()){
            MBR combineArea = k.getMbr().combine(key.getMbr());
            float expandableArea = combineArea.getArea()-k.getMbr().getArea();
            if(minExpandedArea>expandableArea){
                minExpandedArea = expandableArea;
                l = k.getChildNode();
            }
            else if(minExpandedArea==expandableArea) {
                if(l.mbr().getArea()>k.getMbr().getArea()){
                    l=k.getChildNode();
                }
            }
        }
        return  chooseLeaf(l,key);
    }
    //调整
    public void adjust(Node[] nodes){
        Node N1 = nodes[0];
        Node N2 = nodes[1];
        if(N1.getParent()==null){
            if(N2!=null){
                makeRoot(N1,N2);
            }
            return;
        }
        N1.getParent().setMbr(N1.mbr());
        Node parentNode = N1.getParent().getNode();

        if(N2!=null){
            Key newKey = new Key(N2.mbr(),parentNode);
            N2.setParent(newKey);
            newKey.setChildNode(N2);
            if(!parentNode.isFull(M)){
                parentNode.addKey(newKey);
                adjust(new Node[]{parentNode,null});
            }else {
                parentNode.addKey(newKey);
                adjust(parentNode.split(m));
            }
        }else {
            adjust(new Node[]{parentNode,null});
        }
    }
    //造根节点
    public void makeRoot(Node N1,Node N2){
        if(N1.getNodeType()!=2)
            N1.setNodeType(1);
        if(N2.getNodeType()!=2)
            N2.setNodeType(1);
        root = new Node(0);
        Key newKey = new Key(N1.mbr(), root);
        N1.setParent(newKey);
        newKey.setChildNode(N1);
        root.addKey(newKey);
        newKey = new Key(N2.mbr(), root);
        N2.setParent(newKey);
        newKey.setChildNode(N2);
        root.addKey(newKey);

    }

    public String toJson() {
        String tree = "{";
        return tree + dfs(this.root) + "}";
    }

    public String dfs(Node tmpRoot) {
        if(tmpRoot == null)
            return "";
        String tree = String.format("\"Node\":{\"type\":%d,\"keys\":{", tmpRoot.getNodeType());
        int keyNum = 0;
        for(Key key : tmpRoot.getKeys()) {
            tree = tree + "\"key" + keyNum + "\":{";
            tree += String.format("\"id\":%d,", key.getId());
            tree += "\"mbr\":{\"maxdim\":[";

            float[] maxDim = key.getMbr().getMaxDim();
            for(int i = 0; i < maxDim.length; i++) {
                if(i != maxDim.length - 1)
                    tree += maxDim[i] + ",";
                else
                    tree += maxDim[i];
            }
            tree += "],\"mindim\":[";
            float[] minDim = key.getMbr().getMinDim();
            for(int i = 0; i < minDim.length; i++) {
                if(i != minDim.length - 1)
                    tree += minDim[i] + ",";
                else
                    tree += minDim[i];
            }
            tree += "]},\"childnode\":{";
            tree += dfs(key.getChildNode());
            tree += "}},";
            keyNum++;
        }
        tree = tree.substring(0, tree.length()-1);
        tree += "}}";
        return tree;
    }

    public void fromJson(String string) {
        this.root = dfsCreateTree(JSONObject.parseObject(string).get("Node").toString());

    }
    public Node dfsCreateTree(String string) {
        JSONObject rootNode = JSONArray.parseObject(string);
        Node node = new Node();
        node.setNodeType(Integer.valueOf(rootNode.get("type").toString()));
        JSONObject keys = JSONArray.parseObject(rootNode.get("keys").toString());
        for(int i = 0; i < keys.size(); i++) {
            JSONObject keyJson = JSONArray.parseObject(keys.get("key"+i).toString());
            int id = Integer.valueOf(keyJson.get("id").toString());
            JSONObject mbr = JSONArray.parseObject(keyJson.get("mbr").toString());
            JSONArray maxdimJson = JSONArray.parseArray(mbr.get("maxdim").toString());
            JSONArray mindimJson = JSONArray.parseArray(mbr.get("mindim").toString());
            float[] maxdim = new float[maxdimJson.size()];
            float[] mindim = new float[mindimJson.size()];
            for(int j = 0; j < maxdim.length; j++) {
                maxdim[j] = Float.valueOf(maxdimJson.get(j).toString());
                mindim[j] = Float.valueOf(mindimJson.get(j).toString());
            }
            MBR newMbr = new MBR(mindim, maxdim);
            Key key = new Key(id, newMbr);
            key.setNode(node);
            String childnode = keyJson.get("childnode").toString();
            if(!childnode.equals("{}")) {
                childnode = JSONObject.parseObject(keyJson.get("childnode").toString()).get("Node").toString();
                key.setChildNode(dfsCreateTree(childnode));
            }
            node.addKey(key);
        }
        return node;
    }
}

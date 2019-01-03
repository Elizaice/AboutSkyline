package MyRtree;

public class Key {
    private int id;
    private MBR mbr;
    private Node node;
    private Node childNode;

    public Key(MBR mbr, Node node) {
        this.id = -1;
        this.childNode = null;
        this.mbr = mbr;
        this.node = node;
    }

    public Key(int id, MBR mbr){
        this.id = id;
        this.childNode = null;
        this.mbr = mbr;
        this.node = null;
    }

    public Node getChildNode() {
        return childNode;
    }

    public void setChildNode(Node childNode) {
        this.childNode = childNode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MBR getMbr() {
        return mbr;
    }

    public void setMbr(MBR mbr) {
        this.mbr = mbr;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}

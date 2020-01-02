import java.util.ArrayList;

public class Node {

    private int label;
    private double weight;
    private ArrayList<Node> neighbors;


    public Node() {
        neighbors=new ArrayList<>();
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public ArrayList<Node> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(ArrayList<Node> neighbors) {
        this.neighbors = neighbors;
    }

    @Override
    public String toString() {
        String s="Node{" +
                "label=" + label +
                ", weight=" + weight+
                ", neighbors=[";

        for (Node node:neighbors) {
            s+="label= "+node.getLabel()+" weight= "+node.getWeight()+",";
        }
        s+="]}";

        return  s;
    }
}

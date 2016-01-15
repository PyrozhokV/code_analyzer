package syntax;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pyrozhok on 12.01.2016.
 */
public class InterNode extends Node {

    private int id;
    static int curr_id = 0;
    private String sort;
    private List<Node> children;

    public InterNode(String sort) {
        this.sort = sort;
        this.children = new ArrayList<Node>();
        id = curr_id++;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        String res = "(" + sort;
        for (Node node : children) {
            res += " " + node.toString();
        }
        res += ")";
        return res;
    }

    public String toDot() {
        String res = "";
        for (Node node : children) {
            if (node instanceof InterNode) {
                InterNode pn = (InterNode) node;
                res += "\"" + sort + getId() + "\"->\"" + pn.getSort() + pn.getId() + "\";\n";
                res += node.toDot();
            } else {
                res += "\"" + sort + getId() + "\"->\"" + node.toDot() + "\";\n";
            }
        }
        return res;
    }
}

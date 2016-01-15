package syntax;

import lexeme.Position;

/**
 * Created by Pyrozhok on 12.01.2016.
 */
public class LeafNode extends Node {

    public Integer getValue() {
        return value;
    }

    private Integer value;

    private String stringValue;
    private Position pos;

    public LeafNode(Integer value, String stringValue, Position pos) {
        this.value = value;
        this.stringValue = stringValue;
        this.pos = pos;
    }
    public String getStringValue() {
        return stringValue;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Position getPos() {
        return pos;
    }

    public String toString() {
        return "(terminal (" + stringValue + " . " + value + ")";
    }
    public  String toDot() {
        return "(" + stringValue.toString() +  " . " + value + ")";
    }
}

package lexeme;

/**
 * Created by Pyrozhok on 14.01.2016.
 */
public class Position {

    public Position(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    private int line;
    private int column;
}

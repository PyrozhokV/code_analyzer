package syntax;

/**
 * Created by Pyrozhok on 12.01.2016.
 */
public class SyntaxAnalyzerException extends Exception {

    private int position;

    public SyntaxAnalyzerException(String message, int position) {
        super(message);
        this.position = position;
    }

    public SyntaxAnalyzerException(String message) {
        super(message);
    }

    public int getPosition() {
        return position;
    }
}

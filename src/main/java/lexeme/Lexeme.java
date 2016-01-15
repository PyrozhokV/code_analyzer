package lexeme;

/**
 * Created by Pyrozhok on 20.12.2015.
 */
public class Lexeme {

    private String value;
    private String type;

    public Lexeme(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public String getType() {
        return this.type;
    }

    public boolean equals(Object other) {
        Lexeme lexem = (Lexeme) other;
        return lexem.value.equals(this.value);
    }

    public int hashCode() {
        return value.hashCode();
    }

    public String toString() {
        return value + " (" + type + ")";
    }
}

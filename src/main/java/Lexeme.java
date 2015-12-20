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
}

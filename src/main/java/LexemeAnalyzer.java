import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pyrozhok on 20.12.2015.
 */
public class LexemeAnalyzer {

    private int COMPLEX_DELIMITERS_CODE = 301;
    private int INTEGER_CONSTANTS_CODE = 401;
    private int CHAR_CONSTANTS_CODE = 501;
    private int STRING_CONSTANTS_CODE = 601;
    private int KEYWORDS_IDENTIFIERS_CODE = 701;

    private char EOF = '\0';

    private String[] keywords = {"PROGRAM", "BEGIN", "END", "CONST"};
    private char[] delimiters = {';', '.', '='};
    private String[] complexDelimiters = {"(*", "*)", ":="};
    private char[] complexDelimitersSymbols = {'(', '*', ')', ':', '='};

    private HashMap<Lexeme, Integer> lexemesCodes = new HashMap();
    private List<Integer> encodedLexemes = new ArrayList();

    private String restCode;
    private char currentSymbol;
    private String currentLexeme;

    public HashMap<Lexeme, Integer> processCode(String code) throws Exception {
        restCode = code;
        input();

        while (currentSymbol != EOF) {
            currentLexeme = "";
            if (isLetter(currentSymbol)) {
                scanIdentifier();
                saveIdentifier();
            } else if (isDigit(currentSymbol)) {
                scanDigit();
                saveDigit();
            } else if (isDelimiter(currentSymbol)) {
                scanDelimiter();
                saveDelimiter();
            } else if (isComplexDelimiter(currentSymbol)) {
                scanAndSaveComplexDelimiter();
            } else if (isWhiteSpace(currentSymbol)) {
                input();
            }
            else {
                throw new Exception("Wrong character [" + currentSymbol + "]. \nRest code: " + restCode);
            }
        }
        return this.lexemesCodes;
    }

    private void scanAndSaveComplexDelimiter() throws Exception {
        currentLexeme += currentSymbol;
        input();
        if (isComplexDelimiter(currentSymbol)) {
            currentLexeme += currentSymbol;
            if (Arrays.asList(complexDelimiters).contains(currentLexeme)) {
                if (isNewLexeme(currentLexeme)) {
                    lexemesCodes.put(new Lexeme(currentLexeme, "COMPLEX_DELIMITER"), COMPLEX_DELIMITERS_CODE);
                    encodedLexemes.add(COMPLEX_DELIMITERS_CODE);
                    COMPLEX_DELIMITERS_CODE++;
                    input();
                }
            }
            else {
                throw new Exception("Wrong character [" + currentSymbol + "]. \nRest code: " + restCode);
            }
        }
        else {
            throw new Exception("Wrong character [" + currentSymbol + "]. \nRest code: " + restCode);
        }
    }

    private void saveDelimiter() {
        if (isNewLexeme(currentLexeme)) {
            int delimiterCode = (int)currentLexeme.charAt(0);
            lexemesCodes.put(new Lexeme(currentLexeme, "DELIMITER"), delimiterCode);
            encodedLexemes.add(delimiterCode);
        }
    }

    private void scanDelimiter() {
        currentLexeme += currentSymbol;
        input();
    }

    private void saveDigit() {
        if (isNewLexeme(currentLexeme)) {
            lexemesCodes.put(new Lexeme(currentLexeme, "NUMBER"), INTEGER_CONSTANTS_CODE);
            encodedLexemes.add(INTEGER_CONSTANTS_CODE);
            INTEGER_CONSTANTS_CODE++;
        }
    }

    private void scanDigit() {
        currentLexeme += currentSymbol;
        input();
        if (isDigit(currentSymbol)) {
            scanDigit();
        }
    }

    private void saveIdentifier() {
        if (isNewLexeme(currentLexeme)) {
            if (isKeyword(currentLexeme)) {
                lexemesCodes.put(new Lexeme(currentLexeme, "KEYWORD"), KEYWORDS_IDENTIFIERS_CODE);
            }
            else {
                lexemesCodes.put(new Lexeme(currentLexeme, "IDENTIFIER"), KEYWORDS_IDENTIFIERS_CODE);
            }
            encodedLexemes.add(KEYWORDS_IDENTIFIERS_CODE);
            KEYWORDS_IDENTIFIERS_CODE++;
        }
    }

    private void scanIdentifier() {
        currentLexeme += currentSymbol;
        input();
        if (isLetter(currentSymbol) || isDigit(currentSymbol)) {
            scanIdentifier();
        }
    }

    private void input() {
        if (restCode == "") {
            currentSymbol = EOF;
        } else {
            currentSymbol = restCode.charAt(0);
            restCode = restCode.substring(1);
        }
    }

    private boolean isKeyword(String word) {
        return Arrays.asList(keywords).contains(word);
    }

    private boolean isDelimiter(char ch) {
        return Arrays.asList(delimiters).contains(ch);
    }

    private boolean isComplexDelimiter(char ch) {
        return Arrays.asList(complexDelimitersSymbols).contains(ch);
    }

    private boolean isWhiteSpace(char ch) {
        return Character.isWhitespace(ch);
    }

    private boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    private boolean isLetter(char ch) {
        return Character.isLetter(ch);
    }

    private boolean isNewLexeme(String value) {
        List<String> values = new ArrayList<String>();
        for (Lexeme lex : lexemesCodes.keySet()) {
            values.add(lex.getValue());
        }
        return !values.contains(value);
    }

}

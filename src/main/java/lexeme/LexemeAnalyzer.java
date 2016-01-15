package lexeme;

import lexeme.Lexeme;
import syntax.InterNode;

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
    private int KEYWORDS_IDENTIFIERS_CODE = 701;

    private char EOF = '\0';

    private String[] keywords = {"PROGRAM", "BEGIN", "END", "CONST"};
    private char[] delimiters = {';', '.', '='};
    private String[] complexDelimiter = {":="};
    private char[] complexDelimitersSymbols = {':', '='};

    private HashMap<Lexeme, Integer> lexemesCodes = new HashMap();
    private List<Integer> encodedLexemes = new ArrayList();
    public List<Position> positions = new ArrayList<Position>();

    private String restCode;
    private char currentSymbol;
    private String currentLexeme;
    private int line = 1;
    private int column = 0;

    boolean commentEnd = false;

    public HashMap<Lexeme, Integer> processCode(String code) throws Exception {
        restCode = code;
        input();

        while (currentSymbol != EOF) {
            currentLexeme = "";
            if (isLetter(currentSymbol)) {
                positions.add(new Position(line, column));
                scanIdentifier();
                saveIdentifier();
            } else if (isDigit(currentSymbol)) {
                positions.add(new Position(line, column));
                scanDigit();
                saveDigit();
            } else if (isDelimiter(currentSymbol)) {
                positions.add(new Position(line, column));
                scanDelimiter();
                saveDelimiter();
            } else if (isComplexDelimiter(currentSymbol)) {
                positions.add(new Position(line, column));
                scanAndSaveComplexDelimiter();
            } else if (currentSymbol == '(' && restCode.charAt(0) == '*') {  //if comment start
                input();
                scanComment();
            } else if (isWhiteSpace(currentSymbol)) {
                input();
            }
            else {
                throw new Exception("Wrong character [" + currentSymbol + "] at line: " + line + ", column: " + column + ". \nRest code: " + restCode);
            }
        }
        return this.lexemesCodes;
    }

    private void scanComment() throws Exception {
        input();
        if (currentSymbol != EOF) {
            if (commentEnd == false && currentSymbol == '*') {
                commentEnd = true;
                scanComment();
            } else if (commentEnd && currentSymbol == ')') {
                commentEnd = false;
                input();
            } else {
                if (restCode.length() != 0) {
                    isWhiteSpace(currentSymbol);
                }
                scanComment();
            }
        }
        else {
            throw new Exception("Wrong character [" + currentSymbol + "] at line: " + line + ", column: " + column + ": comment is not closed");
        }
    }

    private void scanAndSaveComplexDelimiter() throws Exception {
        currentLexeme += currentSymbol;
        input();
        if (isComplexDelimiter(currentSymbol)) {
            currentLexeme += currentSymbol;
            if (Arrays.asList(complexDelimiter).contains(currentLexeme)) {
//                encodedLexemes.add(COMPLEX_DELIMITERS_CODE);
//                positions.add(new Position(line, column - 1));
                if (isNewLexeme(currentLexeme)) {
                    lexemesCodes.put(new Lexeme(currentLexeme, "COMPLEX_DELIMITER"), COMPLEX_DELIMITERS_CODE);
                    encodedLexemes.add(COMPLEX_DELIMITERS_CODE);
                    COMPLEX_DELIMITERS_CODE++;
                    input();
                }
                else {
                    int lexCode = lexemesCodes.get(new Lexeme(currentLexeme, "COMPLEX_DELIMITERS_CODE"));
                    encodedLexemes.add(lexCode);
                    input();
                }
            }
            else {
                throw new Exception("Wrong character [" + currentSymbol + "] at line: " + line + ", column: " + column + ". \nRest code: " + restCode);
            }
        }
        else {
            throw new Exception("Wrong character [" + currentSymbol + "] at line: " + line + ", column: " + column + ". \nRest code: " + restCode);
        }
    }

    private void saveDelimiter() {
        int delimiterCode = (int)currentLexeme.charAt(0);
//        positions.add(new Position(line, column));
        encodedLexemes.add(delimiterCode);
        if (isNewLexeme(currentLexeme)) {
            lexemesCodes.put(new Lexeme(currentLexeme, "DELIMITER"), delimiterCode);
        }
    }

    private void scanDelimiter() {
        currentLexeme += currentSymbol;
        input();
    }

    private void saveDigit() {
//        encodedLexemes.add(INTEGER_CONSTANTS_CODE);
//        positions.add(new Position(line, column));
        if (isNewLexeme(currentLexeme)) {
            lexemesCodes.put(new Lexeme(currentLexeme, "NUMBER"), INTEGER_CONSTANTS_CODE);
            encodedLexemes.add(INTEGER_CONSTANTS_CODE);
            INTEGER_CONSTANTS_CODE++;
        }
        else {
            int lexCode = lexemesCodes.get(new Lexeme(currentLexeme, "NUMBER"));
            encodedLexemes.add(lexCode);
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
//        encodedLexemes.add(KEYWORDS_IDENTIFIERS_CODE);
//        positions.add(new Position(line, column));
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
        else {
            String type;
            if (isKeyword(currentLexeme)) {
                type = "KEYWORD";
            }
            else {
                type = "IDENTIFIER";
            }
            int lexCode = lexemesCodes.get(new Lexeme(currentLexeme, type));
            encodedLexemes.add(lexCode);
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
        column++;
        if (restCode.equals("")) {
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
        for (char c : delimiters) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }

    private boolean isComplexDelimiter(char ch) {
        for (char c : complexDelimitersSymbols) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }

    private boolean isWhiteSpace(char ch) {
        if (ch == '\n') {
            line++;
            column = 0;
            return true;
        }
        return ch == ' ' || ch == 9;
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

    public List<Integer> getEncodedLexemes() {
        return this.encodedLexemes;
    }

    public HashMap<Integer, Lexeme> getEncodedLexemesTable() {
        HashMap<Integer, Lexeme> result = new HashMap<Integer, Lexeme>();
        for (Lexeme key : lexemesCodes.keySet()) {
            result.put(lexemesCodes.get(key), key);
        }
        return result;
    }

    public HashMap<Lexeme, Integer> getLexemesCodes() {
        return this.lexemesCodes;
    }
}

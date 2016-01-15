package syntax;

import lexeme.Lexeme;
import lexeme.LexemeAnalyzer;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Pyrozhok on 12.01.2016.
 */
public class SyntaxAnalyzer {

    private final LexemeAnalyzer lexer;
    public SyntaxAnalyzerException lastError;
    private InterNode tree;
    HashMap<Integer, Lexeme> table;
    List<Integer> lexemes;
    private int index = 0;

    public SyntaxAnalyzer(LexemeAnalyzer lexer) {
        this.lexer = lexer;
        table = lexer.getEncodedLexemesTable();
        lexemes = lexer.getEncodedLexemes();
    }

    public InterNode getTree() {
        return tree;
    }

    public InterNode parse() throws SyntaxAnalyzerException {
        index = 0;
        tree = parseSignalProgram();
        return tree;
    }

    public InterNode parseSignalProgram() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("SignalProgram");
        result.getChildren().add(parseProgram());
        return result;
    }

    public InterNode parseProgram() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("Program");
        result.getChildren().add(checkLiteral("PROGRAM", "KEYWORD"));
        result.getChildren().add(parseProcedureId());
        result.getChildren().add(checkLiteral(";", "DELIMITER"));
        result.getChildren().add(parseBlock());
        result.getChildren().add(checkLiteral(".", "DELIMITER"));
        return result;
    }

    private InterNode parseProcedureId() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("ProcedureIdentifier");
        result.getChildren().add(parseId());
        return result;
    }

    private Node parseId() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("Identifier");
        result.getChildren().add(parseIdSub());
        return result;
    }

    private Node parseIdSub() throws SyntaxAnalyzerException {
        if (table.get(lexemes.get(index)).getType().equalsIgnoreCase("IDENTIFIER")) {
            return new LeafNode(lexemes.get(index), table.get(lexemes.get(index)).getValue(), lexer.positions.get(index++));
        } else {
            if (lastError != null && lastError.getPosition() > index) {
                throw lastError;
            } else {
                lastError = new SyntaxAnalyzerException("Parser error: Unexpected identifier\nLine: " + lexer.positions.get(index).getLine() + "    " +
                        "Column: " + (lexer.positions.get(index).getColumn()));
                throw lastError;
            }
        }
    }

    private InterNode parseBlock() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("Block");
        result.getChildren().add(parseDeclarations());
        result.getChildren().add(checkLiteral("BEGIN", "KEYWORD"));
        result.getChildren().add(parseStatementsList());
        result.getChildren().add(checkLiteral("END", "KEYWORD"));
        return result;
    }

    private InterNode parseStatementsList() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("StatementList");
        int start_ind = index;
        try {
            result.getChildren().add(parseStatement());
        } catch (SyntaxAnalyzerException ex) {
            if (ex.getMessage().contains("Number")) {
                throw lastError;
            }
            index = start_ind;
            return result;
        }
        result.getChildren().add(parseStatementsList());
        return result;
    }

    private InterNode parseStatement() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("Statement");
        result.getChildren().add(parseVariableIdentifier());
        result.getChildren().add(checkLiteral(":=", "COMPLEX_DELIMITER"));
        result.getChildren().add(parseConstant());
        result.getChildren().add(checkLiteral(";", "DELIMITER"));
        return result;
    }

    private InterNode parseVariableIdentifier() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("VariableIdentifier");
        result.getChildren().add(parseId());
        return result;
    }

    private Node parseDeclarations() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("Declarations");
        result.getChildren().add(parseConstantDeclarations());
        return result;
    }

    private Node parseConstantDeclarations() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("ConstantDeclarations");
        result.getChildren().add(checkLiteral("CONST", "KEYWORD"));
        result.getChildren().add(parseConstantDeclarationsList());
        return result;
    }

    private Node parseConstantDeclarationsList() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("ConstantDeclarationsList");
        int start_ind = index;
        try {
            result.getChildren().add(parseConstantDeclaration());
        } catch (SyntaxAnalyzerException ex) {
            if (ex.getMessage().contains("Number")) {
                throw lastError;
            }
            index = start_ind;
            return result;
        }
        result.getChildren().add(parseConstantDeclarationsList());
        return result;
    }

    private Node parseConstantDeclaration() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("ConstantDeclaration");
        result.getChildren().add(parseConstantIdentifier());
        result.getChildren().add(checkLiteral("=", "DELIMITER"));
        result.getChildren().add(parseConstant());
        result.getChildren().add(checkLiteral(";", "DELIMITER"));
        return result;
    }

    private Node parseConstantIdentifier() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("ConstantIdentifier");
        result.getChildren().add(parseId());
        return result;
    }

    private Node parseConstant() throws SyntaxAnalyzerException {
        InterNode result = new InterNode("Constant");
        result.getChildren().add(parseUnsignedInteger());
        return result;
    }

    private Node parseUnsignedInteger() throws SyntaxAnalyzerException {
        if (table.get(lexemes.get(index)).getType().equalsIgnoreCase("NUMBER")) {
            return new LeafNode(lexemes.get(index), table.get(lexemes.get(index)).getValue(), lexer.positions.get(index++));
        } else {
            if (lastError != null && lastError.getPosition() > index) {
                throw lastError;
            } else {
                lastError = new SyntaxAnalyzerException("Parser error: Unexpected identifier. Number expected\nLine: " + lexer.positions.get(index).getLine() + "    " +
                        "Column: " + (lexer.positions.get(index).getColumn()));
                throw lastError;
            }
        }
    }

    private LeafNode checkLiteral(String value, String type) throws SyntaxAnalyzerException {
        if (lexemes.get(index).equals(findLexeme(new Lexeme(value, type)))) {
            return new LeafNode(lexemes.get(index), table.get(lexemes.get(index)).getValue(), lexer.positions.get(index++));
        } else {
            if (lastError != null && lastError.getPosition() > index) {
                throw lastError;
            } else {
                lastError = new SyntaxAnalyzerException("Parser error: Unexpected token. Waiting for \"" + value + "\"\nLine: " + lexer.positions.get(index).getLine() +
                        "    Column: " + (lexer.positions.get(index).getColumn()), index);
                throw lastError;
            }
        }
    }

    public Integer findLexeme(Lexeme lexeme) {
        return lexer.getLexemesCodes().get(lexeme);
    }
}

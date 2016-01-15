import lexeme.Lexeme;
import lexeme.LexemeAnalyzer;
import org.testng.annotations.Test;
import semantic.SemanticAnalyzer;
import syntax.SyntaxAnalyzer;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pyrozhok on 20.12.2015.
 */
public class LexemeAnalyzerTests {

    @Test
    public void lexemeAnalyzerTest() throws Exception {
        String programCode = "";
        Path inputFile = Paths.get("C:/Users/Pyrozhok/IdeaProjects/code_analyzer/src/main/resources/test2.signal");
        for (String line : Files.readAllLines(inputFile)) {
            programCode += line + "\n";
        }
        System.out.println(programCode);
        LexemeAnalyzer lexemeAnalyzer = new LexemeAnalyzer();
        HashMap<Lexeme, Integer> lexemeCodes = lexemeAnalyzer.processCode(programCode);
        List<Integer> encodedLexemes = lexemeAnalyzer.getEncodedLexemes();

        System.out.print("Encoded lexemes: [ ");
        for (int code : encodedLexemes) {
            System.out.print(code + " ");
        }
        System.out.print("]");
        System.out.println();
        System.out.println();


        ////////////////////////////
        for (Lexeme lex : lexemeCodes.keySet()) {
            System.out.println("[" + lex.getValue() + " : " + lex.getType() + " : " + lexemeCodes.get(lex) + "]");
        }


        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(lexemeAnalyzer);
        System.out.println("\nTree:\n");
        System.out.println(syntaxAnalyzer.parse().toString());

        PrintWriter writer = new PrintWriter("graph.dot", "UTF-8");
        writer.println("digraph g {");
        writer.print(syntaxAnalyzer.getTree().toDot());
        writer.println("}");
        writer.close();
        Runtime.getRuntime().exec("C:\\Program Files (x86)\\Graphviz2.38\\bin\\dot.exe -Tpng -o graph.png graph.dot");
        System.out.println("\nTo see dot graph, open file \"graph.png\" :)");




        ///////////////////
        SemanticAnalyzer checker = new SemanticAnalyzer(syntaxAnalyzer);
        List<Exception> errors = checker.check();
        for (Exception err : errors) {
            System.out.println(err.getMessage());
        }
        System.out.println("\nErrors: " + errors.size());
    }
}

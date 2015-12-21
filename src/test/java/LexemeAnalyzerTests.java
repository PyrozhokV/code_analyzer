import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
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
        Path valid = Paths.get("C:\\Users\\Pyrozhok\\IdeaProjects\\code_analyzer\\src\\main\\resources\\validInput.signal");
        Path test = Paths.get("C:\\Users\\Pyrozhok\\IdeaProjects\\code_analyzer\\src\\main\\resources\\test.signal");
        for (String line : Files.readAllLines(test)) {
            programCode += line + "\n";
        }
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

        for (Lexeme lex : lexemeCodes.keySet()) {
            System.out.println("[" + lex.getValue() + " : " + lex.getType() + " : " + lexemeCodes.get(lex) + "]");
        }
    }
}

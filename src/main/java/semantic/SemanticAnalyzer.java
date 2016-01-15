package semantic;

import lexeme.Position;
import syntax.InterNode;
import syntax.LeafNode;
import syntax.Node;
import syntax.SyntaxAnalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pyrozhok on 15.01.2016.
 */
public class SemanticAnalyzer {

    InterNode tree;

    public SemanticAnalyzer(SyntaxAnalyzer parser) {
        tree = parser.getTree();
    }

    public List<Exception> check() throws Exception {
        List<Exception> errs = checkConstants();
        errs.addAll(checkVars());
        return errs;
    }

    public List<Exception> checkConstants() throws Exception {
        List<Exception> result = new ArrayList<Exception>();
        List<List<Node>> decls = collectListsOfSort(tree, "ConstantDeclarations", "ConstantDeclarationList");
        if (!decls.isEmpty()) {
            List<String> usedVars = new ArrayList<String>();
            for (Node decl : decls.get(0)) {
                String val = getValue(decl, "ConstantDeclaration");
                if (usedVars.contains(val)) {
                    Position pos = getPosition(decl, "ConstantDeclaration");
                    result.add(new Exception("Semantic error: Constant \'" + val +
                            "\' have been already declared in this scope.\nLine: " + pos.getLine()
                            + "    Column: " + pos.getColumn()));
                } else {
                    usedVars.add(val);
                }
            }
        }
        return result;
    }

    public List<Exception> checkVars() throws Exception {
        List<Exception> result = new ArrayList<Exception>();
        List<List<Node>> decls = collectListsOfSort(tree, "StatementList", "Statement");
        if (!decls.isEmpty()) {
            List<String> usedVars = new ArrayList<String>();
            for (Node decl : decls.get(0)) {
                String val = getValue(decl, "VariableIdentifier");
                if (usedVars.contains(val)) {
                    Position pos = getPosition(decl, "VariableIdentifier");
                    result.add(new Exception("Semantic error: variable \'" + val +
                            "\' have been already declared in this scope.\nLine: " + pos.getLine()
                            + "    Column: " + pos.getColumn()));
                } else {
                    usedVars.add(val);
                }
            }
        }
        return result;
    }

    private Position getPosition(Node node) {
        if (node instanceof InterNode) {
            return getPosition(((InterNode) node).getChildren().get(0));
        }
        return ((LeafNode) node).getPos();
    }

    private Position getPosition(Node node, String sort) throws Exception {
        if (node instanceof InterNode) {
            if (((InterNode) node).getSort().equals(sort)) {
                return getPosition(((InterNode) node).getChildren().get(0));
            } else {
                for (Node child : ((InterNode) node).getChildren()) {
                    Position result = getPosition(child, sort);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    private String getValue(Node node) {
        if (node instanceof InterNode) {
            return getValue(((InterNode) node).getChildren().get(0));
        }
        return ((LeafNode) node).getStringValue();
    }

    private String getValue(Node node, String sort) throws Exception {
        if (node instanceof InterNode) {
            if (((InterNode) node).getSort().equals(sort)) {
                return getValue(((InterNode) node).getChildren().get(0));
            } else {
                for (Node child : ((InterNode) node).getChildren()) {
                    String result = getValue(child, sort);
                    if (result != null) {
                        return result;
                    }
                }
            }

        }
        return null;
    }

    public List<List<Node>> collectListsOfSort(Node tree, String sort, String elementSort) {
        if (tree instanceof InterNode) {
            if (((InterNode) tree).getSort().equals(sort)) {
                List<List<Node>> result = new ArrayList<List<Node>>();
                result.add(flattenList((InterNode)tree));
                return result;

            }
            if (!((InterNode) tree).getChildren().isEmpty()) {
                List<List<Node>> result = new ArrayList<List<Node>>();
                for (int i=0; i<((InterNode) tree).getChildren().size(); i++) {
                    for (List<Node> list : collectListsOfSort(((InterNode) tree).getChildren().get(i), sort, elementSort)) {
                        result.add(list);
                    }
                    if (i > 0 && ((InterNode) tree).getChildren().get(i-1) instanceof InterNode) {
                        if (((InterNode) tree).getChildren().size() > 0 &&
                                ((InterNode) ((InterNode) tree).getChildren().get(i-1)).getSort().equals(elementSort)) {
                            result.get(result.size() - 1).add(0, ((InterNode) tree).getChildren().get(i - 1));
                        }
                    }
                }
                return result;
            }
        }
        return new ArrayList<List<Node>>();
    }

    public List<Node> flattenList(InterNode attrList) {
        List<Node> result = new ArrayList<Node>();
        while (!attrList.getChildren().isEmpty()) {
            result.add(attrList.getChildren().get(attrList.getChildren().size() - 2));
            attrList = (InterNode)attrList.getChildren().get(attrList.getChildren().size() - 1);
        }
        return result;
    }
}

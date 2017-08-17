import com.google.common.collect.ImmutableList;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

/**
 * Created by jiatao.tao on 2017/6/21.
 */
public class CalciteParser {
    public static String replaceComputedColumn(String inputSql, Map<String, String> computedColumn)
            throws SqlParseException {
        if (inputSql == null || inputSql.isEmpty() || computedColumn == null) {
            return "";
        }

        String result = inputSql;
        String[] lines = inputSql.split("\n");
        List<Pair<String, String>> toBeReplacedExp = new ArrayList<>();
        for (String ccExp : computedColumn.keySet()) {
            List<SqlNode> matchedNodes = getMathcedNodes(inputSql, computedColumn.get(ccExp));
            for (SqlNode node : matchedNodes) {
                Pair<Integer, Integer> startEndPos = getReplacePos(lines, node);
                int start = startEndPos.getLeft();
                int end = startEndPos.getRight();
                //add table alias like t1.column,if exists alias
                String alias = getTableAlias(node);
                toBeReplacedExp.add(Pair.of(alias, inputSql.substring(start, end)));
            }
            System.out.println(
                    "For sql:" + inputSql + " Computed column: " + ccExp + "'s matched list:" + toBeReplacedExp);
            //replace user's input sql
            for (Pair<String, String> toBeReplaced : toBeReplacedExp) {
                result = result.replace(toBeReplaced.getRight(), toBeReplaced.getLeft() + ccExp);
            }
        }
        return result;
    }

    public static void main(String[] args) {

        //        String a = "   a)) + (a+b)+((( ((b +c";
        //        System.out.println(bracketsCompletion(a));
        //        System.out.println(bracketsCompletion(""));
        //        System.out.println(bracketsCompletion(null));
        //        System.out.println(parse2(a));
//        bracketsCompletion("select (   a + b) + c from t", 11, 21);
//        getPosWithBracketsCompletion("select (a + b) + c from t", 8, 18);
    }

//    private static String getPosWithBracketsCompletion(String str, int left, int right) {
//        if (str == null) {
//            return "";
//        }
//        int leftBracketNum = 0;
//        int rightBracketNum = 0;
//        String substring = str.substring(left, right);
//        for (int i = 0; i < substring.length(); i++) {
//            String temp = substring.charAt(i) + "";
//            if (temp.equals("(")) {
//                leftBracketNum++;
//            }
//            if (temp.equals(")")) {
//                rightBracketNum++;
//                if (leftBracketNum < rightBracketNum) {
//                    while (' ' == str.charAt(left - 1)) {
//                        left--;
//                    }
//                    left--;
//                    leftBracketNum++;
//                }
//            }
//        }
//        while (rightBracketNum < leftBracketNum) {
//            while (' ' == str.charAt(right + 1)) {
//                right++;
//            }
//            right++;
//            rightBracketNum++;
//        }
//
//    }

    private static String parenthesisPatch(String str) {
        Stack<String> exp = new Stack<String>();
        for (int i = 0; i < str.length(); i++) {
            String temp = str.charAt(i) + "";
            String tempExp;
            if (temp.equals(")")) {
                String val2 = exp.pop();
                String op = exp.pop();
                String val1 = exp.pop();
                tempExp = "(" + val1 + op + val2 + ")";
                exp.push(tempExp);
            } else {
                exp.push(temp);
            }
        }
        return exp.pop();
    }

    private static Pair<Integer, Integer> getReplacePos(String[] lines, SqlNode node) {
        SqlParserPos pos = node.getParserPosition();
        int lineStart = pos.getLineNum();
        int columnStart = pos.getColumnNum() - 1;
        int columnEnd = pos.getEndColumnNum();
        //for the case that sql is multi lines
        for (int i = 0; i < lineStart - 1; i++) {
            int offset = lines[i].length();
            columnStart += offset + 1;
            columnEnd += offset + 1;
        }
        return Pair.of(columnStart, columnEnd);
    }

    public static Pair<Integer, Integer> getReplacePos(SqlNode node, String inputSql) {
        if (inputSql == null) {
            return Pair.of(0, 0);
        }
        String[] lines = inputSql.split("\n");
        SqlParserPos pos = node.getParserPosition();
        int lineStart = pos.getLineNum();
        int lineEnd = pos.getEndLineNum();
        int columnStart = pos.getColumnNum() - 1;
        int columnEnd = pos.getEndColumnNum();
        //for the case that sql is multi lines
        for (int i = 0; i < lineStart - 1; i++) {
            columnStart += lines[i].length() + 1;
        }
        for (int i = 0; i < lineEnd - 1; i++) {
            columnEnd += lines[i].length() + 1;
        }
        //for calcite's bug CALCITE-1875
        Pair<Integer, Integer> startEndPos = getPosWithBracketsCompletion(inputSql, columnStart, columnEnd);
        return startEndPos;
    }

    private static Pair<Integer, Integer> getPosWithBracketsCompletion(String inputSql, int left, int right) {
        int leftBracketNum = 0;
        int rightBracketNum = 0;
        String substring = inputSql.substring(left, right);
        for (int i = 0; i < substring.length(); i++) {
            char temp = substring.charAt(i);
            if (temp == '(') {
                leftBracketNum++;
            }
            if (temp == ')') {
                rightBracketNum++;
                if (leftBracketNum < rightBracketNum) {
                    while ('(' != inputSql.charAt(left - 1)) {
                        left--;
                    }
                    left--;
                    leftBracketNum++;
                }
            }
        }
        while (rightBracketNum < leftBracketNum) {
            while (')' != inputSql.charAt(right)) {
                right++;
            }
            right++;
            rightBracketNum++;
        }
        return Pair.of(left, right);
    }

    //Return matched node's position and it's alias(if exists).iF can not find match, return an empty capacity list
    private static List<SqlNode> getMathcedNodes(String inputSql, String ccExp) throws SqlParseException {
        if (ccExp == null || ccExp.equals("")) {
            return new ArrayList<>();
        }
        ArrayList<SqlNode> toBeReplacedNodes = new ArrayList<>();
        SqlNode ccNode = getCCExpNode(ccExp);
        List<SqlNode> inputNodes = getInputTreeNodes(inputSql);

        // find whether user input sql's tree node equals computed columns's define expression
        for (SqlNode inputNode : inputNodes) {
            if (isNodeEqual(inputNode, ccNode)) {
                toBeReplacedNodes.add(inputNode);
            }
        }
        return toBeReplacedNodes;
    }

    private static List<SqlNode> getInputTreeNodes(String inputSql) throws SqlParseException {
        SqlTreeVisitor stv = new SqlTreeVisitor();
        parse(inputSql).accept(stv);
        return stv.getSqlNodes();
    }

    private static SqlNode getCCExpNode(String ccExp) throws SqlParseException {
        ccExp = "select " + ccExp + " from t";
        return ((SqlSelect) parse(ccExp)).getSelectList().get(0);
    }

    static SqlNode parse(String sql) throws SqlParseException {
        SqlParser.ConfigBuilder paserBuilder = SqlParser.configBuilder();
        SqlParser sqlParser = SqlParser.create(sql, paserBuilder.build());
        return sqlParser.parseQuery();
    }

    static boolean isNodeEqual(SqlNode node0, SqlNode node1) {
        if (node0 == null) {
            return node1 == null;
        } else if (node1 == null) {
            return false;
        }

        if (!Objects.equals(node0.getClass().getSimpleName(), node1.getClass().getSimpleName())) {
            return false;
        }

        if (node0 instanceof SqlCall) {
            SqlCall thisNode = (SqlCall) node0;
            SqlCall thatNode = (SqlCall) node1;
            if (!thisNode.getOperator().getName().equalsIgnoreCase(thatNode.getOperator().getName())) {
                return false;
            }
            return isNodeEqual(thisNode.getOperandList(), thatNode.getOperandList());
        }
        if (node0 instanceof SqlLiteral) {
            SqlLiteral thisNode = (SqlLiteral) node0;
            SqlLiteral thatNode = (SqlLiteral) node1;
            return Objects.equals(thisNode.getValue(), thatNode.getValue());
        }
        if (node0 instanceof SqlNodeList) {
            SqlNodeList thisNode = (SqlNodeList) node0;
            SqlNodeList thatNode = (SqlNodeList) node1;
            if (thisNode.getList().size() != thatNode.getList().size()) {
                return false;
            }
            for (int i = 0; i < thisNode.getList().size(); i++) {
                SqlNode thisChild = thisNode.getList().get(i);
                final SqlNode thatChild = thatNode.getList().get(i);
                if (!isNodeEqual(thisChild, thatChild)) {
                    return false;
                }
            }
            return true;
        }
        if (node0 instanceof SqlIdentifier) {
            SqlIdentifier thisNode = (SqlIdentifier) node0;
            SqlIdentifier thatNode = (SqlIdentifier) node1;
            // compare ignore table alias.eg: expression like "a.b + a.c + a.d" ,alias a will be ignored when compared
            String name0 = thisNode.names.get(thisNode.names.size() - 1);
            String name1 = thatNode.names.get(thatNode.names.size() - 1);
            return name0.equals(name1);
        }

        System.out.println("unknow instance type");//log
        return false;
    }

    private static boolean isNodeEqual(List<SqlNode> operands0, List<SqlNode> operands1) {
        if (operands0.size() != operands1.size()) {
            return false;
        }
        for (int i = 0; i < operands0.size(); i++) {
            if (!isNodeEqual(operands0.get(i), operands1.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static String getTableAlias(SqlNode node) {
        if (node instanceof SqlCall) {
            SqlCall call = (SqlCall) node;
            return getTableAlias(call.getOperandList());
        }
        if (node instanceof SqlIdentifier) {
            StringBuilder alias = new StringBuilder("");
            ImmutableList<String> names = ((SqlIdentifier) node).names;
            if (names.size() >= 2) {
                for (int i = 0; i < names.size() - 1; i++) {
                    alias.append(names.get(i)).append(".");
                }
            }
            return alias.toString();
        }
        if (node instanceof SqlNodeList) {
            return "";
        }
        if (node instanceof SqlLiteral) {
            return "";
        }
        return "";
    }

    private static String getTableAlias(List<SqlNode> operands) {
        for (SqlNode operand : operands) {
            return getTableAlias(operand);
        }
        return "";
    }

    //-----------------------------------------------------------//
    public static String getLastNthName(SqlIdentifier id, int n) {
        //n = 1 is getting column
        //n = 2 is getting table's alias, if has.
        //n = 3 is getting database name, if has.
        return id.names.get(id.names.size() - n).replace("\"", "").toUpperCase();
    }

}

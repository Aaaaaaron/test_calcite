import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jiatao.tao on 2017/6/21.
 */
public class Main0 {
    public static void main(String[] args) throws SqlParseException {
        String sql = "SELECT a \n" + "FROM a.KYLIN_SALES as KYLIN_SALES\n"
                + "INNER JOIN \"A\".KYLIN_ACCOUNT as BUYER_ACCOUNT\n"
                + "ON KYLIN_SALES.BUYER_ID = BUYER_ACCOUNT.ACCOUNT_ID\n" + "INNER JOIN \"KYLIN_COUNTRY\" as BUYER_COUNTRY\n"
                + "ON BUYER_ACCOUNT.ACCOUNT_COUNTRY = BUYER_COUNTRY.COUNTRY";
        String sql2 = "select * from a.t,tt limit 100";
        System.out.println(sql2.split("limit")[0]);
        String sql3 = "select * from a.t,a.tt,ttt";
//        System.out.println(schemaCompletion(sql, getSchema()));
//        System.out.println();
        System.out.println(schemaCompletion(sql2, getSchema()));
        System.out.println();
        System.out.println(schemaCompletion(sql3, getSchema()));
        //        TableWithAliasVisitor ftv1 = new TableWithAliasVisitor();
        //        node.accept(ftv1);
        //        List<SqlNode> tables = ftv1.getTablesWithouSchema();
        //
        //        TableWithAliasVisitor ftv2 = new TableWithAliasVisitor();
        //        node2.accept(ftv2);
        //        List<SqlNode> tables2 = ftv2.getTablesWithouSchema();
        //
        //        TableWithAliasVisitor ftv3 = new TableWithAliasVisitor();
        //        node3.accept(ftv3);
        //        List<SqlNode> tables3 = ftv3.getTablesWithouSchema();
        System.out.println();


    }

    static String getSchema() {
        return "edw";
    }

    public static String schemaCompletion(String inputSql, String schema) throws SqlParseException {
        StrBuilder afterConvert = new StrBuilder(inputSql);
        SqlSelect node = (SqlSelect) CalciteParser.parse(inputSql);
        List<Pair<Integer, Integer>> posList = getSortedTablePosList(inputSql, node.getFrom());
        for (Pair<Integer, Integer> pos : posList) {
            String tableWithSchema = schema + "." + inputSql.substring(pos.getLeft(), pos.getRight());
            afterConvert.replace(pos.getLeft(), pos.getRight(), tableWithSchema);
        }
        return afterConvert.toString();
    }

    public static List<Pair<Integer, Integer>> getSortedTablePosList(String inputSql, SqlNode node)
            throws SqlParseException {
        List<Pair<Integer, Integer>> tablesWithouSchemaPosList = new ArrayList<>();
        String[] lines = inputSql.split("\n");
        FromTableVisitor ftv = new FromTableVisitor();
        node.accept(ftv);
        List<SqlNode> tablesWithouSchema = ftv.getTablesWithoutSchema();
        for (SqlNode sqlNode : tablesWithouSchema) {
            SqlParserPos pos = sqlNode.getParserPosition();
            int lineStart = pos.getLineNum();
            int lineEnd = pos.getEndLineNum();
            int columnStart = pos.getColumnNum() - 1;
            int columnEnd = pos.getEndColumnNum();
            for (int i = 0; i < lineStart - 1; i++) {
                columnStart += lines[i].length() + 1;
            }
            for (int i = 0; i < lineEnd - 1; i++) {
                columnEnd += lines[i].length() + 1;
            }
            tablesWithouSchemaPosList.add(Pair.of(columnStart, columnEnd));
        }
        Collections.sort(tablesWithouSchemaPosList);
        Collections.reverse(tablesWithouSchemaPosList);
        return tablesWithouSchemaPosList;
    }
}

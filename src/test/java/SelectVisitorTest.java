import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectVisitorTest {
    @Test
    public void test() throws SqlParseException {
        String sql = "select a, (select count(*) from DB.aa) from ttt join (select a,b from (select * from DB.t), (select * from DB.bb)), tt where c in (select * from tt) and d > 10";
        SqlNode parse1 = CalciteParser.parse(sql);

        String sql2 = "select a from ()";
        // first get all select clause
        SelectVisitor sv = new SelectVisitor();
        parse1.accept(sv);
        List<SqlNode> selectHasTable = sv.getSelectHasTable();
        Map<SqlNode, List<SqlNode>> result = new HashMap<>();
        for (SqlNode node : selectHasTable) {
            SqlSelect select = (SqlSelect) node;
            FromTableVisitor2 ftv = new FromTableVisitor2();
            select.getFrom().accept(ftv);
            List<SqlNode> tables = ftv.getTablesWithoutSchema();
            if (tables.size() > 0) {
                result.put(node, tables);
            }
        }

        // then get all select clause's table without subquery
        System.out.println();
    }
}

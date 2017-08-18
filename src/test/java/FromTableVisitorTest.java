import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.junit.Test;

import java.util.List;

public class FromTableVisitorTest {
    @Test
    public void test() throws SqlParseException {
        String sql = "select a as a1 from ttt join (select * from (select * from t)), tt where c in (select * from tt) and d > 10";
        String sql1 = "select a, a from t as t1, tt";
        String sql2 = "SELECT a as aa,(SELECT COUNT(*) FROM t t1) AS SalesAmount FROM p p1";
        SqlNode parse = CalciteParser.parse(sql);
        SqlNode parse1 = CalciteParser.parse(sql1);
        SqlNode parse2 = CalciteParser.parse(sql2);

//        FromVisitor fv = new FromVisitor();
//        parse3.accept(fv);
//        List<SqlNode> sqlNodes = fv.getSqlNodes();
        FromTableVisitor fv = new FromTableVisitor();
        parse.accept(fv);
        List<SqlNode> tablesWithoutSchema = fv.getTablesWithoutSchema();
        System.out.println();
    }
}

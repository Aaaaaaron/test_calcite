import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws SqlParseException {
        String sql = "select DB.t1.a, \"t2\".a, t3.a, a from DB1.t t1, tt t2, ttt t3, DB2.tttt, ttttt";
        String sql1 = "select a, a from t, tt";
        String sql2 = "select a, a from t, tt order by a";
        String sql3 = "select a, b from ttt join (select * from (select * from t)), tt where c in (select * from tt) and d > 10";
        SqlNode parse1 = CalciteParser.parse(sql1);
        SqlNode parse2 = CalciteParser.parse(sql2);
        SqlNode parse3 = CalciteParser.parse(sql3);

        FromVisitor fv = new FromVisitor();
        parse3.accept(fv);
        List<SqlNode> sqlNodes = fv.getSqlNodes();

        SelectVisitor sv = new SelectVisitor();
        parse3.accept(sv);
        List<Pair<SqlNode, SqlNode>> selectHasTable = sv.getSelectHasTable();

//        Set<String> allSQLTblAndCol = getAllSQLTblAndCol(sql3);

        System.out.println();
    }

    private static Set<String> getAllSQLTblAndCol(String sql) throws SqlParseException {
        SqlNode parse = CalciteParser.parse(sql);
        TblsColsVisitor allTblsColsVisitor = new TblsColsVisitor(parse);
        parse.accept(allTblsColsVisitor);
        return allTblsColsVisitor.getTablesAndColumns();
    }
}

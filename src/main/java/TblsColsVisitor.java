import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.util.SqlVisitor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by jiatao.tao on 2017/6/19.
 */
public class TblsColsVisitor implements SqlVisitor<SqlNode> {
    //all columns and tables
    private Set<String> tblsCols = new HashSet<>();

    //all alias:table, visitor need to know the relation between table and alias
    private Map<String, String> tableWithAlias;

    public TblsColsVisitor(SqlNode node) {
        TableWithAliasVisitor tav = new TableWithAliasVisitor();
        node.accept(tav);
        tableWithAlias = tav.getTableWithAlias();
    }

    // if column is whit alias, will use table instead alias
    public Set<String> getTablesAndColumns() {
        return tblsCols;
    }

    @Override
    public SqlNode visit(SqlNodeList nodeList) {
        for (int i = 0; i < nodeList.size(); i++) {
            SqlNode node = nodeList.get(i);
            node.accept(this);
        }
        return null;
    }

    @Override
    public SqlNode visit(SqlCall call) {
        for (SqlNode operand : call.getOperandList()) {
            if (operand != null) {
                operand.accept(this);
            }
        }
        return null;
    }

    @Override
    public SqlNode visit(SqlIdentifier id) {
        String identifier = CalciteParser.getLastNthName(id, 1);

        if (id.names.size() >= 2) {
            String alias = CalciteParser.getLastNthName(id, 2).toUpperCase();
            String table = tableWithAlias.get(alias);
            if (table != null) {
                // add column like 'TABLE.COLUMN'
                identifier = table + "." + identifier;
            }
        }

        for (String alias : tableWithAlias.keySet()) {
            //if identifier is table alias, skip.
            if (alias.equals(identifier)) {
                return null;
            }
        }
        tblsCols.add(identifier);
        return null;
    }

    @Override
    public SqlNode visit(SqlLiteral literal) {
        return null;
    }

    @Override
    public SqlNode visit(SqlDataTypeSpec type) {
        return null;
    }

    @Override
    public SqlNode visit(SqlDynamicParam param) {
        return null;
    }

    @Override
    public SqlNode visit(SqlIntervalQualifier intervalQualifier) {
        return null;
    }


}

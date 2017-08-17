import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.util.SqlVisitor;

import java.util.HashMap;
import java.util.Map;

class TableWithAliasVisitor implements SqlVisitor<SqlNode> {
    private Map<String, String> tableWithAlias;

    public TableWithAliasVisitor() {
        tableWithAlias = new HashMap<>();
    }

    Map<String, String> getTableWithAlias() {
        return tableWithAlias;
    }

    @Override
    public SqlNode visit(SqlNodeList nodeList) {
        return null;
    }

    @Override
    public SqlNode visit(SqlLiteral literal) {
        return null;
    }

    @Override
    public SqlNode visit(SqlCall call) {
        if (call instanceof SqlBasicCall) {
            SqlIdentifier id0 = (SqlIdentifier) ((SqlBasicCall) call).getOperands()[0];
            SqlIdentifier id1 = (SqlIdentifier) ((SqlBasicCall) call).getOperands()[1];
            String table = CalciteParser.getLastNthName(id0, 1);
            String alais = CalciteParser.getLastNthName(id1, 1);
            tableWithAlias.put(alais, table);
            return null;
        }
        if (call instanceof SqlJoin) {
            SqlJoin node = (SqlJoin) call;
            node.getLeft().accept(this);
            node.getRight().accept(this);
            return null;
        }
        for (SqlNode operand : call.getOperandList()) {
            if (operand != null) {
                operand.accept(this);
            }
        }
        return null;
    }

    @Override
    public SqlNode visit(SqlIdentifier id) {
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
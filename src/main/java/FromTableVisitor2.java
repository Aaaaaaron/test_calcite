import org.apache.calcite.sql.SqlAsOperator;
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
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.util.SqlVisitor;

import java.util.ArrayList;
import java.util.List;

public class FromTableVisitor2 implements SqlVisitor<SqlNode> {
    private List<SqlNode> tables;

    FromTableVisitor2() {
        this.tables = new ArrayList<>();
    }

    List<SqlNode> getTablesWithoutSchema() {
        return tables;
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
        if (call instanceof SqlSelect) {
            return null;
        }
        if (call instanceof SqlBasicCall) {
            SqlBasicCall node = (SqlBasicCall) call;
            if (node.getOperator() instanceof SqlAsOperator) {
                node.getOperands()[0].accept(this);
            }
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
        tables.add(id);
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

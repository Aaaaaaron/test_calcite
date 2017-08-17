import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.util.SqlVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiatao.tao on 2017/6/19.
 */
public class FromVisitor implements SqlVisitor<SqlNode> {
    //get all from clause, not all tables.
    List<SqlNode> fromClause;

    public FromVisitor() {
        this.fromClause = new ArrayList<>();
    }

    public List<SqlNode> getSqlNodes() {
        return fromClause;
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
    public SqlNode visit(SqlLiteral literal) {
        return null;
    }

    @Override
    public SqlNode visit(SqlCall call) {
        if (call instanceof SqlSelect) {
            SqlSelect select = (SqlSelect) call;
            fromClause.add(select.getFrom());
            select.getFrom().accept(this);
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

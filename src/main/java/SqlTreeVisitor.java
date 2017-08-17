import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.util.SqlVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiatao.tao on 2017/6/19.
 */
public class SqlTreeVisitor implements SqlVisitor<SqlNode> {
    List<SqlNode> sqlNodes;

    public SqlTreeVisitor() {
        this.sqlNodes = new ArrayList<>();
    }

    public List<SqlNode> getSqlNodes() {
        return sqlNodes;
    }

    @Override
    public SqlNode visit(SqlNodeList nodeList) {
        sqlNodes.add(nodeList);
        for (int i = 0; i < nodeList.size(); i++) {
            SqlNode node = nodeList.get(i);
            node.accept(this);
        }
        return null;
    }

    @Override
    public SqlNode visit(SqlLiteral literal) {
        sqlNodes.add(literal);
        return null;
    }

    @Override
    public SqlNode visit(SqlCall call) {
        sqlNodes.add(call);
        for (SqlNode operand : call.getOperandList()) {
            if (operand != null) {
                operand.accept(this);
            }
        }
        return null;
    }

    @Override
    public SqlNode visit(SqlIdentifier id) {
        sqlNodes.add(id);
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

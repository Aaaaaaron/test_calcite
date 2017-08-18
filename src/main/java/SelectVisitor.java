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
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/*
select a, b from t, (select * from tt).要遍历拿到 t和相应的 select, 但是不能拿到子查询里面的 tt,
首先处理最外面的 select 判断它是否有想要的 table 有就加到 map<table, select>

然后再getFrom最外面的 sql, 拿到这个 sql 所有 from 出现的地方,

上面说的 根本不用

FromTableVisitor 里面的 visit(SqlCall call) 就可以 handle 整个 sql 的状况.

上面说的也不对 有 bug 的

要给定一个 table 再利用这个 visitor, 针对给定的 table, 每当遇到 select 先入栈, 然后找id == table 找到了出栈, 因为一个 select 不会 from 一个相同的表两次.
 */
public class SelectVisitor implements SqlVisitor<SqlNode> {
    private List<Pair<SqlNode, SqlNode>> tableWithSelect;
    private Stack<SqlNode> selects = new Stack<>();

    SelectVisitor() {
        this.tableWithSelect = new ArrayList<>();
    }

    List<Pair<SqlNode, SqlNode>> getSelectHasTable() {
        return tableWithSelect;
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

        if (call instanceof SqlBasicCall) {
            SqlBasicCall node = (SqlBasicCall) call;
            return node.getOperands()[0].accept(this);
        }
        if (call instanceof SqlJoin) {
            SqlJoin node = (SqlJoin) call;
            node.getLeft().accept(this);
            node.getRight().accept(this);
            return null;
        }
        if (call instanceof SqlSelect) {
            selects.add(call);
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
        tableWithSelect.add(Pair.<SqlNode, SqlNode>of(id, selects.pop()));
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

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

/*
select a, b from t, (select * from tt).要遍历拿到 t和相应的 select, 但是不能拿到子查询里面的 tt,
首先处理最外面的 select 判断它是否有想要的 table 有就加到 map<table, select>

然后再getFrom最外面的 sql, 拿到这个 sql 所有 from 出现的地方,

上面说的 根本不用

FromTableVisitor 里面的 visit(SqlCall call) 就可以 handle 整个 sql 的状况.

上面说的也不对 有 bug 的

要给定一个 table 再利用这个 visitor, 针对给定的 table, 每当遇到 select 先入栈, 然后找id == table 找到了出栈, 因为一个 select 不会 from 一个相同的表两次.

拿到所有 table 对应的select node,

"select a, b from ttt join (select * from (select * from t)), tt where c in (select * from tt) and d > 10"

ttt:select a, b from ttt join (select * from (select * from t)), tt where c in (select * from tt) and d > 10
tt:select a, b from ttt join (select * from (select * from t)), tt where c in (select * from tt) and d > 10
t:select * from t
tt:select * from tt
*/
public class SelectVisitor implements SqlVisitor<SqlNode> {
    private List<SqlNode> selects;

    SelectVisitor() {
        this.selects = new ArrayList<>();
    }

    List<SqlNode> getSelectHasTable() {
        return selects;
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
//            SqlSelect select = (SqlSelect) call;
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

import java.util.ArrayList;
import java.util.List;

/*
this is only used for select *, the other situation will be intercepted
*/
public class ColumnPostFilter {
    public static void main(String[] args) {
        List<String> columns = new ArrayList<>();
        List<String> columnsBlackList = new ArrayList<>();
        columns.add("a");
        columns.add("b");
        columns.add("c");
        columns.add("d");
        columns.add("e");
        columns.add("f");
        columns.add("g");

        int columnSize = columns.size();

        columnsBlackList.add("b");
        columnsBlackList.add("d");

    }
}

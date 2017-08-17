package old;

import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main1 {
    public static void main(String[] args) throws SqlParseException {
        StringBuilder sql = new StringBuilder("12ttt678ttt1ttt");
        List<Pair<Integer, Integer>> tablePosList = new ArrayList<>();
        tablePosList.add(Pair.of(10, 12));
        tablePosList.add(Pair.of(2, 5));
        tablePosList.add(Pair.of(8, 9));
        System.out.println(sql.substring(10, 12));
        System.out.println(sql.substring(2, 5));
        System.out.println(sql.substring(8, 9));
        Collections.sort(tablePosList);
        Collections.reverse(tablePosList);
        for (Pair<Integer, Integer> pos : tablePosList) {
            sql.replace(pos.getLeft(), pos.getRight(), "ttt");
        }
        System.out.println(sql.toString());
        System.out.println(tablePosList);
    }
}

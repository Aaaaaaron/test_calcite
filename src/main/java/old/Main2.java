package old;

import org.apache.calcite.sql.parser.SqlParseException;

public class Main2 {
    public static void main(String[] args) throws SqlParseException {

        String sql = "select (\"DB\".\"t1\" . \"a\" + DB.t1.b + DB.t1.c) as c, substring(substring(lstg_format_name,1,3),1,3) as d from table1 as t1 group by t1.a+   t1.b +     t1.c having t1.a+t1.b+t1.c > 100 order by t1.a +t1.b +t1.c";
//        String a = "a + b    +c";
//        String b = "  a + b+ c";
//        System.out.println(isTwoCCDefinitionEquals(a, b));
    }

    private static boolean isTwoCCDefinitionEquals(String definition0, String definition1) {
        definition0 = definition0.replaceAll("\\s*", "");
        System.out.println(definition0);
        definition1 = definition1.replaceAll("\\s*", "");
        System.out.println(definition1);
        return definition0.equals(definition1);
    }
}

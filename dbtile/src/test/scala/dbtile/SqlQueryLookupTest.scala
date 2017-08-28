package dbtile

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import java.io.File

class SqlQueryLookupTest extends FunSuite {
    val sqlLookup = new SqlQueryLookup(new File("/Users/marcin/projects/scala/db-tile/dbtile/src/test/resources/sql/"))

    test ("should return raw contents of SQL template") {
        val sql = sqlLookup.get("default").get

        assert (sql == "SELECT MAX(ROUND(latitude, {{round}})) AS latitude, MAX(ROUND(longitude, {{round}})) AS longitude FROM {{table_name}} GROUP BY CONCAT(ROUND(latitude, {{round}}), ROUND(longitude, {{round}}))")
    }

    test ("should replace tokens define in tokens map") {
        val tokens = Map(
            "round" -> "2",
            "table_name" -> "my_table"
        )
        val sql = sqlLookup.getWithReplace("default", tokens).get

        assert (sql == "SELECT MAX(ROUND(latitude, 2)) AS latitude, MAX(ROUND(longitude, 2)) AS longitude FROM my_table GROUP BY CONCAT(ROUND(latitude, 2), ROUND(longitude, 2))")
    }

    test ("should replace all tokens in SQL template") (pending)
}

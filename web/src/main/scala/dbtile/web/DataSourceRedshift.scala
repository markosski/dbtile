package dbtile.web

import java.io.File
import java.sql.DriverManager
import java.util.Properties

import dbtile._

import scala.collection.mutable

/**
  * Created by marcin1 on 8/15/17.
  */
class DataSourceRedshift extends DataSource {
    // Query database
    Class.forName("com.amazon.redshift.jdbc42.Driver")
    val props = new Properties()
    props.setProperty("user", Config.conf.getString("sql.user"))
    props.setProperty("password", Config.conf.getString("sql.pass"))

    val conn = DriverManager.getConnection(s"jdbc:redshift://${Config.conf.getString("sql.host")}:${Config.conf.getString("sql.port")}/", props)
    val sqlLookup = new SqlQueryLookup(new File(Config.conf.getString("templateDir")))

    def get(bbox: BoundingBox, options: Map[String, String]): Vector[Point] = {
        val tokens: mutable.Map[String, String] = mutable.Map(
            "round" -> "1",
            "table_name" -> "audi.scores_composite_sample",
            "SWlon" -> bbox.SW.lon.toString,
            "SWlat" -> bbox.SW.lat.toString,
            "NElon" -> bbox.NE.lon.toString,
            "NElat" -> bbox.NE.lat.toString
        )

        val nameParts = "default".split("__")
        var sqlTemplateName = nameParts(0)
        val zoom = nameParts(1).toInt

        if (zoom <= 3) {
            tokens.put("round", "1")
        } else if (zoom < 6) {
            tokens.put("round", "2")
        } else if (zoom < 9) {
            tokens.put("round", "3")
        } else if (zoom < 16) {
            tokens.put("round", "4")
        } else {
            sqlTemplateName = "default_full"
        }

        sqlLookup.getWithReplace(sqlTemplateName, tokens.toList) match {
            case Some(sql) => {
                val stmt = conn.createStatement()
                val rs = stmt.executeQuery(sql)
                var data = List[Point]()

                while (rs.next()) {
                    data = Point(rs.getDouble("longitude"), rs.getDouble("latitude")) :: data
                }

                stmt.close
                rs.close

                data.toVector
            }
            case None => Vector(Point(0.0,0.0))
        }
    }
}

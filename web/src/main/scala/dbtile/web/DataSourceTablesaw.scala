package dbtile.web

import dbtile.{BoundingBox, DataSource, Point}
import tech.tablesaw.api.QueryHelper._
import tech.tablesaw.api.Table
import org.apache.log4j.Logger

/**
  * Example implementation of Tablesaw DataSource.
  */
class DataSourceTablesaw extends DataSource {
    private val logger = Logger.getLogger(getClass.getName)

    // TODO: Store it in hashmap like structure so we can switch handle many files with options.
    val allData = Table.readTable(Config.conf.getString("tablesaw.dir") + "1950-2016_all_tornadoes-small.csv.saw");

    def get(bbox: BoundingBox, options: Map[String, String]): Vector[Point] = {
        val filtered = allData.selectWhere(
                allOf(
                    column("slon").isGreaterThanOrEqualTo(bbox.SW.lon.toFloat),
                    column("slon").isLessThanOrEqualTo(bbox.NE.lon.toFloat),
                    column("elat").isGreaterThanOrEqualTo(bbox.SW.lat.toFloat),
                    column("elat").isLessThanOrEqualTo(bbox.NE.lat.toFloat)
                ))

        val newData = new Array[Point](filtered.rowCount)
        filtered.forEach(i => { newData(i) = Point(filtered.get(i, 2).toDouble, filtered.get(i, 3).toDouble)})

        logger.info(s"rows: ${filtered.rowCount}")
        newData.toVector
    }
}
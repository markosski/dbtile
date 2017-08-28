package dbtile

/**
  * Created by marcin1 on 8/17/17.
  */
class DataSourceTestData extends DataSource {
    def get(bbox: BoundingBox, options: Map[String, String]): Vector[Point] = {
      Vector[Point]()
    }
}

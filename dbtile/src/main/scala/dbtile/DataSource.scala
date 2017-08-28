package dbtile

/**
  * Created by marcin1 on 8/15/17.
  */

trait DataSource {
    def get(bbox: BoundingBox, options: Map[String, String]): Vector[Point]
}

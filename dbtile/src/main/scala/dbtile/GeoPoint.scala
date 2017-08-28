package dbtile

/**
  * Created by marcin1 on 8/15/17.
  */

case class Point(lon: Double, lat: Double)

case class BoundingBox(SW: Point, NE: Point)

object BoundingBox {
    /**
      * Get tile upper left corner.
      *
      * @param xtile
      * @param ytile
      * @param zoom
      * @return
      */
    private def tileUL(xtile: Int, ytile: Int, zoom: Int) = {
        val n = math.pow(2.0, zoom)

        val lon_deg = xtile / n * 360.0 - 180.0
        val lat_rad = math.atan(math.sinh(math.Pi * (1 - 2 * ytile / n)))
        val lat_deg = math.toDegrees(lat_rad)

        Point(lon_deg, lat_deg)
    }

    /**
      * Create BoundinBox.
      *
      * @param xtile
      * @param ytile
      * @param zoom
      * @return
      */
    def create(xtile: Int, ytile: Int, zoom: Int) = {
        val SW = tileUL(xtile, ytile, zoom)
        val NE = tileUL(xtile + 1, ytile + 1, zoom)

        BoundingBox(Point(SW.lon, NE.lat), Point(NE.lon, SW.lat))
    }
}


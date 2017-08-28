package dbtile

/**
  * Created by marcin1 on 7/13/17.
  *
  * http://www.maptiler.org/google-maps-coordinates-tile-bounds-projection/
  * http://docs.aws.amazon.com/redshift/latest/mgmt/connecting-in-code.html
  * https://stackoverflow.com/questions/14329691/covert-latitude-longitude-point-to-a-pixels-x-y-on-mercator-projection
  * https://stackoverflow.com/questions/1591902/converting-long-lat-to-pixel-x-y-given-a-zoom-level
  * http://appelsiini.net/2008/introduction-to-marker-clustering-with-google-maps/
  */
import java.awt.Color
import java.util.Properties
import scala.util.{Try, Success, Failure}


object Main extends App {
    System.setProperty("java.awt.headless", "true")

    val zoom = 4
    val col = 4
    val row = 6
    val tile = new ImageTile(col, row, zoom, new VaryingIntensityPixel(new Color(255, 0, 0, 100), 64, zoom))

    val ds = new DataSourceTestData

    tile.render(ds, Map[String, String]())
    tile.store("/Users/marcin/Downloads/test.png")
}

package dbtile

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image._
import java.io.File
import javax.imageio.ImageIO
import scala.math
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import java.net.URL
import org.apache.log4j.Logger

/**
  * Created by marcin1 on 8/15/17.
  */

case class Pixel(x: Int, y: Int)

/**
  * Pixel renderer dictates how information is encoded in the image. For example different pixel formatter
  * can be introduced to render circles instead of single pixels or add intensity control to pixel color.
  */
trait PixelFormat {
    val dim = 256

    def add(x: Int, y: Int): Unit
    def project(image: BufferedImage): Unit
}

/**
  * Default pixel formatter.
  * @param color
  */
class BasicPixel(color: Color) extends PixelFormat {
    val arr = Array.ofDim[Int](dim, dim)

    def add(x: Int, y: Int): Unit = {
        arr(x)(y) = 1
    }

    def project(image: BufferedImage) = {
        for (x <- 0 until dim; y <- 0 until dim) {
            if (arr(x)(y) == 1) image.setRGB(x, y, color.getRGB)
        }
    }
}

class VaryingIntensityPixel(color: Color, intensity: Int, zoom: Int) extends PixelFormat {
    private val logger = Logger.getLogger(getClass.getName)

    require(zoom >= 0 && zoom <= 22)  // max zoom level
    require(intensity >= 0 && intensity <= 255)

    val arr = Array.ofDim[Int](dim, dim)
    val size: Int = {
        if (zoom >= 0 && zoom <= 6) 2
        else if (zoom > 6 && zoom <= 12) 4
        else 6 }

    logger.info(s"point size: $size px")

    def add(x: Int, y: Int): Unit = {
        addNeighbors(x, y)
    }

    def addNeighbors(x: Int, y: Int) = {
        val x0 = x/size
        val y0 = y/size

        for (x1 <- math.max(0, x - size) to math.min(dim - 1, x + size);
             y1 <- math.max(0, y - size) to math.min(dim - 1, y + size)) {

            if (x1/size == x0 && y1/size == y0) arr(x1)(y1) += 1
        }
    }

    def project(image: BufferedImage) = {
        for (x <- 0 until dim; y <- 0 until dim) {
            if (arr(x)(y) > 0) {
                val newAlpha: Int = math.min(
                    math.log(arr(x)(y)) * intensity + color.getAlpha * zoom,
                    255
                ).toInt

                val newColor = new Color(color.getRed, color.getBlue, color.getGreen, newAlpha)
                image.setRGB(x, y, newColor.getRGB)
            }
        }
    }
}

/**
  *
  * @param x
  * @param y
  * @param zoom
  * @param size
  * @param pixelFormat
  */
class ImageTile(x: Int, y: Int, zoom: Int, pixelFormat: PixelFormat) {
    private val dim = 256
    private val image = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB)
    private val bbox: BoundingBox = BoundingBox.create(x, y, zoom)

    /**
      * Used internally to set pixel on the image.
      * @param p
      */
    private def setPixel(p: Point): Unit = {
        if (p.lon > bbox.SW.lon && p.lon < bbox.NE.lon
                && p.lat > bbox.SW.lat && p.lat < bbox.NE.lat) {

            val offset = (dim << (zoom - 1)).toFloat

            val pixel = Pixel(
                math.floor(offset + (offset * p.lon / 180)).toInt ,
                math.floor(offset - offset / math.Pi * math.log((1 + math.sin(p.lat * math.Pi / 180)) / (1 - math.sin(p.lat * math.Pi / 180))) / 2).toInt
            )

            val localX = pixel.x % dim
            val localY = pixel.y % dim

            pixelFormat.add(localX, localY)
        }
    }

    /**
      * Uses DataSource to render points transforming them into pixels on the tile.
      * @param ds
      * @param queryName
      */
    def render(ds: DataSource, options: Map[String, String]) = {
        ds.get(bbox, options).foreach(x => setPixel(x))
        pixelFormat.project(image)
    }

    /**
      * Store image tile localy in file system.
      * @param filename
      * @return
      */
    def store(filename: String) = {
        ImageIO.write(image, "png", new File(filename))
    }

    /**
     * Refs:
     * https://www.mkyong.com/java/how-to-convert-bufferedimage-to-byte-in-java/
     */
    def getBytesPNG: Array[Byte] = {
        val baos = new ByteArrayOutputStream()
        ImageIO.write(image, "png", baos)
        baos.flush
        baos.toByteArray
    }
}

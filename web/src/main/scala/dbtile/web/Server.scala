package dbtile.web

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import dbtile._

import scala.io.StdIn

/**
 * Simple akka-http server to serve tiles.
 */
object WebServer {
    def main(args: Array[String]) {

        implicit val system = ActorSystem("actor-system")
        implicit val materializer = ActorMaterializer()
        // needed for the future flatMap/onComplete in the end
        implicit val executionContext = system.dispatcher

        val route =
            path("tile" / Segment / IntNumber / IntNumber / IntNumber) { (tileType, z, x, y) =>
            get {
                entity(as[HttpRequest]) { requestData => {
                    complete {
                        import java.awt.Color
                        val tile = new ImageTile(x, y, z, new VaryingIntensityPixel(new Color(0, 255, 0, 10), 64, z))
                        val ds = new DataSourceTablesaw
                        tile.render(ds, Map[String, String]())

                        val header = headers.RawHeader("Access-Control-Allow-Origin", "*")
                        val contentType = ContentType(MediaTypes.`image/png`)

                        HttpResponse(StatusCodes.OK, entity = HttpEntity(contentType, tile.getBytesPNG)).withHeaders(header)
                    }
                }}
            }
        }

        val testMap = path("map") {
            val body = """
<html>
<head>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.2.0/dist/leaflet.css"
   integrity="sha512-M2wvCLH6DSRazYeZRIm1JnYyh22purTM+FDB5CsyxtQJYeKq83arPe5wgbNmcFXGqiSH2XR8dT/fJISVA1r/zQ=="
   crossorigin=""/>
   <script src="https://unpkg.com/leaflet@1.2.0/dist/leaflet.js"
      integrity="sha512-lInM/apFSqyy1o6s89K4iQUKg6ppXEgsVxT35HbzUupEVRh2Eu9Wdl4tHj7dZO0s1uvplcYGmt3498TtHq+log=="
      crossorigin="">
    </script>
    <style>
        #mapid { height: 400px; width: 600px;}
    </style>
</head>
<body>
    <div id="mapid"></div>
    <script>
        var mymap = L.map('mapid').setView([0.0, 0.0], 1);
        L.tileLayer('http://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 18
        }).addTo(mymap);

        L.tileLayer('http://localhost:8080/tile/1950__TX/{z}/{x}/{y}', {
            maxZoom: 18,
            opacity: 0.75
        }).addTo(mymap);
    </script>
</body>
</html>
"""
            get {
                complete(
                    HttpEntity(ContentTypes.`text/html(UTF-8)`, body)
                )
            }
        }

        val bindingFuture = Http().bindAndHandle(route ~ testMap, "localhost", 8080)

        println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
        StdIn.readLine() // let it run until user presses return
        bindingFuture
          .flatMap(_.unbind()) // trigger unbinding from the port
          .onComplete(_ => system.terminate()) // and shutdown when done
    }
}

package dbtile.web

import com.typesafe.config.ConfigFactory

/**
  * Created by marcin1 on 8/27/17.
  */
object Config {
    lazy val conf = ConfigFactory.load()
}

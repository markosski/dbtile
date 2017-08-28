package dbtile

import com.typesafe.config.ConfigFactory
import java.io.File
import scala.io.Source
import scala.collection.mutable

class SqlQueryLookup(dir: File) {
    val files = dir.list.filter(_.contains(".sql"))
    val lookup = mutable.Map[String, String]()

    for (f <- files) {
        lookup += f.split('.')(0) -> Source.fromFile(new File(dir.toString + "/" + f)).mkString("").stripMargin.trim
    }

    def get(name: String): Option[String] = lookup.get(name)

    def getWithReplace(name: String, tokens: Seq[(String, String)]): Option[String] = {
        get(name) match {
            case Some(sql) =>
                Some(
                    tokens.foldLeft(sql)((sql, x) => sql.replaceAllLiterally(s"{{${x._1}}}", x._2))
                )
            case None => None
        }
    }
}

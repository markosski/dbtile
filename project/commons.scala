import sbt._
import Keys._

object Commons {
    val settings: Seq[Def.Setting[_]] = Seq(
      scalaVersion := "2.12.2",
      resolvers += Opts.resolver.mavenLocalFile,
      resolvers += "redshift" at "http://redshift-maven-repository.s3-website-us-east-1.amazonaws.com/release"
    )
}

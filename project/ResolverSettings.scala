import sbt._

/**
 * Resolvers
 */
object ResolverSettings {

  /**
   * The local / development resolvers: it includes the default ones + Scala Linter.
   */
  lazy val resolvers = Seq(
    Resolver.defaultLocal,
    Resolver.mavenLocal,
    Resolver.typesafeRepo("snapshots"),
    Resolver.typesafeRepo("releases"),
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    "Maven repo mirror UK" at "http://uk.maven.org/maven2"
  )

}

name := "crowdsaClient"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies += "pdeboer" % "pplib_2.11" % "0.1-SNAPSHOT"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.3.6"

libraryDependencies += "org.apache.httpcomponents" % "httpmime" % "4.3.6"

libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.5"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.34"

libraryDependencies += "com.typesafe" % "config" % "1.2.0"

libraryDependencies += "org.apache.pdfbox" % "pdfbox" % "1.8.8"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0-SNAP4"

// Scala 2.10, 2.11
libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc"       % "2.2.2",
  "com.h2database"  %  "h2"                % "1.4.184",
  "org.scalikejdbc" %% "scalikejdbc-config"  % "2.2.2"
)


resolvers += Resolver.file("Local repo", file(System.getProperty("user.home") + "/.ivy2/local"))(Resolver.ivyStylePatterns)
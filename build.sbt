organization := "com.thenetcircle.services"

name := "payment"

version := "2.0.0"

scalaVersion := "2.10.0-RC2"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

resolvers += "sardine" at "http://sardine.googlecode.com/svn/maven/"

resolvers += "artifactory" at "http://stresser:8081/artifactory/services"

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases" at "http://oss.sonatype.org/content/repositories/releases")


externalResolvers ++= Seq("local repository" at "file:///home/wee/.m2/repository")

libraryDependencies += "org.specs2" %% "specs2" % "1.13-SNAPSHOT" % "test"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.1.0-RC2" cross CrossVersion.full

libraryDependencies += "org.scalatest" %% "scalatest" % "2.0.M4" % "test" cross CrossVersion.full

libraryDependencies += "info.cukes" % "cucumber-core" % "1.1.1" % "test"

libraryDependencies += "info.cukes" % "cucumber-junit" % "1.1.1" % "test"

libraryDependencies += "info.cukes" % "cucumber-scala" % "1.1.1" % "test"

libraryDependencies += "info.cukes" % "cucumber-java" % "1.1.1" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.10.0-RC2" % "test"

libraryDependencies += "org.scala-lang" % "scala-library" % "2.10.0-RC2" % "test"

libraryDependencies += "com.googlecode.sardine" % "sardine" % "314"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.0.0"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.0.0"

libraryDependencies += "org.processing" % "core" % "1.5.1"

libraryDependencies += "javax.inject" % "javax.inject" % "1"

libraryDependencies += "com.typesafe" %% "scalalogging-slf4j" % "0.4.0" cross CrossVersion.full


ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.18"

lazy val sparkVersion = "3.5.7"
lazy val mongoVersion = "10.5.0"

lazy val root = (project in file("."))
  .settings(
    name := "final-project",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
      "org.apache.spark" %% "spark-mllib" % sparkVersion,
      "org.mongodb.spark" %% "mongo-spark-connector" % mongoVersion
    )
  )

ThisBuild / version := "1.0.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.10"

lazy val AkkaVersion = "2.7.0"
lazy val AkkaHttpVersion = "10.5.0"
lazy val LogbackVersion = "1.4.5"
lazy val ScalaTestVersion = "3.2.15"
lazy val AkkaStreamKafkaVer = "4.0.2"
lazy val SlickVer = "3.4.1"
lazy val PostgresVer = "42.5.4"
lazy val LiquibaseVer = "4.21.1"

lazy val root = (project in file("."))
  .settings(
    name := "akka-playground",
    idePackagePrefix := Some("com.mucciolo"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-remote" % AkkaVersion,
      "com.typesafe.akka" %% "akka-cluster-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-cluster-sharding-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-persistence-query" % AkkaVersion,
      "com.typesafe.akka" %% "akka-discovery" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream-kafka" % AkkaStreamKafkaVer,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.typesafe.slick" %% "slick" % SlickVer,
      "com.typesafe.slick" %% "slick-hikaricp" % SlickVer,
      "org.postgresql" % "postgresql" % PostgresVer,
      "org.liquibase" % "liquibase-core" % LiquibaseVer,
      "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
    ),
    resolvers += "Artifactory" at "https://kaluza.jfrog.io/artifactory/maven"
  )
ThisBuild / version := "1.0.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.2.2"

lazy val AkkaVersion = "2.7.0"
lazy val AkkaHttpVersion = "10.5.0"
lazy val LogbackVersion = "1.4.5"
lazy val ScalaTestVersion = "3.2.15"

lazy val root = (project in file("."))
  .settings(
    name := "akka-playground",
    idePackagePrefix := Some("com.mucciolo"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed"             % AkkaVersion,
      "com.typesafe.akka" %% "akka-remote"                  % AkkaVersion,
      "com.typesafe.akka" %% "akka-cluster-typed"           % AkkaVersion,
      "com.typesafe.akka" %% "akka-cluster-sharding-typed"  % AkkaVersion,
      "com.typesafe.akka" %% "akka-persistence-typed"       % AkkaVersion,
      "com.typesafe.akka" %% "akka-persistence-query"       % AkkaVersion,
      "com.typesafe.akka" %% "akka-discovery"               % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream-typed"            % AkkaVersion,
      "com.typesafe.akka" %% "akka-http"                    % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-slf4j"                   % AkkaVersion,
      "ch.qos.logback"    % "logback-classic"               % LogbackVersion,
      "org.scalatest"     %% "scalatest"                    % ScalaTestVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed"     % AkkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"          % AkkaVersion % Test
)
  )
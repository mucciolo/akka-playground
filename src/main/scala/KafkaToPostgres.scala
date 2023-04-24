package com.mucciolo

import Tables.points

import akka.actor.CoordinatedShutdown
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Keep
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{StringDeserializer, VoidDeserializer}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ProvenShape, Tag}

import java.sql.{Connection, DriverManager}
import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.language.postfixOps
import scala.util.{Failure, Success, Try, Using}

final case class Point(id: UUID, value: Long)

object Tables {

  final class PointTable(tag: Tag) extends Table[Point](tag, "points") {
    def id: Rep[UUID] = column[UUID]("id", O.Unique)
    def value: Rep[Long] = column[Long]("value")

    def * : ProvenShape[Point] = (id, value) <> (Point.tupled, Point.unapply)
  }

  val points = TableQuery[PointTable]
}


object KafkaToPostgres extends App {

  Using {
    val connection: Connection =
      DriverManager.getConnection("jdbc:postgresql://localhost:5432/playground", "user", "letmein")
    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection))
  } { db =>
    val liquibase = new Liquibase("db/changelog.yaml", new ClassLoaderResourceAccessor(), db)
    Try(liquibase.update())
  } match {
    case Failure(exception) =>
      println(exception.getMessage)
      System.exit(1)
    case Success(_) =>
      println("Liquibase migration was successful.")
  }

  private implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "KafkaToMysql")
  private implicit val ec: ExecutionContext = system.executionContext

  private val bootstrapServers = "localhost:9092"
  private val topic = "playground"
  private val subscription = Subscriptions.topics(topic)
  private val consumerSettings =
    ConsumerSettings[Void, String](system, new VoidDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId("playground")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  private val db = Database.forConfig("postgres")

  private val consumerControl: Consumer.Control =
    Consumer
      .committableSource(consumerSettings, subscription)
      .mapAsync(1) { msg =>
        val value = msg.record.value().toLong
        val point = Point(UUID.randomUUID(), value)
        println(point)

        db.run(points.returning(points.map(_.value)) += point).map(_ => msg.committableOffset)
      }
      .toMat(Committer.sink(CommitterSettings(system)))(Keep.left)
      .run()

  CoordinatedShutdown(system).addJvmShutdownHook(consumerControl.shutdown())

}

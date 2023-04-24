package com.mucciolo

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringSerializer, VoidSerializer}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object KafkaProducer extends App {

  private implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "KafkaToMysql")
  private implicit val ec: ExecutionContext = system.executionContext
  private val bootstrapServers = "localhost:9092"
  private val topic = "playground"

    private val producerSettings =
      ProducerSettings(system, new VoidSerializer, new StringSerializer)
        .withBootstrapServers(bootstrapServers)

    Source(1L to 10L)
      .throttle(1, 1 second)
      .map(_.toString)
      .map(value => new ProducerRecord[Void, String](topic, value))
      .runWith(Producer.plainSink(producerSettings))
      .onComplete(_ => system.terminate())

}

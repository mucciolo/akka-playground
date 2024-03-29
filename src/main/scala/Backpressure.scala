package com.mucciolo

import akka._
import akka.actor.typed._
import akka.actor.typed.scaladsl._
import akka.stream.scaladsl._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object Backpressure extends App {

  private implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "Backpressure")
  private implicit val ec: ExecutionContext = system.executionContext

  private val asyncFlow: Flow[Int, Int, NotUsed] =
    Flow[Int]
      .mapAsync(3) { x =>
        Future {
          println(s"[asyncFlow] x = $x @ ${Thread.currentThread().getName}")
          Thread.sleep(Random.nextInt(1000))
          x
        }
      }

  private val fastSource: Source[Int, NotUsed] =
    Source(1 to 10)
      .via(asyncFlow)
      .map { x =>
        println(s"[fastSource] x = $x @ ${Thread.currentThread().getName}")
        x
      }.async

  private val slowSink: Sink[Int, Future[Done]] = Sink.foreach[Int] { x =>
    println(s"[slowSink] x = $x @ ${Thread.currentThread().getName}")
    Thread.sleep(1000)
    println(x)
  }

  fastSource.runWith(slowSink).onComplete(_ => system.terminate())

}
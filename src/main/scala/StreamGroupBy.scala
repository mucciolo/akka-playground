package com.mucciolo

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.ExecutionContext

object StreamGroupBy extends App {

  private implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "group-by")
  private implicit val ec: ExecutionContext = system.executionContext

  Source(1L to 1000000L)
    .groupBy(maxSubstreams = 2, _ % 2L)
    .reduce(_ + _)
    .mergeSubstreams
    .runWith(Sink.seq)
    .map(println)
    .onComplete(_ => system.terminate())

}

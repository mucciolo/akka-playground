package com.mucciolo

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.scaladsl.{Flow, Sink, Source}
import org.slf4j.{Logger, LoggerFactory}

object SourceWithContextPlayground extends App {

  private implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "group-by")

  private val logger: Logger = LoggerFactory.getLogger(SourceWithContextPlayground.getClass)

  private val flow: Flow[(Int, Logger), (Int, Logger), NotUsed] = Flow.fromFunction {
    case (n, ctx) =>
      ctx.info("Received element: {}", n)
      (2 * n, ctx)
  }

  Source(1 to 2)
    .asSourceWithContext(_ => logger)
    .via(flow)
    .asSource
    .map { case (n, _) => n }
    .runWith(Sink.foreach(println))

}

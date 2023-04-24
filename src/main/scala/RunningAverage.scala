package com.mucciolo

import akka.actor.typed._
import akka.actor.typed.scaladsl.Behaviors
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.stage._
import akka.{Done, NotUsed}

import scala.concurrent.{ExecutionContextExecutor, Future}

object RunningAverage extends GraphStage[FlowShape[Int, Double]] {

  private val in = Inlet[Int]("MovingAverage.in")
  private val out = Outlet[Double]("MovingAverage.out")

  override val shape: FlowShape[Int, Double] = FlowShape.of(in, out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with InHandler with OutHandler {

      private var sum: Double = 0.0
      private var count: Int = 0

      override def onPush(): Unit = {
        val n = grab(in)
        sum += n
        count += 1
        push(out, sum / count)
      }

      override def onPull(): Unit = {
        pull(in)
      }

      setHandlers(in, out, this)

    }
}

object RunningAverageApp extends App {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "example")
  implicit val ec: ExecutionContextExecutor = system.executionContext

  val source: Source[Int, NotUsed] = Source(1 to 10)
  val sink: Sink[Double, Future[Done]] = Sink.foreach[Double](println)
  val pipeline: Future[Done] = source.via(RunningAverage).runWith(sink)

  pipeline.onComplete(_ => system.terminate())
}


package com.mucciolo

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl._
import akka.stream.scaladsl._
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

final class RunningAverageSpec extends AnyWordSpec with Matchers with BeforeAndAfterAll {

  implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty, "RunningAverageSpec")

  override def afterAll(): Unit = {
    system.terminate()
    super.afterAll()
  }

  "RunningAverage" should {
    "compute the running average of a stream" in {

      val end = 10
      val source = Source(1 to end)
      val expectedOutput = Seq(1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5)

      source.via(RunningAverage)
        .runWith(TestSink())
        .request(end)
        .expectNextN(expectedOutput)
        .expectComplete()

    }
  }
}

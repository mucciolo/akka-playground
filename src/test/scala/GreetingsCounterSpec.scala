package com.mucciolo

import GreeterActor.Greet

import akka.actor.testkit.typed.scaladsl
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, LoggingTestKit, ManualTime, ScalaTestWithActorTestKit}
import org.scalatest.{BeforeAndAfterAll, color}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}

final class GreetingsCounterSpec extends ScalaTestWithActorTestKit
  with AnyWordSpecLike with Matchers {

  "GreetingsCounter" should {
    "greet if max count is not reached" in {

      val maxCount = 2
      val counter = spawn(GreetingsCounter(maxCount))
      val probe = createTestProbe[GreeterActor.Greet]()

      counter ! GreeterActor.Greeted("test", probe.ref)
      probe.expectMessage(GreeterActor.Greet("test", counter.ref))

    }

    "log greeting number" in LoggingTestKit.info("Greeting 1 for test").expect {

      val maxCount = 2
      val counter  = spawn(GreetingsCounter(maxCount))
      val probe    = createTestProbe[GreeterActor.Greet]()

      counter ! GreeterActor.Greeted("test", probe.ref)

    }

    "should stop when max count is reached" in {

      val maxCount = 1
      val counter = spawn(GreetingsCounter(maxCount))
      val probe = createTestProbe[GreeterActor.Greet]()

      counter ! GreeterActor.Greeted("test", probe.ref)
      probe.expectTerminated(counter)
    }
  }

}
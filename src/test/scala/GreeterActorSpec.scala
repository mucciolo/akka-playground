package com.mucciolo

import GreeterActor.Greet

import akka.actor.testkit.typed.scaladsl.LoggingTestKit
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, ManualTime, ScalaTestWithActorTestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}

final class GreeterActorSpec extends ScalaTestWithActorTestKit
  with AnyWordSpecLike with Matchers {

  "Greeter" should {
    "respond to the greeting actor" in {

      val greeter = spawn(GreeterActor())
      val probe = createTestProbe[GreeterActor.Greeted]()

      greeter ! GreeterActor.Greet("test", probe.ref)

      probe.expectMessage(GreeterActor.Greeted("test", greeter.ref))

    }

    "say hello when greeted" in LoggingTestKit.info("Hello, test!").expect {
      val greeter = spawn(GreeterActor())
      val probe   = createTestProbe[GreeterActor.Greeted]()

      greeter ! GreeterActor.Greet("test", probe.ref)
    }
  }

}
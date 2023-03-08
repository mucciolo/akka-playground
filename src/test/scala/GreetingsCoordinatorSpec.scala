package com.mucciolo

import GreeterActor.Greet
import GreetingsCoordinator.SayHello

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, LoggingTestKit, ManualTime, ScalaTestWithActorTestKit}
import akka.actor.typed.scaladsl.Behaviors
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.{AnyWordSpec, AnyWordSpecLike}
import org.scalatest.{BeforeAndAfterAll, color}

final class GreetingsCoordinatorSpec extends ScalaTestWithActorTestKit
  with AnyWordSpecLike with Matchers {

  "GreetingsCoordinator" should {
    "should say greet exactly 3 times" in LoggingTestKit
      .info("Hello, test!")
      .withOccurrences(3).expect {

      val coordinator = spawn(GreetingsCoordinator())

      coordinator ! SayHello("test")

    }

  }

}
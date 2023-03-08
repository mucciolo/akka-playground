package com.mucciolo

import GreeterActor.*
import GreetingsCoordinator.SayHello

import akka.actor.typed.scaladsl.{Behaviors, LoggerOps}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}

object GreeterActor {
  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
  final case class Greeted(whom: String, from: ActorRef[Greet])

  def apply(): Behavior[Greet] = Behaviors.receive { (ctx, msg) =>
    ctx.log.info(s"Hello, ${msg.whom}!")

    msg.replyTo ! Greeted(msg.whom, ctx.self)
    Behaviors.same
  }
}

object GreetingsCounter {

  def apply(max: Int): Behavior[Greeted] = {
    counter(0, max)
  }

  private def counter(greetingCounter: Int, max: Int): Behavior[Greeted] =
    Behaviors.receive { (ctx, msg) =>

      val n = greetingCounter + 1
      ctx.log.info(s"Greeting $n for ${msg.whom}")

      if n == max then
        Behaviors.stopped
      else
        msg.from ! Greet(msg.whom, ctx.self)
        counter(n, max)
    }
}

object GreetingsCoordinator {

  final case class SayHello(name: String)

  def apply(): Behavior[SayHello] =
    Behaviors.setup { ctx =>
      val greeter = ctx.spawn(GreeterActor(), "greeter")

      Behaviors.receiveMessage { msg =>
        val counter = ctx.spawn(GreetingsCounter(max = 3), msg.name)
        greeter ! Greet(msg.name, replyTo = counter)
        Behaviors.same
      }
    }
}

@main def greeterActor(): Unit = {
  val system: ActorSystem[SayHello] = ActorSystem(GreetingsCoordinator(), "coordinator")

  system ! SayHello("World")
  system ! SayHello("Akka")

  system.terminate()
}
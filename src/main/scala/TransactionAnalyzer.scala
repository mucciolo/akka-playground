package com.mucciolo

import akka.actor.typed.*
import akka.actor.typed.scaladsl.*
import akka.stream.ClosedShape
import akka.stream.scaladsl.*

import scala.concurrent.{ExecutionContext, Future}

object TransactionAnalyzer {

  private implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "Backpressure")
  private implicit val ec: ExecutionContext = system.executionContext

  final case class Transaction(amount: Double, sender: String, receiver: String, date: Int)

  /* Transaction is suspect when:
    - amount > 10k
    - receiver is in a blacklist
    - date is wrong when date > 1000 days
   */
  val suspicionAmountThreshold = 10000
  val suspicionDateThreshold = 1000
  val blacklist = Set("badguy", "badlady", "yourkids")
  val transactions = List(
    Transaction(30, "goodguy1", "goodguy2", 0),
    Transaction(30000, "goodguy1", "badguy", 0),
    Transaction(300, "badguy", "goodguy2", 3000),
    Transaction(3530, "goodguy1", "yourkids", 30000),
  )

  val countSink = Sink.fold[Int, Transaction](0)((count, _) => count + 1)

  val sumAllCounts: (Future[Int], Future[Int], Future[Int]) => Future[Int] = (f1, f2, f3) => for {
    count1 <- f1
    count2 <- f2
    count3 <- f3
  } yield count1 + count2 + count3

  val amlGraph = GraphDSL.createGraph(countSink, countSink, countSink)(sumAllCounts) {
    implicit builder =>
      (amountSink, blacklistSink, dateSink) =>

        import GraphDSL.Implicits._

        val txnSource = builder.add(Source(transactions))
        val broadcast = builder.add(Broadcast[Transaction](3))

        val amountDetector = builder.add(
          Flow[Transaction]
            .filter(txn => txn.amount > suspicionAmountThreshold)
            .wireTap(txn => println(s"Suspicious txn: improper amount $txn"))
        )

        val blacklistedDetector = builder.add(
          Flow[Transaction]
            .filter(txn => blacklist.contains(txn.receiver))
            .wireTap(txn => println(s"Suspicious txn: blacklisted $txn"))
        )

        val dateDetector = builder.add(
          Flow[Transaction]
            .filter(txn => txn.date > suspicionDateThreshold)
            .wireTap(txn => println(s"Suspicious txn: weird date $txn"))
        )

        // @formatter:off
        txnSource ~> broadcast ~> amountDetector      ~> amountSink
                     broadcast ~> blacklistedDetector ~> blacklistSink
                     broadcast ~> dateDetector        ~> dateSink
        // @formatter:on

        ClosedShape
  }

  @main def run(): Unit = {
    RunnableGraph.fromGraph(amlGraph)
      .run()
      .foreach(finalCount => println(s"TOTAL warnings: $finalCount"))

    system.terminate()
  }

}

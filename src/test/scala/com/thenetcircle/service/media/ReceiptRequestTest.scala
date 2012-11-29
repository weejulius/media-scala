package com.thenetcircle.service.media

import org.specs2.mutable.Specification
import akka.actor.{ Props, ActorSystem }
import akka.pattern.ask
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import concurrent.Await

/**
 * User: julius.yu
 * Date: 11/29/12
 */
class ReceiptRequestTest extends Specification {

  val system = ActorSystem("system")
  val myActor = system.actorOf(Props[ReceiptRequest], name = "receipt_request")
  implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  "the request with parameter <name>" should {
    "be receipted" in {
      Await.result(myActor ask Map("name" -> "jyu"), timeout.duration).asInstanceOf[String] must beEqualTo("hello jyu")
    }
  }

}

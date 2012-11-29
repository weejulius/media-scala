package com.thenetcircle.service.media

import akka.actor.Actor

/**
 * User: julius.yu
 * Date: 11/29/12
 */
class ReceiptRequest extends Actor {

  def receive = {
    case a: Map[String, String] => {
      println("------" + a)
      "hello " + a.get("name")
    }
  }
}

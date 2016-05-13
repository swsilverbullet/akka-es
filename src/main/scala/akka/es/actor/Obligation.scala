package akka.es.actor

import akka.actor.Actor
import akka.actor.Actor.Receive
import akka.es.actor.Obligation._

class Obligation(id: String, contractualQuantity: Long) extends Actor {
  private var settledQuantities: Seq[Long] = Seq()
  private var state: State = Open

  override def receive: Receive = {
    case Confirm(quantity) =>
      println(s"confirm: $quantity")
      settledQuantities = settledQuantities :+ quantity
      updateStateForConfirm()
    case Cancel =>
      state = Cancelled
  }

  private def updateStateForConfirm() =
    if (settledQuantities.sum == contractualQuantity)
      state = Settled
    else if (settledQuantities.sum < contractualQuantity)
      state = PartiallySettled
    else
      state = OverSettled
}

object Obligation {
  case class Confirm(settledQuantity: Long)
  case class Cancel()

  sealed trait State
  case object Open extends State
  case object PartiallySettled extends State
  case object Settled extends State
  case object OverSettled extends State
  case object Cancelled extends State
}
package akka.es.actor

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class ObligationSpec extends TestKit(ActorSystem("ObligationSpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An Echo actor" must {
    "send back messages unchanged" in {
      val obligation = system.actorOf(TestActors.echoActorProps)
      obligation ! "hello world"
      expectMsg("hello world")
    }
  }

  "An obligation actor" must {
    "receive confirmation message" in {
      val obligation = system.actorOf(Props(classOf[Obligation], "1", 1000L))
      obligation ! Obligation.Confirm(100)
    }
  }
}

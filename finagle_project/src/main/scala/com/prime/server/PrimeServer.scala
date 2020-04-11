package com.prime.server

import com.prime.logic.PrimeNumberGenerator
import com.server.{PrimeList, PrimeServerService}
import com.twitter.util.{Await, Future}
import com.twitter.app.App
import com.twitter.finagle.{ListeningServer, Thrift}
import com.twitter.server.TwitterServer

class PrimeServer(address: String) extends TwitterServer {

  val primeServer: ListeningServer = Thrift.server.serveIface(
    address,
    PrimeServerService
  )

  def main(): Unit = {
    onExit {
      primeServer.close()
    }
    Await.ready(primeServer)
  }

}

object PrimeServerService extends PrimeServerService[Future] {
  override def getPrimeNumber(input: Int): Future[PrimeList] =
    Future(PrimeList(PrimeNumberGenerator.getPrimes(input)))
}

object RunPrimeServer extends App {
  val primeServer = new PrimeServer("localHost:9000")
  primeServer.main()
}
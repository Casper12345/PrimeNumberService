package com.prime.server

import com.prime.logic.PrimeNumberGenerator
import com.server.{PrimeList, PrimeServerService}
import com.twitter.util.{Await, Future}
import com.twitter.app.App
import com.twitter.finagle.{ListeningServer, Thrift}
import com.twitter.server.TwitterServer
import com.typesafe.config.{Config, ConfigFactory}

class PrimeServer(address: String) extends TwitterServer {

  private val primeServer: ListeningServer = Thrift.server.serveIface(
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
  val conf: Config = ConfigFactory.load("server_app.conf")
  val primeServer = new PrimeServer(conf.getString("server.address"))
  primeServer.main()
}
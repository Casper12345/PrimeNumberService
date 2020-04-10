package com.prime.server

import com.prime.logic.PrimeNumberGenerator
import com.server.{PrimeList, PrimeServerService}
import com.twitter.util.{Await, Future}
import com.twitter.app.App
import com.twitter.finagle.{ListeningServer, Thrift}

object Server extends App {

  val primeServer: ListeningServer = Thrift.server.serveIface(
    "localHost:9000",
    PrimeServerService
  )
  onExit {
    primeServer.close()
  }

  Await.ready(primeServer)
}

object PrimeServerService extends PrimeServerService[Future] {
  override def getPrimeNumber(input: Int): Future[PrimeList] =
    Future(PrimeList(PrimeNumberGenerator.getPrimes(input)))
}
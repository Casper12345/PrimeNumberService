package com.prime.proxy

import com.server.PrimeServerService.GetPrimeNumber
import com.server.{PrimeList, PrimeServerService}
import com.twitter.finagle.{Http, Service, Thrift}
import com.twitter.util.{Await, Return, Throw}
import com.twitter.finagle.http.{HttpMuxer, Request, Response, Status}
import com.twitter.server.TwitterServer
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.path._
import spray.json._
import com.twitter.app.App

class ProxyHttpServer(serverAddress: String, proxyAddress: String) extends TwitterServer {
  import com.prime.serializer.JsonSerializers._

  val clientServicePerEndpoint: PrimeServerService.ServicePerEndpoint =
    Thrift.client.servicePerEndpoint[PrimeServerService.ServicePerEndpoint](
      serverAddress,
      "thrift_client"
    )

  def primeService(number: Int): Service[Request, Response] = (request: Request) => {
    clientServicePerEndpoint.getPrimeNumber(GetPrimeNumber.Args(number)).liftToTry.map {
      case Throw(e) =>
        val response = Response(request.version, Status.InternalServerError)
        response.contentString = e.getMessage
        response
      case Return(list: PrimeList) =>
          val response = Response(request.version, Status.Ok)
          response.setContentTypeJson()
          response.contentString = list.toJson.prettyPrint
          response
    }
  }

  def main() {
    val router = RoutingService.byPathObject[Request] {
      case Root / "prime" / Integer(number) => primeService(number)
    }

    HttpMuxer.addRichHandler("/", router)
    val server = Http.serve(proxyAddress, HttpMuxer)
    onExit {
      server.close()
    }
    Await.ready(server)
  }

}

object RunProxyHttpServer extends App {
  val proxyServer = new ProxyHttpServer( "localhost:9000", ":8888")
  proxyServer.main()
}
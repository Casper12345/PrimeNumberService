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

object ProxyHttpServer extends TwitterServer {
  import com.prime.serializer.JsonSerializers._

  val clientServicePerEndpoint: PrimeServerService.ServicePerEndpoint =
    Thrift.client.servicePerEndpoint[PrimeServerService.ServicePerEndpoint](
      "localhost:9000",
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
    val server = Http.serve(":8888", HttpMuxer)
    onExit {
      server.close()
    }
    Await.ready(server)
  }

}

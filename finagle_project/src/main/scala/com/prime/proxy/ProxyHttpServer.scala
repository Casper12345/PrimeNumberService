package com.prime.proxy

import com.prime.util.ServerErrors.GenericHttpRequestError
import com.server.PrimeServerService.GetPrimeNumber
import com.server.{PrimeList, PrimeServerService}
import com.twitter.finagle.{Http, Service, Thrift}
import com.twitter.util.{Await, Future, Return, Throw}
import com.twitter.finagle.http.{HttpMuxer, Request, Response, Status, Version}
import com.twitter.server.TwitterServer
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.path._
import spray.json._
import com.twitter.app.App
import com.typesafe.config.{Config, ConfigFactory}

class ProxyHttpServer(serverAddress: String, proxyAddress: String) extends TwitterServer {

  import com.prime.serializer.JsonSerializers._

  private val clientServicePerEndpoint: PrimeServerService.ServicePerEndpoint =
    Thrift.client.servicePerEndpoint[PrimeServerService.ServicePerEndpoint](
      serverAddress,
      "thrift_client"
    )

  private def primeService(number: Int): Service[Request, Response] = (request: Request) => {
    clientServicePerEndpoint.getPrimeNumber(GetPrimeNumber.Args(number)).liftToTry.flatMap {
      case Throw(e) =>
        errorService(e)(request)
      case Return(list: PrimeList) =>
        Future.value(createJsonResponse(request.version, list.toJson.prettyPrint, Status.Ok))
    }
  }

  private def errorService(throwable: Throwable): Service[Request, Response] = (request: Request) => {
    val errorMessageJson = throwable.toJson.prettyPrint
    throwable match {
      case _: GenericHttpRequestError =>
        Future.value(createJsonResponse(request.version, errorMessageJson, Status.BadRequest))
      case _ =>
        Future.value(createJsonResponse(request.version, errorMessageJson, Status.InternalServerError))
    }
  }

  private def createJsonResponse(version: Version, jsonString: String, status: Status): Response = {
    val response = Response(version, status)
    response.setContentTypeJson()
    response.setContentString(jsonString)
    response
  }

  private val router: RoutingService[Request] = RoutingService.byPathObject[Request] {
    case Root / "prime" / Integer(number) =>
      if (number <= 1000000 && number >= 0) primeService(number) else errorService(
        GenericHttpRequestError(s"$number is not in the allowed range: 0 - 1000000")
      )
  }

  def main() {
    HttpMuxer.addRichHandler("/", router)
    val server = Http.serve(proxyAddress, HttpMuxer)
    onExit {
      server.close()
    }
    Await.ready(server)
  }

}

object RunProxyHttpServer extends App {
  val conf: Config = ConfigFactory.load("proxy_app.conf")
  val proxyServer = new ProxyHttpServer(
    conf.getString("server.prime_server_address"),
    conf.getString("server.http_proxy_address")
  )
  proxyServer.main()
}
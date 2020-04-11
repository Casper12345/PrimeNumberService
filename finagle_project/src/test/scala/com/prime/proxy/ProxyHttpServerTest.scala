package com.prime.proxy

import com.prime.server.PrimeServer
import com.twitter.finagle.Http
import com.twitter.finagle.http.{Method, Request, Status}
import com.twitter.inject.server.{EmbeddedTwitterServer, FeatureTest}
import com.typesafe.config.{Config, ConfigFactory}

class ProxyHttpServerTest extends FeatureTest {

  val proxyConf: Config = ConfigFactory.load("proxy_app.conf")
  val serverConf: Config = ConfigFactory.load("server_app.conf")

  override protected val server =
    new EmbeddedTwitterServer(
      new ProxyHttpServer(
        proxyConf.getString("server.prime_server_address"),
        proxyConf.getString("server.http_proxy_address")
      )
    )

  val primeServer = new EmbeddedTwitterServer(
    new PrimeServer(serverConf.getString("server.address"))
  )

  override protected def beforeAll(): Unit = {
    server.start()
    primeServer.start()
  }

  private lazy val httpClient =
    Http.client
      .withSessionQualifier.noFailFast
      .withSessionQualifier.noFailureAccrual
      .newService(
        proxyConf.getString("server.http_proxy_address")
      )

  test("ProxyHttpServer#starts") {
    server.isHealthy should be(true)
  }

  test("ProxyHttpServer#return 200 and fetch primes on correct url") {
    val request = Request(Method.Get, "/prime/10")

    val response = await(httpClient(request))
    response.status should equal(Status.Ok)
    response.getContentString() should equal("{\n  \"prime_list\": [2, 3, 5, 7]\n}")
  }

  test("ProxyHttpServer#return 404 on incorrect url") {
    val request = Request(Method.Get, "/prim")

    val response = await(httpClient(request))
    response.status should equal(Status.NotFound)
  }

  test("ProxyHttpServer#return 400 on negative input") {
    val request = Request(Method.Get, "/prime/-200")

    val response = await(httpClient(request))
    response.status should equal(Status.BadRequest)
    response.getContentString() should equal("{\n  \"error_message\": \"-200 is not in the allowed range: 0 - 1000000\"\n}")
  }

  test("ProxyHttpServer#return 400 on incorrect range") {
    val request = Request(Method.Get, "/prime/1000002")

    val response = await(httpClient(request))
    response.status should equal(Status.BadRequest)
    response.getContentString() should equal("{\n  \"error_message\": \"1000002 is not in the allowed range: 0 - 1000000\"\n}")
  }

}
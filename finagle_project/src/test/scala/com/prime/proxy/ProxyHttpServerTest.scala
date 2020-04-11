package com.prime.proxy

import java.net.InetAddress
import com.prime.server.PrimeServer
import com.twitter.finagle.Http
import com.twitter.finagle.http.{Method, Request, Status}
import com.twitter.inject.server.{EmbeddedTwitterServer, FeatureTest}

class ProxyHttpServerTest extends FeatureTest {

  override protected val server =
    new EmbeddedTwitterServer(new ProxyHttpServer("localhost:9000", ":8888"))

  val primeServer = new EmbeddedTwitterServer(new PrimeServer("localHost:9000"))

  override protected def beforeAll(): Unit = {
    server.start()
    primeServer.start()
  }

  private lazy val httpClient =
    Http.client
      .withSessionQualifier.noFailFast
      .withSessionQualifier.noFailureAccrual
      .newService(
        s"${InetAddress
          .getLoopbackAddress
          .getHostAddress
        }:8888")

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

}
package com.prime.logic

import org.scalatest.{FlatSpec, Matchers}
import scala.io.Source
import scala.util.Try

class PrimeNumberGeneratorTest extends FlatSpec with Matchers {

  "isPrime" should "return true when number is prime and false when not" in {
    val primeList = Util.parsePrimes("test_resources/first_10k_primes.txt")

    for (i <- 0 to 104730) {
      if (primeList.contains(i)) {
        PrimeNumberGenerator.isPrime(i) shouldBe true
      } else {
        PrimeNumberGenerator.isPrime(i) shouldBe false
      }
    }
  }

  "getPrimes" should "return first primes up to 1m" in {
    val primeList = Util.parsePrimes("test_resources/primes_up_to_1m.txt")

    val output = PrimeNumberGenerator.getPrimes(1000000)

    output shouldEqual primeList

  }
}

object Util {
  def parsePrimes(file: String): List[Int] = {
    Source.fromResource(file).getLines()
      .reduce((s1,s2) => s1 + " " + s2).split("[ \\t]+")
      .flatMap(s => Try(s.toInt).toOption).toList
  }
}

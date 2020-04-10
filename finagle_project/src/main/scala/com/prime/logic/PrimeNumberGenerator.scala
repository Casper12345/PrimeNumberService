package com.prime.logic

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

object PrimeNumberGenerator {
  private val PrimeCache = ListBuffer[Int]()

  private[logic] def isPrime(n: Int): Boolean = {
    @tailrec
    def go(i: Int): Boolean = {
      if (i == n) {
        true
      } else {
        if (n % i == 0) {
          false
        } else {
          go(i + 1)
        }
      }
    }

    def isPrime: Boolean = {
      if(PrimeCache.contains(n)){
        true
      } else {
        if(go(2)) {
          PrimeCache += n
          true
        } else {
          false
        }
      }
    }
    if(n <= 1) false else isPrime
  }

  private[prime] def getPrimes(n: Int): List[Int] = {
    @tailrec
    def go(i: Int, acc: List[Int]): List[Int] = {
      if(i > n) {
        acc
      } else {
        if(isPrime(i)){
          go(i + 1, acc :+ i)
        } else {
          go(i + 1, acc)
        }
      }
    }
    go(2, Nil)
  }
}

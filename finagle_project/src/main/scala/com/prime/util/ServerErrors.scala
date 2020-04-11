package com.prime.util

object ServerErrors {
  case class GenericHttpRequestError(m: String) extends Throwable(m)
}

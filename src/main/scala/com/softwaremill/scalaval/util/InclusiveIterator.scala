package com.softwaremill.scalaval.util

class InclusiveIterator[A](ia: Iterator[A]) {
  def takeWhileInclusive(p: A => Boolean) = {
    var done = false
    val p2 = (a: A) => !done && { if (!p(a)) done=true; true }
    ia.takeWhile(p2)
  }
}

object InclusiveIterator {
  implicit def iterator_can_include[A](ia: Iterator[A]) = new InclusiveIterator(ia)
}

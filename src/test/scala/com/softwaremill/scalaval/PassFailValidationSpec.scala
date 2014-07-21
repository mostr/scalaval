package com.softwaremill.scalaval

import org.scalatest._

class PassFailValidationSpec extends FlatSpec with ShouldMatchers {

  import Validation._

  it should "execute provided block only when all validation fails" in {
    // given
    val set = Set(1, 2, 3)
    val nonEmpty= rule("size")(set.nonEmpty, "Set cannot be empty")
    val containsThree = rule("containsThree")(set.contains(3), "Set should contain element 3")

    // when
    var blockCalled = false
    validate(nonEmpty, containsThree).whenOk {
      blockCalled = true
    }

    // then
    blockCalled should be(true)
  }

  it should "not not execute provided block when any validation fails" in {
    // given
    val set = Set(1, 2)
    val nonEmpty= rule("size")(set.nonEmpty, "Set cannot be empty")
    val containsThree = rule("containsThree")(set.contains(3), "Set should contain element 3")

    // when
    var blockCalled = false
    validate(nonEmpty, containsThree).whenOk {
      blockCalled = true
    }

    // then
    blockCalled should be(false)
  }

}

package com.softwaremill.scalaval

import org.scalatest._

class EvaluatingRulesSpec extends FlatSpec with ShouldMatchers {

  import Validation._

  val setToValidate = Set(1, 2, 3)

  it should "not evaluate next rule if previous failed and was set to stop on fail" in {
    // given
    val containsFive = rule("containFive", haltOnFail = true)(setToValidate.contains(5), "Set should contain element 5")
    val containsThree= rule("containThree") {
      fail("This rule should not be evaluated")
      (setToValidate.contains(3), "Set should contain element 3")
    }

    // when
    validate(containsFive, containsThree).errors

    // then
    // all should be fine
  }

}

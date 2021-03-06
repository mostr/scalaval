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
  }

  it should "not evaluate rule when rule created" in {
    rule("containThree") {
      fail("This rule should not be evaluated")
      (setToValidate.contains(3), "Set should contain element 3")
    }
  }

  it should "evaluate rules only when validation called" in {
    // given
    var ruleEvaluated = false
    val r = rule("containThree") {
      ruleEvaluated = true
      (setToValidate.contains(3), "Set should contain element 3")
    }

    // when
    validate(r)

    // then
    ruleEvaluated should be(true)
  }

  it should "evaluate rules only once" in {
    // given
    var ruleRunCount = 0
    val r = rule("containThree") {
      ruleRunCount += 1
      (setToValidate.contains(3), "Set should contain element 3")
    }

    // when
    val result = validate(r)
    result.errors
    result.whenOk {
      // noop
    }

    // then
    ruleRunCount should be(1)
  }

}

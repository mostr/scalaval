package com.softwaremill.scalaval

import org.scalatest.{FlatSpec, ShouldMatchers}

class CreateValidationSpec extends FlatSpec with ShouldMatchers {

  import Validation._

  it should "create Validation with provided rules" in {
    // either
    val ruleOne = rule("one")(true, "Rule one not satisfied msg")
    val ruleTwo = rule("two")(false, "Rule two not satisfied msg")
    validate(ruleOne, ruleTwo)

    // or
    val rulesList = List(ruleOne, ruleTwo)
    validate(rulesList)
  }

  it should "create rule with some logic" in {
    // given
    val set = Set(1, 3, 5, 7)

    // when
    val allOdd = rule("allOdd") {
      val evenFound = set.exists(_ % 2 == 0)
      (evenFound, "Set should contain only odd numbers")
    }

    // then
    validate(allOdd)
  }

  it should "create rule - different ways" in {
    val ruleForField = rule("email")(true, "Email should be valid")
    val ruleForFieldWithHalt = rule("email", haltOnFail = true)(true, "Email should be valid")
    val generalRule = rule((true, "Email should be valid"))
    val generalRuleWithBlock = rule {
      (true, "Email should be valid")
    }
    val generalRuleWithHalt = rule(haltOnFail = true)(true, "Email should be valid")

    validate(ruleForField, ruleForFieldWithHalt, generalRule, generalRuleWithBlock, generalRuleWithHalt)
  }

}

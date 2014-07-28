package com.softwaremill.scalaval

import org.scalatest._

class ReportingValidationErrorsSpec extends FlatSpec with ShouldMatchers {

  import Validation._

  val setToValidate = Set(1, 2, 3)

  it should "report all errors when no rule set to stop on fail" in {
    // given
    val minLength= rule("length")(setToValidate.size > 5, "Set should contain at least 6 elements")
    val containsFive = rule("containsFive")(setToValidate.contains(5), "Set should contain element 5")

    // when
    val validationErrors = validate(minLength, containsFive).errors

    // then
    validationErrors.fieldErrors.flatMap(_._2) should be(List("Set should contain at least 6 elements", "Set should contain element 5"))
  }

  it should "stop on failing rule when set to stop on fail" in {
    // given
    val minLength= rule("length", haltOnFail = true)(setToValidate.size > 5, "Set should contain at least 6 elements")
    val containsFive = rule("containsFive")(setToValidate.contains(5), "Set should contain element 5")

    // when
    val validationErrors = validate(minLength, containsFive).errors

    // then
    validationErrors.fieldErrors.flatMap(_._2) should be(List("Set should contain at least 6 elements"))
  }

  it should "not stop on passing rule when marked as haltOnFailed" in {
    // given
    val minLength= rule("length", haltOnFail = true)(setToValidate.size > 2, "Set should contain at least 3 elements")
    val containsFive = rule("containsFive")(setToValidate.contains(5), "Set should contain element 5")

    // when
    val validationErrors = validate(minLength, containsFive).errors

    // then
    validationErrors.fieldErrors.flatMap(_._2) should be(List("Set should contain element 5"))
  }

  it should "report no errors when all rules were satisfied" in {
    // given
    val minLength= rule("length", haltOnFail = true)(setToValidate.size > 2, "Set should contain at least 3 elements")
    val containsThree = rule("containsThree")(setToValidate.contains(3), "Set should contain element 3")

    // when
    val validationErrors = validate(minLength, containsThree).errors

    // then
    validationErrors should be('empty)
  }

  it should "return Either Left with errors when called with code block and validation fails" in {
    // given
    val containsFive = rule("containsFive")(setToValidate.contains(5), "Set should contain element 5")

    // when
    val Left(result) = validate(containsFive).whenOk {
      // noop - will not be called, validation failed
    }

    // then
    result.fieldErrors.flatMap(_._2) should be(List("Set should contain element 5"))
  }

  it should "return Either Right with block result when code block executes" in {
    // given
    val containsThree = rule("containsThree")(setToValidate.contains(3), "Set should contain element 3")

    // when
    val Right(sum) = validate(containsThree).whenOk[Int] {
      // do things and keep calm - validation passed
      setToValidate.foldLeft(0) { (res, el) => res + el }
    }

    // then
    sum should be(6)
  }

  it should "report both general and field errors" in {
    // given
    val minLength= rule("length")(setToValidate.size >= 10, "Set should contain at least 10 elements")
    val containsFive = rule { (setToValidate.contains(5), "Set should contain element 5") }

    // when
    val validationErrors = validate(minLength, containsFive).errors

    // then
    validationErrors.fieldErrors.flatMap(_._2) should be(List("Set should contain at least 10 elements"))
    validationErrors.otherErrors should be(Seq("Set should contain element 5"))

  }

}

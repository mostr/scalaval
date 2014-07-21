package com.softwaremill.scalaval

import com.softwaremill.scalaval.Validation._


case class Validation(rules: Seq[ValidationRuleDef]) {

  private type ValidationErrors = Map[String, Seq[String]]

  lazy val errors = evaluatedRules.filter(!_.evaluated.result).groupBy(_.field).mapValues(_.map(_.evaluated.msg))

  def hasErrors = errors.nonEmpty

  def whenOk[T](block: => T): Either[ValidationErrors, T] = if(errors.isEmpty) Right(block) else Left(errors)

  private lazy val evaluatedRules = {
    import com.softwaremill.scalaval.util.InclusiveIterator._
    rules.toIterator.takeWhileInclusive { rule =>
      !rule.haltOnFail || rule.evaluated.result
    }.toSeq
  }

}

object Validation {

  def validate(checks: Iterable[ValidationRuleDef]) = new Validation(checks.toSeq)
  def validate(checks: ValidationRuleDef*) = Validation(checks)

  def rule(field: String, haltOnFail: Boolean = false)(checkFn: => (Boolean, String)) = new ValidationRuleDef(field, haltOnFail, checkFn)

  class ValidationRuleDef(val field: String, val haltOnFail: Boolean, checkFn: => (Boolean, String)) {
    lazy val evaluated = SingleRuleCheckResult.tupled(checkFn)
  }

  protected case class SingleRuleCheckResult(result: Boolean, msg: String)

  protected object SingleRuleCheckResult extends ((Boolean, String) => SingleRuleCheckResult)

}


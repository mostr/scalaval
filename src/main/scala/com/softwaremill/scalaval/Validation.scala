package com.softwaremill.scalaval

object Validation {
  
  type FieldErrors = Map[String, Seq[String]]
  type OtherErrors = Seq[String]

  private val OtherErrorsKey = "___"

  def rule(field: String, haltOnFail: Boolean = false)(checkFn: => (Boolean, String)): RuleDefinition = new RuleDefinition(field, haltOnFail, checkFn)

  def rule(haltOnFail: Boolean)(checkFn: => (Boolean, String)): RuleDefinition = rule(OtherErrorsKey, haltOnFail)(checkFn)

  def rule(checkFn: => (Boolean, String)): RuleDefinition = rule(haltOnFail = false)(checkFn)

  def validate(rules: RuleDefinition*): Result = validate(rules.toSeq)

  def validate(rules: Iterable[RuleDefinition]): Result = {
    import com.softwaremill.scalaval.util.InclusiveIterator._
    val evaluatedRules = rules.toIterator.takeWhileInclusive(rule => !rule.haltOnFail || rule.result).toSeq
    val allErrors = evaluatedRules.filter(!_.result).groupBy(_.field).mapValues(_.map(_.errorMsg))
    Result(Errors.fromMap(allErrors))
  }

  case class Errors(fieldErrors: FieldErrors, otherErrors: OtherErrors) {
    def isEmpty = fieldErrors.isEmpty && otherErrors.isEmpty
  }

  object Errors {
    def fromMap(errors: FieldErrors) = {
      val generalErrors = errors.find(_._1 == OtherErrorsKey)
      val fieldErrors = errors.filterNot(_._1 == OtherErrorsKey)
      Errors(fieldErrors, generalErrors.map(_._2).getOrElse(Seq.empty[String]))
    }
  }

  case class Result(errors: Errors) {
    def whenOk[T](block: => T): Either[Errors, T] = if(errors.isEmpty) Right(block) else Left(errors)
  }

  class RuleDefinition(val field: String, val haltOnFail: Boolean, checkFn: => (Boolean, String)) {
    private lazy val evaluatedRule = checkFn
    lazy val result = evaluatedRule._1
    lazy val errorMsg = evaluatedRule._2
  }

}
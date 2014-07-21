package com.softwaremill.scalaval

object Validation {
  
  type Errors = Map[String, Seq[String]]

  def rule(field: String, haltOnFail: Boolean = false)(checkFn: => (Boolean, String)) = new RuleDefinition(field, haltOnFail, checkFn)

  def validate(rules: RuleDefinition*): Result = validate(rules.toSeq)

  def validate(rules: Iterable[RuleDefinition]): Result = {
    import com.softwaremill.scalaval.util.InclusiveIterator._
    val evaluatedRules = rules.toIterator.takeWhileInclusive(rule => !rule.haltOnFail || rule.result).toSeq
    val errors = evaluatedRules.filter(!_.result).groupBy(_.field).mapValues(_.map(_.errorMsg))
    Result(errors)
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
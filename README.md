# ScalaVal

Dead simple validation micro library (or micro framework) for Scala. 

Why
---
Because every project bigger than HelloWorldApp requires data validation at some point. Reinventing the structure and validation handling boilerplate every time you need one is definitely not something developers enjoy. I too had this pain when hacking on [http://codebrag.com](Codebrag).

This small utility aims to provide minimal framework to write your validation logic, act as a guard between data and action and collect results in unified way. That's all it does. And it's really, really tiny. No external dependencies and no magic included. Just boilerplate.

Installation
---

Current version released is 0.1. Project package is deployed to [Sonatype's OSS repository](https://oss.sonatype.org/content/repositories/snapshots/com/softwaremill/scalaval/). To use it in your project, add a dependency:

````scala
libraryDependencies += "com.softwaremill.scalaval" %% "scalaval" % "0.1"
libraryDependencies += "com.softwaremill.scalaval" %% "scalaval" % "0.1"
````

ScalaVal works with Scala 2.10+.

How-to
---
1. Define rules (in Scala code, no insane rules lang etc). 
	- Each rule can get freely-defined "label" (usually name of field it validates) if you want to link given rule with corresponding field
 	- Returns pair: `(condition, errorMessage)`
  	- When `condition` is not met validation rule is considered as failed and `errorMessage` is collected
2. Call `validate` and collect errors or provide optional block of code to execute when validation passes
3. Handle validation result returned as `Either` with either errors reported or or block result

#### Quick example
See full example at the bottom of the page.

````scala
// rule
val checkUserNameAvailable = rule("username") {
  val exists = // call DB to check if user with this usename exists
  (exists, s"Username ${form.userName} is already taken")
}

val noFieldRule = rule {
  val isWorkday = // determine if workday
  (isWorkday, "Come on! Leave it, enjoy your weekend")
}

// validation
validate(noFieldRule, checkUserNameAvailable).whenOk {
  // save user
}
````

Evaluating rules
---
Rules are evaluated only once and only when `validate` is called.

By default **all rules are evaluated** and all errors collected are reported. You can make particular rule immediately halting validation process if required e.g. when further validation calls are expensive etc. In such case it will **evaluate only rules until this failing rule is found (inclusive)**. This is shown below:

````scala
// rule
val checkUserNameAvailable = rule("username", haltOnFail = true) {
  val exists = // call DB to check if user with this usename exists
  (exists, s"Username ${form.userName} is already taken")
}
````

Example usage
---
Take a look at project's [specs dir](src/test/scala/com/softwaremill/scalaval) for more detailed examples.


Let's validate some User sign-up form object

````scala
case class SignUpForm(email: String, password: String, rePassword: String)

object ScalaValExample extends App {

  import com.softwaremill.scalaval.Validation._

  // invalid email and passwords don't match
  val form = SignUpForm("john.doe.com", "secret", "secre")

  // define rules
  val workdayRule = rule {
    // this is general rule with no particular field
    val isWorkday = true // determine if workday
    (isWorkday, "Come on! Leave it, enjoy your weekend")
  }

  val emailValid = rule("email") {
    val emailOk = form.email.contains("@")  // just pretend it's enough
    (emailOk, s"Provided email ${form.email} is invalid")
  }
  val passwordLength = rule("password")(form.password.length >= 8, "Password is too short")
  val passwordMatch = rule("password")(form.password == form.rePassword, "Passwords don't match")

  // validate against rules (in order)
  val result = validate(workdayRule, emailValid, passwordLength, passwordMatch).whenOk {
    // register user
    // save in DB, trigger email sending, etc
  }

  // handle operation result
  result match {
    case Left(errors) => {
      errors.fieldErrors.flatMap(_._2).foreach(println)  // field errors, grouped by key provided on rule definition
      errors.otherErrors.foreach(println)                // oher errors, not linked to any particular field
    }
    case _ => // yay! User registered, go celebrating!
  }

}
````

License
---
Licensed under Apache 2.0 licence. 
Issues, Pull Requests are more than welcome!

# ScalaVal

Dead simple validation micro library (or micro framework) for Scala.


Why
---
Because every project bigger than HelloWorldApp requires data validation at some point. Reinventing the structure and validation handling boilerplate every time you need one is definitely not something developers enjoy. I too had this pain when hacking on [http://codebrag.com](Codebrag).

This small utility aims to provide minimal framework to write your validation logic, act as a guard between data and action and collect results in unified way. That's all it can do. And it's really tiny. No external dependencies and no magic included. Just boilerplate.

How-to
---
1. Define rules (in Scala code, no insane rules lang etc). 
	- Each rule gets freely-defined "label" (usually name of field it validates)
 	- Returns pair (as `Tuple2`) of `(condition, errorMessage)`.
  	- When `condition` is not met validation rule is considered as failed and `errorMessage` is collected
   - Rules are evaluated lazily.
2. Call validation and collect errors. Provide optional block of code to execute when validation passes
3. Handle validation result returned as `Either` with either errors or block result

#### Quick example
See full example at the bottom of the page.

````scala
// rule
val checkUserNameAvailable = rule("username") {
  val exists = // call DB to check if user with this usename exists
  (exists, s"Username ${form.userName} is already taken")
}

// validation
validate(checkUserNameAvailable).whenOk {
  // save user
}
````

Reporting validation errors
---
By default all rules are evaluated and all errors are reported.
You can make particular rule halting validation process too. It will report only errors collected until this failing rule (inclusive).

Laziness
---
All rules are evaluated lazily. When validation stops on single rule, next ones will not be evaluated

Requirements
---
ScalaVal requires Scala 2.11

Example usage
---
Take a look at project's [specs dir](src/test/scala/com/softwaremill/scalaval) for more detailed examples.


Let's validate some User sign-up form object

````scala
case class SignUpForm(userName: String, email: String, password: String, rePassword: String)

object ScalaValExample extends App {

  import com.softwaremill.scalaval.Validation._

  // invalid email and passwords don't match
  val form = SignUpForm("john", "john.doe.com", "secret", "secre")

  // define rules

  val userNameReq = rule("userName")(form.userName.nonEmpty, "Username field is required")

  val userNameTaken = rule("userName") {
    val exists = false // check whether username is already taken, call DB etc
    (exists, s"Username ${form.userName} is already taken")
  }

  val emailValid = rule("email") {
    val emailOk = form.email.contains("@")  // just pretend it's enough
    (emailOk, "Provide valid email address")
  }

  val passwordLength = rule("password")(form.password.length >= 8, "Password is too short")
  val passwordOk = rule("password")(form.password == form.rePassword, "Passwords don't match")

  // validate against rules (in order)
  val result = validate(userNameReq, userNameTaken, emailValid, passwordLength, passwordOk).whenOk {
    // register user
    // save in DB, trigger email sending, etc
  }

  // handle operation result
  result match {
    case Left(errors) => errors.flatMap(_._2).foreach(println)  // handle errors, grouped by key       
    case _ => // yay! User registered, go celebrating!
  }

}
````

License
---
Licensed under Apache 2.0 licence. 
Issues, Pull Requests are more than welcome!
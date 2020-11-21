# Zemail

[![Actions Status](https://github.com/dylandoamaral/zemail/workflows/Continuous%20Integration/badge.svg)](https://github.com/dylandoamaral/zemail/actions)
[![codecov](https://codecov.io/gh/dylandoamaral/zemail/branch/master/graph/badge.svg)](https://codecov.io/gh/dylandoamaral/zemail)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

A ZIO friendly library to send Email using Courier.

## Why use it

- You need to send emails using ZIO

## Using Zemail

The latest version is 0.1.0, which is avaible for scala 2.13.

If you're using sbt, add the following to your build:

```bash
libraryDependencies ++= Seq(
  "dev.doamaral" %% "zemail" % "0.1.0"
)
```

## How it works

Here is a sample to send an email:

```scala
import zio.{App, ExitCode}

import zemail.email
import zemail.email.MailerOps

import courier.{Mailer, _}

object Send extends App {
  val envelop = Envelope
    .from("zemail" `@` "gmail.com")
    .to("zinteract" `@` "gmail.com")
    .subject("Zemail")
    .content(Text("Hi from zemail !"))

  val app = for {
    _ <- email.send(envelop)
  } yield ()

  val builder = Mailer("smtp.gmail.com", 587)
    .auth(true)
    .as("zemail@gmail.com", "mypassword")
    .startTls(true)()

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideLayer(builder.buildLayer)
      .exitCode
}
```

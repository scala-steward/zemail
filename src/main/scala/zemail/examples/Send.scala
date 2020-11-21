package zemail.examples

import courier.Mailer

import zio.{App, ExitCode}

import zemail.email
import zemail.email.MailerOps

import courier._

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

package zemail.test

import zio.ZIO
import zio.test._
import zio.test.Assertion._

import zemail.email
import zemail.email.MailerOps

import courier._
import java.util.Properties
import javax.mail.Provider
import org.jvnet.mock_javamail.{Mailbox, MockTransport}

class MockedSMTPProvider
    extends Provider(Provider.Type.TRANSPORT, "mocked", classOf[MockTransport].getName, "Mock", null)

object EmailSpec extends DefaultRunnableSpec {
  private val mockedSession = javax.mail.Session.getDefaultInstance(new Properties() {
    {
      put("mail.transport.protocol.rfc822", "mocked")
    }
  })
  mockedSession.setProvider(new MockedSMTPProvider)

  def spec =
    suite("Send Email Spec")(
      testM("As an user I want to send an email") {
        val envelop = Envelope
          .from("from" `@` "gmail.com")
          .to("to" `@` "gmail.com")
          .subject("Test Title")
          .content(Text("Test Content"))

        val effect = for {
          _       <- email.send(envelop)
          mailbox <- ZIO.effect(Mailbox.get("to@gmail.com"))
          message <- ZIO.effect(mailbox.get(0))
        } yield assert(mailbox.size)(equalTo(1)) &&
          assert(message.getSubject)(equalTo("Test Title")) &&
          assert(message.getContent)(equalTo("Test Content"))

        val builder = Mailer(mockedSession)

        effect.provideLayer(builder.buildLayer)
      }
    )
}

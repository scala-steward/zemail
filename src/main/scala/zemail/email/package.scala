package zemail

import zio.{Has, RIO, ZIO, ZLayer}

import courier.Mailer
import courier.Envelope

package object email {
  type Email = Has[Mailer]

  /** Allow to convert a Mailer object into a Email layer
    */
  implicit class MailerOps(mailer: Mailer) {
    val buildLayer: ZLayer[Any, Throwable, Email] = ZLayer.fromEffect(ZIO.effect(mailer))
  }

  /** Get the underlying Mailer.
    */
  def underlying: RIO[Email, Mailer] =
    ZIO.access(_.get)

  /** Send an email
    */
  def send(e: Envelope): ZIO[Email, Throwable, Unit] =
    underlying.flatMap(mailer => ZIO.fromFuture(implicit ec => mailer(e)))
}

package cats.data

import cats.{Monad, MonadError}

private[cats] trait OptionTMonadErrorUnit[F[_]] extends MonadError[OptionT[F, ?], Unit] with OptionTMonad[F] {
  implicit def F: Monad[F]

  def raiseError[A](e: Unit): OptionT[F, A] = OptionT.none

  def handleErrorWith[A](fa: OptionT[F, A])(f: Unit => OptionT[F, A]): OptionT[F, A] =
    OptionT(F.flatMap(fa.value) {
      case s @ Some(_) => F.pure(s)
      case None => f(()).value
    })
}
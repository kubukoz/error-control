package cats.syntax

import cats.data.EitherT
import cats.{ErrorControl, Monad, MonadError}

object errorControl extends ErrorControlSyntax

trait ErrorControlSyntax {
  implicit final def catsSyntaxErrorControlF[F[_], E, A](fa: F[A])(
    implicit F: MonadError[F, E]): ErrorControlFOps[F, E, A] =
    new ErrorControlFOps[F, E, A](fa)

  implicit final def catsSyntaxErrorControlG[G[_], A](ga: G[A])(implicit G: Monad[G]): ErrorControlGOps[G, A] =
    new ErrorControlGOps[G, A](ga)

  implicit final def catsSyntaxErrorControlEither[G[_], E, A](gea: G[Either[E, A]])(
    implicit G: Monad[G]): ErrorControlEitherOps[G, E, A] =
    new ErrorControlEitherOps[G, E, A](gea)

}

final class ErrorControlFOps[F[_], E, A] private[syntax] (private val fa: F[A]) extends AnyVal {

  def control[G[_], B](f: Either[E, A] => G[B])(implicit E: ErrorControl[F, G, E]): G[B] =
    E.control(fa)(f)

  def controlError[G[_]](f: E => G[A])(implicit E: ErrorControl[F, G, E]): G[A] =
    E.controlError(fa)(f)

  def trial[G[_]](implicit E: ErrorControl[F, G, E]): G[Either[E, A]] =
    E.trial(fa)

  def trialT[G[_]](implicit E: ErrorControl[F, G, E]): EitherT[G, E, A] =
    E.trialT(fa)

  def intercept[G[_]](f: E => A)(implicit E: ErrorControl[F, G, E]): G[A] =
    E.intercept(fa)(f)
}

final class ErrorControlGOps[G[_], A] private[syntax] (private val ga: G[A]) extends AnyVal {
  def assure[F[_]]: AssurePartiallyApplied[F, G, A] = new AssurePartiallyApplied[F, G, A](ga)
}

final class ErrorControlEitherOps[G[_], E, A] private[syntax] (private val gea: G[Either[E, A]]) extends AnyVal {

  def absolve[F[_]](implicit E: ErrorControl[F, G, E]): F[A] =
    E.absolve(gea)
}

private[syntax] final class AssurePartiallyApplied[F[_], G[_], A] private[syntax] (private val ga: G[A])
    extends AnyVal {

  def apply[E](error: A => Option[E])(implicit E: ErrorControl[F, G, E]): F[A] =
    E.assure(ga)(error)
}

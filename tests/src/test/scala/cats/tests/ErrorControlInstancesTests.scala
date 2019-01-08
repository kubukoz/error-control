package cats

package tests

import cats.kernel.laws.discipline.SerializableTests
import cats.laws.discipline.{ErrorControlTests, MonadErrorTests}
import cats.data.{EitherT, IndexedStateT, State, StateT, WriterT, OptionT}
import cats.laws.discipline.arbitrary._
import cats.laws.discipline.eq._
import cats.instances.all._
import org.scalacheck.Arbitrary

class ErrorControlInstancesTests extends CatsSuite {
  import ErrorControlInstancesTests._

  implicit val eqEitherTFA: Eq[EitherT[StateT[Option, Int, ?], Unit, Int]] =
    EitherT.catsDataEqForEitherT[StateT[Option, Int, ?], Unit, Int]

  checkAll("Either[String, Int]", ErrorControlTests[Either[String, ?], Id, String].errorControl[Int])
  checkAll("ErrorControl[Either[String, ?], Id, String]",
           SerializableTests.serializable(ErrorControl[Either[String, ?], Id, String]))

  checkAll("EitherT[List, String, Int]", ErrorControlTests[EitherT[List, String, ?], List, String].errorControl[Int])
  checkAll(
    "ErrorControl[EitherT[List, String, ?], List, String]",
    SerializableTests.serializable(ErrorControl[EitherT[List, String, ?], List, String])
  )

  checkAll(
    "StateT[EitherT[Option, String, ?], Int, ?]",
    ErrorControlTests[StateT[EitherT[Option, String, ?], Int, ?], StateT[Option, Int, ?], String].errorControl[Int])
  checkAll(
    "ErrorControl[StateT[EitherT[Option, String, ?], Int, ?], StateT[Option, Int, ?], String]",
    SerializableTests.serializable(
      ErrorControl[StateT[EitherT[Option, String, ?], Int, ?], StateT[Option, Int, ?], String])
  )

  checkAll("Option[Int]", ErrorControlTests[Option, Id, Unit].errorControl[Int])
  checkAll("ErrorControl[Option, Id, Unit]", SerializableTests.serializable(ErrorControl[Option, Id, Unit]))

  checkAll("OptionT[List, Int]", MonadErrorTests[OptionT[List, ?], Unit].monadError[Int, Int, Int])
  checkAll("MonadError[OptionT[List, ?]]", SerializableTests.serializable(MonadError[OptionT[List, ?], Unit]))

  checkAll("OptionT[List, Int]", ErrorControlTests[OptionT[List, ?], List, Unit].errorControl[Int])
  checkAll("ErrorControl[OptionT[List, ?], List, Unit]",
           SerializableTests.serializable(ErrorControl[OptionT[List, ?], List, Unit]))

  checkAll(
    "WriterT[EitherT[Option, String, ?], Int, ?]",
    ErrorControlTests[WriterT[EitherT[Option, String, ?], Int, ?], WriterT[Option, Int, ?], String].errorControl[Int]
  )
  checkAll(
    "ErrorControl[WriterT[EitherT[Option, String, ?], Int, ?], WriterT[Option, Int, ?], String]",
    SerializableTests.serializable(
      ErrorControl[WriterT[EitherT[Option, String, ?], Int, ?], WriterT[Option, Int, ?], String])
  )

}

object ErrorControlInstancesTests extends LowPriority {
  implicit def stateEq[S: Eq: Arbitrary, A: Eq]: Eq[State[S, A]] =
    indexedStateTEq[Eval, S, S, A]

}

trait LowPriority {
  implicit def indexedStateTEq[F[_], SA, SB, A](implicit SA: Arbitrary[SA],
                                                FSB: Eq[F[(SB, A)]],
                                                F: FlatMap[F]): Eq[IndexedStateT[F, SA, SB, A]] =
    Eq.by[IndexedStateT[F, SA, SB, A], SA => F[(SB, A)]](state => s => state.run(s))
}

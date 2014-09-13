package com.dominikgruber.fpinscala.chapter12

import com.dominikgruber.fpinscala.chapter04.{Either, Left, Right}
import com.dominikgruber.fpinscala.chapter06.State

trait Monad[F[_]] extends Applicative[F] {
  def flatMap[A,B](fa: F[A])(f: A => F[B]): F[B] = join(map(fa)(f))

  def join[A](ffa: F[F[A]]): F[A] = flatMap(ffa)(fa => fa)

  def compose[A,B,C](f: A => F[B], g: B => F[C]): A => F[C] =
    a => flatMap(f(a))(g)

  override def map2[A,B,C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] =
    flatMap(fa)(a => map(fb)(b => f(a,b)))
}

object Monad {

  /**
   * Exercise 05
   * Write a monad instance for Either.
   */
  def eitherMonad[E] = new Monad[({type f[x] = Either[E, x]})#f] {

    def unit[A](a: => A): Either[E, A] = Right(a)

    /*
     * "A minimal implementation of Monad must implement unit and override
     * either flatMap or join and map."
     */
    override def flatMap[A,B](fa: Either[E, A])(f: A => Either[E, B]): Either[E, B] = fa match {
      case Right(a) => f(a)
      case Left(e) => Left(e)
    }
  }

  // From Chapter 11
  def stateMonad[S] = new Monad[({type f[x] = State[S,x]})#f] {
    def unit[A](a: => A): State[S,A] = State(s => (a, s))
    override def flatMap[A,B](st: State[S,A])(f: A => State[S,B]): State[S,B] =
      st flatMap f
  }
}
package com.itforelead.smspaltfrom.effects

import cats.effect.Temporal
import org.typelevel.log4cats.Logger
import retry.RetryDetails._
import retry._

trait Retry[F[_]] {
  def retry[A](policy: RetryPolicy[F])(fa: F[A]): F[A]
}

object Retry {
  def apply[F[_]: Retry]: Retry[F] = implicitly

  implicit def forLoggerTemporal[F[_]: Logger: Temporal]: Retry[F] =
    new Retry[F] {
      def retry[A](policy: RetryPolicy[F])(fa: F[A]): F[A] = {
        def onError(e: Throwable, details: RetryDetails): F[Unit] =
          details match {
            case WillDelayAndRetry(_, retriesSoFar, _) =>
              Logger[F].warn(
                s"Failed to process send email with ${e.getMessage}. So far we have retried $retriesSoFar times."
              )
            case GivingUp(totalRetries, _) =>
              Logger[F].warn(s"Giving up on send email after $totalRetries retries.")
          }

        retryingOnAllErrors[A](policy, onError)(fa)
      }
    }
}

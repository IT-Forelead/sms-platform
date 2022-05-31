package smsplatform.utils

import cats.effect.{IO, Resource}
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import weaver.SimpleIOSuite
import weaver.scalacheck.Checkers

trait DBSuite extends SimpleIOSuite with Checkers {
  lazy val imageName: String = "postgres:12"
  lazy val container: PostgreSQLContainer[Nothing] = new PostgreSQLContainer(
    DockerImageName
      .parse(imageName)
      .asCompatibleSubstituteFor("postgres")
  )
  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def databaseResource: Resource[IO, PostgreSQLContainer[Nothing]] =
    for {
      container <- Resource.fromAutoCloseable {
        IO {
          container.start()
          container
        }
      }
      _ <- Resource.eval(logger.info("Container has started"))
    } yield container
}

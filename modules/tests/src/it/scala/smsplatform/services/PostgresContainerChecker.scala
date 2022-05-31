package smsplatform.services

import cats.effect.IO
import smsplatform.utils.DBSuite

object PostgresContainerChecker extends DBSuite {

  test("Check is Running.") {
    databaseResource.use(a => IO(assert(a.isRunning)))
  }
}

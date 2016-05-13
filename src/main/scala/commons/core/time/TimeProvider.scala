package commons.core.time

import java.time.{Instant, LocalDate}

trait TimeProvider {

  def localDate: LocalDate

  def now: Instant

  def millis: Long
}

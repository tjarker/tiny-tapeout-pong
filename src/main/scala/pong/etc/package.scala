package pong

package object etc {
  implicit class IntHertz(val value: Int) extends AnyVal {
    def Hz: Hertz = Hertz(value)
    def kHz: Hertz = Hertz(value * 1000)
    def MHz: Hertz = Hertz(value * 1000000)
  }
}

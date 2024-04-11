package pong.etc

case class Hertz(value: Int) {

  def /(that: Hertz): Int = (value.toDouble / that.value).round.toInt
}

object Hertz {
  def apply(value: Int): Hertz = new Hertz(value)

  implicit class IntHertz(val value: Int) extends AnyVal {
    def Hz: Hertz = Hertz(value)
    def kHz: Hertz = Hertz(value * 1000)
    def MHz: Hertz = Hertz(value * 1000000)
  }
}

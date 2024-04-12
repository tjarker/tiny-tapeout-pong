package pong.etc

case class Hertz(value: Int) {

  def /(that: Hertz): Int = (value.toDouble / that.value).round.toInt
}

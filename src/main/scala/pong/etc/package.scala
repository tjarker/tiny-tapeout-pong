package pong

import chisel3._

package object etc {
  implicit class IntHertz(val value: Int) extends AnyVal {
    def Hz: Hertz = Hertz(value)
    def kHz: Hertz = Hertz(value * 1000)
    def MHz: Hertz = Hertz(value * 1000000)
  }

  def rising(edge: Bool): Bool = edge && !RegNext(edge)

  implicit class rangeCheck(x: UInt) {
    def inRange(min: UInt, max: UInt): Bool = x >= min && x < max
  }
}

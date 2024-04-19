package pong

import chisel3._
import chisel3.util.Cat

package object etc {
  implicit class IntHertz(val value: Int) extends AnyVal {
    def Hz: Hertz = Hertz(value)
    def kHz: Hertz = Hertz(value * 1000)
    def MHz: Hertz = Hertz(value * 1000000)
  }

  def rising(edge: Bool): Bool = edge && !RegNext(edge)

  implicit class rangeCheck[T <: Data with Num[T]](x: T) {
    def inRange(min: T, max: T): Bool = x >= min && x < max
  }

  implicit class MsbAdder(x: UInt) {
    def addMsb: UInt = {
      Cat(0.B, x)
    }
  }
}

package pong.etc

import chisel3._
import chisel3.util._
object TickGen {

  def apply(freq: Hertz, ena: Bool = 1.B)(implicit sysFreq: Hertz): Bool = {

    val period = sysFreq / freq

    val tickReg = RegInit(0.U(log2Ceil(period).W))

    val tick = ena && tickReg === (period - 1).U
    tickReg := Mux(tick, 0.U, tickReg + 1.U)

    tick
  }

}

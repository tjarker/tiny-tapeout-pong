package pong.etc

import chisel3._
import chisel3.util._

class Debouncer extends Module {

  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())

    val tick = Input(Bool())
  })

  val reg = RegInit(0.U(2.W))

  when(io.tick) {
    reg := Cat(io.in, reg(1))
  }

  io.out := reg.andR

}

object Debouncer {
  def apply(in: Bool, tick: Bool): Bool = {
    val debouncer = Module(new Debouncer)
    debouncer.io.in := in
    debouncer.io.tick := tick
    debouncer.io.out
  }
}

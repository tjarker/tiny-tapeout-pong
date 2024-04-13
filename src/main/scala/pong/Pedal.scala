package pong

import chisel3._
import chisel3.util.log2Ceil
import pong.vga.Resolution
import pong.etc._

class Pedal(res: Resolution, xRange: Range) extends Module {

  val io = IO(new Bundle {
    val up = Input(Bool())
    val down = Input(Bool())
    val pxlPos = Input(Vec2D(UInt(log2Ceil(res.width).W)))
    val rgb = Output(Color(2.W))
    val active = Output(Bool())

    val gameTick = Input(Bool())
  })

  val speed = 2

  val pos = RegInit((res.height / 2).U(log2Ceil(res.height).W))

  when(io.gameTick) {
    when(io.up && pos > 0.U) {
      pos := pos - speed.U
    }
    when(io.down && pos < res.height.U) {
      pos := pos + speed.U
    }
  }

  val active = io.pxlPos.x.inRange(xRange.start.U, xRange.end.U) &&
    io.pxlPos.y.inRange(pos - 30.U, pos + 30.U)

  val black = Color(2.W, 0.U, 0.U, 0.U)

  io.rgb := Mux(active, Color(2.W, 0.U, 3.U, 0.U), black)
  io.active := active

}

package pong

import chisel3._
import pong.etc.{Color, TickGen, Vec2D}
import pong.etc.Hertz.IntHertz
import vga._
class Pong extends Module {

  val io = IO(new Bundle {
    val hSync = Output(Bool())
    val vSync = Output(Bool())
    val rgb = Output(Color(2.W))
  })

  val vgaTimer = Module(new VgaTimer(Resolution.VGA640x480, 100 MHz))

  val ball = Module(new Ball)
  ball.io.pxlPos := Vec2D(vgaTimer.io.x, vgaTimer.io.y)
  ball.io.gameTick := TickGen((100 MHz) / (10 Hz))

  io.hSync := vgaTimer.io.hSync
  io.vSync := vgaTimer.io.vSync

  io.rgb := ball.io.rgb

}

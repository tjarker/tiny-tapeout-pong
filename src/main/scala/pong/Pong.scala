package pong

import chisel3._
import pong.etc._
import vga._

class Pong()(implicit sysFreq: Hertz) extends Module {

  val io = IO(new Bundle {
    val hSync = Output(Bool())
    val vSync = Output(Bool())
    val rgb = Output(Color(2.W))
  })

  val vgaTimer = Module(new VgaTimer(Resolution.VGA640x480))

  val ball = Module(new Ball)
  ball.io.pxlPos := Vec2D(vgaTimer.io.x, vgaTimer.io.y)
  ball.io.gameTick := rising(vgaTimer.io.vSync)

  io.hSync := vgaTimer.io.hSync
  io.vSync := vgaTimer.io.vSync

  io.rgb := ball.io.rgb

}

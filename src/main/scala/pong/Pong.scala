package pong

import chisel3._
import chisel3.util._
import pong.etc._
import vga._

class Pong(res: Resolution)(implicit sysFreq: Hertz) extends Module {

  val io = IO(new Bundle {
    val hSync = Output(Bool())
    val vSync = Output(Bool())
    val rgb = Output(Color(2.W))

    val btn = Input(UInt(4.W))
  })

  val vgaTimer = Module(new VgaTimer(res))

  val tick = rising(vgaTimer.io.vSync)

  val btn = io.btn.asBools.map(Debouncer(_, tick))

  val pedal0 = Module(new Pedal(res, 0 until 10))
  pedal0.io.pxlPos := Vec2D(vgaTimer.io.x, vgaTimer.io.y)
  pedal0.io.gameTick := tick
  pedal0.io.up := btn(0)
  pedal0.io.down := btn(1)

  val pedal1 = Module(new Pedal(res, res.width - 10 until res.width))
  pedal1.io.pxlPos := Vec2D(vgaTimer.io.x, vgaTimer.io.y)
  pedal1.io.gameTick := tick
  pedal1.io.up := btn(2)
  pedal1.io.down := btn(3)

  val ball = Module(new Ball(5, res))
  ball.io.pxlPos := Vec2D(vgaTimer.io.x, vgaTimer.io.y)
  ball.io.gameTick := tick

  io.hSync := vgaTimer.io.hSync
  io.vSync := vgaTimer.io.vSync

  io.rgb := PriorityMux(
    Seq(
      pedal0.io.active -> pedal0.io.rgb,
      pedal1.io.active -> pedal1.io.rgb,
      ball.io.active -> ball.io.rgb
    )
  )

}

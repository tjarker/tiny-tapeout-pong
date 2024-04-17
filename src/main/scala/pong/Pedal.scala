package pong

import chisel3._
import chisel3.util.log2Ceil
import pong.etc._
import pong.vga.Resolution

object Pedal {
  trait Side
  case object Left extends Side
  case object Right extends Side

  case class Size(width: Int, height: Int)
}

class Pedal(res: Resolution, size: Pedal.Size, side: Pedal.Side)
    extends GameObject(res) {

  val io = IO(new Bundle {
    val up = Input(Bool())
    val down = Input(Bool())
    val pos = Output(UInt(log2Ceil(res.height).W))
  })

  val speed = 3

  val pos = RegInit(
    (res.height / 2 - size.height / 2).U(log2Ceil(res.height).W)
  )

  when(gameIO.tick) {
    when(io.up && pos > 0.U) {
      pos := pos - speed.U
    }.elsewhen(io.down && pos < (res.height - size.height).U) {
      pos := pos + speed.U
    }
  }

  val xRange = side match {
    case Pedal.Left  => 0 until size.width
    case Pedal.Right => res.width - size.width until res.width
  }

  val active =
    gameIO.rendering.pxlPos.x.inRange(xRange.start.U, xRange.end.U) &&
      gameIO.rendering.pxlPos.y.inRange(pos, pos + size.height.U)

  gameIO.rendering.color := Color.green
  gameIO.rendering.active := active
  io.pos := pos

}

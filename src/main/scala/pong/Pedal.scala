package pong

import chisel3._
import chisel3.util.{MuxCase, log2Ceil}
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

    val ballPos = Input(Vec2D(UInt(log2Ceil(res.width).W)))
    val ballVel = Input(Vec2D(SInt(5.W)))
    val force = Input(Bool())
  })

  val speed = 4

  val pos = RegInit(
    (res.height / 2 - size.height / 2).U(log2Ceil(res.height).W)
  )

  when(gameIO.tick) {
    when(io.force) {

      when(side match {
        case Pedal.Left =>
          io.ballPos.x < ((res.width * 0.5).toInt).U && io.ballVel.x(4)
        case Pedal.Right =>
          io.ballPos.x > ((res.width * 0.5).toInt).U && !io.ballVel.x(4)
      }) {

        val sPos = (0.B ## pos).asSInt
        val sBall = (0.B ## io.ballPos.y).asSInt

        val err = (sPos + (size.height / 2).S) - sBall

        val correction = MuxCase(
          0.S,
          Seq(
            (err > speed.S) -> -speed.S,
            (err > 0.S) -> -1.S,
            (err < -speed.S) -> speed.S,
            (err < 0.S) -> 1.S
          )
        )

        val newPos = sPos + correction

        pos := MuxCase(
          newPos.asUInt,
          Seq(
            (newPos < 0.S) -> 0.U,
            (newPos > (res.height - size.height).S) -> (res.height - size.height).U
          )
        )
      }

    }.elsewhen(io.up && pos >= speed.U) {
      pos := pos - speed.U
    }.elsewhen(io.down && pos < (res.height - size.height - speed).U) {
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

  gameIO.rendering.color := Mux(io.force, Color.blue, Color.green)
  gameIO.rendering.active := active
  io.pos := pos

}

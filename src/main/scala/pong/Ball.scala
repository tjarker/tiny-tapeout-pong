package pong

import chisel3._
import pong.etc._

class Ball extends Module {

  val io = IO(new Bundle {
    val pxlPos = Input(Vec2D(10.W))
    val rgb = Output(Color(2.W))
    val active = Output(Bool())

    val gameTick = Input(Bool())
  })

  val ballPosx = RegInit((640 * 4 / 2).U(13.W))
  val ballPosy = RegInit((480 * 4 / 2).U(13.W))

  val ballVelx = RegInit(3.S(5.W))
  val ballVely = RegInit(2.S(5.W))

  val ballRadius = 5

  val x = ballPosx(12, 2)
  val y = ballPosy(12, 2)

  when(io.gameTick) {
    when(
      ballPosx < (4 * ballRadius).U || ballPosx > (4 * 640 - 1 - 4 * ballRadius).U
    ) {
      ballVelx := -ballVelx
      ballPosx := (ballPosx.asSInt - ballVelx).asUInt
    } otherwise {
      ballPosx := (ballPosx.asSInt + ballVelx).asUInt
    }
    when(
      ballPosy < (4 * ballRadius).U || ballPosy > (4 * 480 - 1 - 4 * ballRadius).U
    ) {
      ballVely := -ballVely
      ballPosy := (ballPosy.asSInt - ballVely).asUInt
    } otherwise {
      ballPosy := (ballPosy.asSInt + ballVely).asUInt
    }
  }

  val active =
    io.pxlPos.x >= (x - ballRadius.U) && io.pxlPos.x < (x + ballRadius.U) &&
      io.pxlPos.y >= (y - ballRadius.U) && io.pxlPos.y < (y + ballRadius.U)

  val black = Color(2.W, 0.U, 0.U, 0.U)

  io.rgb := Mux(active, Color(2.W, 3.U, 3.U, 3.U), black)
  io.active := active
}

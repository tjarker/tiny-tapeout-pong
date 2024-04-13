package pong

import chisel3._
import chisel3.util._
import pong.etc._
import pong.vga.Resolution

class Ball(rad: Int, res: Resolution) extends Module {

  val io = IO(new Bundle {
    val pxlPos = Input(Vec2D(UInt(log2Ceil(res.width).W)))
    val rgb = Output(Color(2.W))
    val active = Output(Bool())

    val gameTick = Input(Bool())
  })

  val posReg = RegInit(
    Vec2D(
      (res.width / 2).U(log2Ceil(res.width).W),
      (res.height / 2).U(log2Ceil(res.height).W)
    )
  )
  val velReg = RegInit(
    Vec2D(
      3.S(5.W),
      2.S(5.W)
    )
  )

  when(io.gameTick) {
    when(!posReg.x.inRange(rad.U, (res.width - rad).U)) {
      velReg.x := -velReg.x
      posReg.x := (posReg.x.asSInt - velReg.x).asUInt
    } otherwise {
      posReg.x := (posReg.x.asSInt + velReg.x).asUInt
    }
    when(!posReg.y.inRange(rad.U, (res.height - rad).U)) {
      velReg.y := -velReg.y
      posReg.y := (posReg.y.asSInt - velReg.y).asUInt
    } otherwise {
      posReg.y := (posReg.y.asSInt + velReg.y).asUInt
    }
  }

  val active =
    io.pxlPos.x.inRange(posReg.x - rad.U, posReg.x + rad.U) &&
      io.pxlPos.y.inRange(posReg.y - rad.U, posReg.y + rad.U)

  val black = Color(2.W, 0.U, 0.U, 0.U)
  val cyan = Color(2.W, 2.U, 3.U, 3.U)
  val red = Color(2.W, 3.U, 0.U, 0.U)

  val lost = io.pxlPos.x.inRange(0.U, 10.U) ||
    io.pxlPos.x.inRange((res.width - 10).U, res.width.U)

  val activeColor = Mux(lost, red, cyan)
  io.rgb := Mux(active, activeColor, black)
  io.active := active
}

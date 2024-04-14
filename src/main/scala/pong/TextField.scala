package pong

import chisel3._
import chisel3.util.log2Ceil
import pong.etc.{Color, FontRom, Vec2D, rangeCheck}
import pong.vga.Resolution

class TextField(res: Resolution, x: Int, y: Int, prefix: String)
    extends Module {

  val io = IO(new Bundle {
    val pxlPos = Input(Vec2D(UInt(log2Ceil(res.width).W)))
    val rgb = Output(Color(2.W))
    val active = Output(Bool())

    val up = Input(Bool())
  })

  val cnt = RegInit('0'.U(8.W))

  when(io.up) {
    cnt := cnt + 1.U
  }

    val offset = Vec2D(
    io.pxlPos.x - x.U,
    io.pxlPos.y - y.U
  )

  val pxlWidth = (prefix.length + 1) * 8

  val active = io.pxlPos.x.inRange(x.U, (x + pxlWidth).U) && io.pxlPos.y
    .inRange(y.U, (y + 16).U)

  println(prefix.toCharArray.map(_.U(8.W)).mkString(","))

  val characters = VecInit(prefix.toCharArray.map(_.U(8.W)) :+ cnt)

  val char = characters((offset.x >> 3).asUInt)

  val font = FontRom()

  val charBit = font(char ## offset.y(3,0))(offset.x(2, 0))

  val black = Color(2.W, 0.U, 0.U, 0.U)
  val white = Color(2.W, 3.U, 3.U, 3.U)

  io.rgb := Mux(charBit, white, black)
  io.active := active

}

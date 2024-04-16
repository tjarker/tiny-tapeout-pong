package pong

import chisel3._
import chisel3.util.log2Ceil
import pong.etc.{BcdRom, Color, FontRom, Vec2D, rangeCheck}
import pong.vga.Resolution

class TextField(res: Resolution, x_center: Int, y: Int) extends Module {

  val io = IO(new Bundle {
    val pxlPos = Input(Vec2D(UInt(log2Ceil(res.width).W)))
    val rgb = Output(Color())
    val active = Output(Bool())

    val up = Input(Vec(2, Bool()))
  })

  val cnt = RegInit(VecInit(0.U(8.W), 0.U(8.W)))

  io.up.zip(cnt).foreach { case (up, cnt) =>
    when(up) {
      cnt := cnt + 1.U
    }
  }

  val bcdIn = Mux(io.pxlPos.x > x_center.U, cnt(1), cnt(0))

  val bcdOut = BcdRom()(bcdIn)

  val characters =
    VecInit(
      bcdOut(7, 4),
      bcdOut(3, 0),
      FontRom.SPACE.U,
      FontRom.HYPHEN.U,
      FontRom.SPACE.U,
      bcdOut(7, 4),
      bcdOut(3, 0)
    )

  val pxlWidth = characters.length * 8

  val x = x_center - (pxlWidth / 2)

  val offset = Vec2D(
    io.pxlPos.x - x.U,
    io.pxlPos.y - y.U
  )

  val active = io.pxlPos.x.inRange(x.U, (x + pxlWidth).U) && io.pxlPos.y
    .inRange(y.U, (y + 16).U)

  val char = characters((offset.x >> 3).asUInt)

  val font = FontRom()

  val charBit = font(char)(offset.y(3, 0))(offset.x(2, 0))

  io.rgb := Mux(charBit, Color.white, Color.black)
  io.active := active

}

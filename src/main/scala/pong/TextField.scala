package pong

import chisel3._
import pong.etc.{BcdRom, Color, FontRom, Vec2D, rangeCheck}
import pong.vga.Resolution

class TextField(res: Resolution, xCenter: Int, y: Int) extends GameObject(res) {

  val io = IO(new Bundle {
    val increaseScore = Input(Vec(2, Bool()))
  })

  val scores = RegInit(VecInit(0.U(8.W), 0.U(8.W)))

  io.increaseScore.zip(scores).foreach { case (up, cnt) =>
    when(up) {
      cnt := cnt + 1.U
    }
  }

  val bcdIn = Mux(gameIO.rendering.pxlPos.x > xCenter.U, scores(1), scores(0))

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

  val x = xCenter - (pxlWidth / 2)

  val offset = Vec2D(
    gameIO.rendering.pxlPos.x - x.U,
    gameIO.rendering.pxlPos.y - y.U
  )

  val active = gameIO.rendering.pxlPos.x.inRange(x.U, (x + pxlWidth).U) &&
    gameIO.rendering.pxlPos.y.inRange(y.U, (y + 16).U)

  val char = WireDefault(characters((offset.x >> 3).asUInt))

  val font = FontRom()

  val charBit = font(char)(offset.y(3, 0))(offset.x(2, 0))

  gameIO.rendering.color := Mux(charBit, Color.white, Color.black)
  gameIO.rendering.active := active

}

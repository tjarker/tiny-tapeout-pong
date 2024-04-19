package pong

import chisel3._
import chisel3.util._
import pong.etc._
import pong.vga.Resolution

class Ball(res: Resolution, pedalSize: Pedal.Size) extends GameObject(res) {

  val io = IO(new Bundle {
    val leftPedalPos = Input(UInt(9.W))
    val rightPedalPos = Input(UInt(9.W))
    val lost = Output(Vec(2, Bool()))

    val pos = Output(Vec2D(UInt(log2Ceil(res.width).W)))
    val vel = Output(Vec2D(SInt(5.W)))
  })

  val size = 11

  val sprite = BallBitmap(size, size)

  val lfsr = chisel3.util.random.LFSR(4, gameIO.newGame)

  val speeds =
    VecInit(-3.S(5.W), -2.S(5.W), 2.S(5.W), 3.S(5.W))

  val posReg = RegInit(
    Vec2D(
      (res.width / 2 - size / 2).U(log2Ceil(res.width).W),
      (res.height / 2 - size / 2).U(log2Ceil(res.height).W)
    )
  )
  val velReg = RegInit(
    Vec2D(
      3.S(5.W),
      2.S(5.W)
    )
  )

  when(gameIO.tick) {

    when(
      posReg.x < pedalSize.width.U && posReg.y.addMsb.asSInt.inRange(
        io.leftPedalPos.addMsb.asSInt - (size / 2).S,
        io.leftPedalPos.addMsb.asSInt + (pedalSize.height - size / 2).S
      )
    ) {
      velReg.x := -velReg.x
      posReg.x := pedalSize.width.U
    }.elsewhen(
      posReg.x > (res.width - 1 - pedalSize.width - size).U && posReg.y.addMsb.asSInt
        .inRange(
          io.rightPedalPos.addMsb.asSInt - (size / 2).S,
          io.rightPedalPos.addMsb.asSInt + (pedalSize.height - size / 2).S
        )
    ) {
      velReg.x := -velReg.x
      posReg.x := (res.width - 1 - pedalSize.width - size).U
    }.otherwise {
      posReg.x := (posReg.x.asSInt + velReg.x).asUInt
    }

    when(!posReg.y.inRange(2.U, (res.height - size - 2).U)) {
      velReg.y := -velReg.y
      posReg.y := (posReg.y.asSInt - velReg.y).asUInt
    }.otherwise {
      posReg.y := (posReg.y.asSInt + velReg.y).asUInt
    }
  }

  val active =
    gameIO.rendering.pxlPos.x.inRange(posReg.x, posReg.x + size.U) &&
      gameIO.rendering.pxlPos.y.inRange(posReg.y, posReg.y + size.U)

  val spriteOffset = Vec2D(
    gameIO.rendering.pxlPos.x - posReg.x,
    gameIO.rendering.pxlPos.y - posReg.y
  )

  val spriteBit = sprite(spriteOffset.y)(spriteOffset.x)

  val lostLeft = posReg.x < 3.U
  val lostRight = posReg.x > (res.width - 1 - size).U

  val activeColor = Mux(lostLeft || lostRight, Color.red, Color.cyan)
  gameIO.rendering.color := Mux(spriteBit, activeColor, Color.black)
  gameIO.rendering.active := active
  io.lost := VecInit(lostLeft, lostRight)

  io.pos := posReg
  io.vel := velReg

  when(gameIO.newGame) {
    posReg := Vec2D(
      (res.width / 2 + size / 2).U(log2Ceil(res.width).W),
      (res.height / 2 + size / 2).U(log2Ceil(res.height).W)
    )
    velReg := Vec2D(
      speeds(lfsr(3, 2)),
      speeds(lfsr(1, 0))
    )
  }
}

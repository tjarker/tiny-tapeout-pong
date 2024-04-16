package pong

import chisel3._
import chisel3.util._
import pong.etc._
import pong.vga.Resolution

class Ball(res: Resolution) extends Module {

  val io = IO(new Bundle {
    val pxlPos = Input(Vec2D(UInt(log2Ceil(res.width).W)))
    val rgb = Output(Color())
    val active = Output(Bool())

    val gameTick = Input(Bool())

    val leftPedal = Input(UInt(9.W))
    val rightPedal = Input(UInt(9.W))

    val newGame = Input(Bool())
    val run = Input(Bool())
    val lost = Output(Vec(2, Bool()))
  })

  val size = 11

  val sprite = BallBitmap(size, size)

  val lfsr = chisel3.util.random.LFSR(4, io.newGame)

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

  when(io.gameTick && io.run) {

    when(
      posReg.x <= 10.U && posReg.y.inRange(
        io.leftPedal - (size / 2).U,
        io.leftPedal + (60 - size / 2).U
      )
    ) {
      velReg.x := -velReg.x
      posReg.x := (posReg.x.asSInt - velReg.x).asUInt
    }.elsewhen(
      posReg.x >= (res.width - 1 - 10 - size).U && posReg.y.inRange(
        io.rightPedal - (size / 2).U,
        io.rightPedal + (60 - size / 2).U
      )
    ) {
      velReg.x := -velReg.x
      posReg.x := (posReg.x.asSInt - velReg.x).asUInt
    }.otherwise {
      posReg.x := (posReg.x.asSInt + velReg.x).asUInt
    }

    when(!posReg.y.inRange(0.U, (res.height - size).U)) {
      velReg.y := -velReg.y
      posReg.y := (posReg.y.asSInt - velReg.y).asUInt
    }.otherwise {
      posReg.y := (posReg.y.asSInt + velReg.y).asUInt
    }
  }

  val active =
    io.pxlPos.x.inRange(posReg.x, posReg.x + size.U) &&
      io.pxlPos.y.inRange(posReg.y, posReg.y + size.U)

  val spriteOffset = Vec2D(
    io.pxlPos.x - posReg.x,
    io.pxlPos.y - posReg.y
  )

  val spriteBit = sprite(spriteOffset.y)(spriteOffset.x)

  val lostLeft = posReg.x < 4.U
  val lostRight = posReg.x > (res.width - 1 - size).U

  val activeColor = Mux(lostLeft || lostRight, Color.red, Color.cyan)
  io.rgb := Mux(spriteBit, activeColor, Color.black)
  io.active := active
  io.lost := VecInit(lostLeft, lostRight)

  when(io.newGame) {
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

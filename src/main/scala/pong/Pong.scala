package pong

import chisel3._
import chisel3.util._
import pong.etc._
import pong.vga._

class Pong(res: Resolution)(implicit sysFreq: Hertz) extends Module {

  val io = IO(new Bundle {
    val hSync = Output(Bool())
    val vSync = Output(Bool())
    val rgb = Output(Color())

    val btn = Input(UInt(4.W))

    val autopilot = Input(Vec(2, Bool()))

    val ena = Input(Bool())

    val debug = Output(UInt(8.W))
  })

  val pedalSize = Pedal.Size(10, 60)

  object State extends ChiselEnum {
    val Waiting, Playing, GameOver, GameOverWaitRelease, NewGame = Value
  }

  val stateReg = RegInit(State.Waiting)

  val vgaTimer = Module(new VgaTimer(res))
  vgaTimer.io.ena := io.ena
  val tick = rising(vgaTimer.io.vSync)

  val btn = io.btn.asBools.map(Debouncer(_, tick))

  val pedalBtn = btn.map(b => b && tick)

  val leftPedal = Module(new Pedal(res, pedalSize, Pedal.Left))
  leftPedal.io.up := pedalBtn(0)
  leftPedal.io.down := pedalBtn(1)

  val rightPedal = Module(new Pedal(res, pedalSize, Pedal.Right))
  rightPedal.io.up := pedalBtn(2)
  rightPedal.io.down := pedalBtn(3)

  val ball = Module(new Ball(res, pedalSize))
  ball.io.leftPedalPos := leftPedal.io.pos
  ball.io.rightPedalPos := rightPedal.io.pos

  leftPedal.io.ballPos := ball.io.pos
  rightPedal.io.ballPos := ball.io.pos
  leftPedal.io.ballVel := ball.io.vel
  rightPedal.io.ballVel := ball.io.vel
  leftPedal.io.force := io.autopilot(0)
  rightPedal.io.force := io.autopilot(1)

  val score = Module(new TextField(res, res.width / 2, 0))
  score.io.increaseScore := VecInit(
    ball.io.lost.map(lost => !lost && stateReg === State.GameOver)
  )

  val gameObjs = Seq(leftPedal, rightPedal, ball, score)

  gameObjs.foreach(obj => obj.gameIO.tick := tick && stateReg === State.Playing)
  gameObjs.foreach(obj => obj.gameIO.newGame := stateReg === State.NewGame)
  gameObjs.foreach(obj => obj.gameIO.rendering.pxlPos := vgaTimer.io.pos)

  io.hSync := vgaTimer.io.hSync
  io.vSync := vgaTimer.io.vSync
  io.rgb := PriorityMux(
    gameObjs.map(obj =>
      obj.gameIO.rendering.active -> obj.gameIO.rendering.color
    ) :+ (1.B -> Color.black)
  )

  switch(stateReg) {
    is(State.Waiting) {
      when(btn.reduce(_ || _) && io.ena) {
        stateReg := State.Playing
      }
    }
    is(State.Playing) {
      when(ball.io.lost.reduce(_ || _)) {
        stateReg := State.GameOver
      }
    }
    is(State.GameOver) {
      stateReg := State.GameOverWaitRelease
    }
    is(State.GameOverWaitRelease) {
      when(!btn.reduce(_ || _)) {
        stateReg := State.NewGame
      }
    }
    is(State.NewGame) {
      stateReg := State.Waiting
    }
  }

  io.debug := Cat(
    tick,
    ball.io.lost(1),
    ball.io.lost(0),
    vgaTimer.io.hSync,
    vgaTimer.io.vSync,
    stateReg.asUInt.apply(2, 0)
  )
}

package pong.vga

import chisel3._
import chisel3.util._
import pong.etc._

class VgaTimer(res: Resolution)(implicit sysFreq: Hertz) extends Module {

  val io = IO(new Bundle {

    val ena = Input(Bool())

    val hSync = Output(Bool())
    val vSync = Output(Bool())
    val drawing = Output(Bool())
    val pos = Output(Vec2D(UInt(log2Ceil(res.width).W)))
  })

  // generate pixel ticks
  val pxlTick = TickGen(res.pxlClk, io.ena)

  // pixel counters
  val (xReg, hWrap) = Counter(pxlTick, res.line)
  val (yReg, yWrap) = Counter(hWrap, res.frame)

  // connect IO
  io.vSync := !((yReg >= res.vSyncStart.U) && (yReg < res.vSyncEnd.U))
  io.hSync := !((xReg >= res.hSyncStart.U) && (xReg < res.hSyncEnd.U))
  io.pos.x := xReg
  io.pos.y := yReg
  io.drawing := yReg < res.v.active.U
}

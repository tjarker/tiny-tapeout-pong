package pong.vga

import chisel3._
import chisel3.util._
import pong.etc.{Hertz, TickGen}
class VgaTimer(res: Resolution)(implicit sysFreq: Hertz) extends Module {

  val io = IO(new Bundle {
    val hSync = Output(Bool())
    val vSync = Output(Bool())
    val drawing = Output(Bool())
    val x = Output(UInt(log2Ceil(res.h.active).W))
    val y = Output(UInt(log2Ceil(res.v.active).W))
  })

  // generate pixel ticks
  val pxlTick = TickGen(res.pxlClk)

  // pixel counters
  val (xReg, hWrap) = Counter(pxlTick, res.line)
  val (yReg, yWrap) = Counter(hWrap, res.frame)

  // connect IO
  io.vSync := (yReg >= res.vSyncStart.U) && (yReg < res.vSyncEnd.U)
  io.hSync := (xReg >= res.hSyncStart.U) && (xReg < res.hSyncEnd.U)
  io.x := xReg
  io.y := yReg
  io.drawing := yReg < res.v.active.U
}

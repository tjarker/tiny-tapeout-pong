package pong

import chisel3._
import chisel3.util._
import pong.etc.{Color, Vec2D}
import pong.vga.Resolution

class RenderPort(res: Resolution) extends Bundle {

  val pxlPos = Input(Vec2D(UInt(log2Ceil(res.width).W)))
  val color = Output(Color())
  val active = Output(Bool())

}

package pong.etc

import chisel3._
import chisel3.internal.firrtl.Width

class Vec2D(w: Width) extends Bundle {
  val x = UInt(w)
  val y = UInt(w)

  def +(that: Vec2D): Vec2D = Vec2D(x + that.x, y + that.y)
}

object Vec2D {
  def apply(w: Width): Vec2D = new Vec2D(w)
  def apply(x: UInt, y: UInt): Vec2D = {
    val v = Wire(new Vec2D(Seq(x,y).map(_.getWidth).max.W))
    v.x := x
    v.y := y
    v
  }
}

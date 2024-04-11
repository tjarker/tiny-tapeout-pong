package pong.etc

import chisel3._

class Vec2D extends Bundle {
  val x = UInt(10.W)
  val y = UInt(10.W)

  def +(that: Vec2D): Vec2D = Vec2D(x + that.x, y + that.y)
}

object Vec2D {
  def apply(): Vec2D = new Vec2D
  def apply(x: UInt, y: UInt): Vec2D = {
    val position = Wire(new Vec2D)
    position.x := x
    position.y := y
    position
  }
}

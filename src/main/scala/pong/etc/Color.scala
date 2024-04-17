package pong.etc

import chisel3._
import chisel3.experimental.BundleLiterals.AddBundleLiteralConstructor

class Color extends Bundle {
  val r = UInt(2.W)
  val g = UInt(2.W)
  val b = UInt(2.W)
}

object Color {
  def apply(): Color = new Color()

  def apply(r: UInt, g: UInt, b: UInt): Color = {
    val color = new Color().Lit(
      _.r -> r,
      _.g -> g,
      _.b -> b
    )
    color
  }

  val black = Color(0.U, 0.U, 0.U)
  val white = Color(3.U, 3.U, 3.U)
  val red = Color(3.U, 0.U, 0.U)
  val green = Color(0.U, 3.U, 0.U)
  val cyan = Color(2.U, 3.U, 3.U)
  val blue = Color(0.U, 0.U, 3.U)
}

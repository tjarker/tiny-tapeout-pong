package pong.etc

import chisel3._
import chisel3.experimental.BundleLiterals.AddBundleLiteralConstructor
import chisel3.internal.firrtl.Width

class Color(bits: Width) extends Bundle {
  val r = UInt(bits)
  val g = UInt(bits)
  val b = UInt(bits)
}

object Color {
  def apply(bits: Width): Color = new Color(bits)

  def apply(w: Width, r: UInt, g: UInt, b: UInt): Color = {
    val color = new Color(w).Lit(
      _.r -> r,
      _.g -> g,
      _.b -> b
    )
    color
  }
}

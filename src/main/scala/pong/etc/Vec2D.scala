package pong.etc

import chisel3._
import chisel3.experimental.BundleLiterals.AddBundleLiteralConstructor

class Vec2D[T <: Data with Num[T]](t: => T) extends Bundle {
  val x = t
  val y = t

  def +(that: Vec2D[T]): Vec2D[T] = Vec2D(x + that.x, y + that.y)
}

object Vec2D {
  def apply[T <: Data with Num[T]](t: => T): Vec2D[T] = new Vec2D(t)
  def apply[T <: Data with Num[T]](x: T, y: T): Vec2D[T] = {

    val t = if (x.getWidth > y.getWidth) x else y

    if (x.isLit && y.isLit) {
      Vec2D(chiselTypeOf(t)).Lit(_.x -> x, _.y -> y)
    } else {
      val v = Wire(Vec2D(chiselTypeOf(t)))
      v.x := x
      v.y := y
      v
    }
  }
}

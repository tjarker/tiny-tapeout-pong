package pong.etc

import chisel3._

object BallBitmap {
  def apply(width: Int, height: Int): Vec[Vec[Bool]] = {

    // Function to generate a single row of the bitmap
    def generateRow(
        y: Int,
        centerX: Int,
        centerY: Int,
        radius: Double
    ): Seq[Int] = {
      Seq.tabulate(width)(x =>
        if (
          Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math
            .pow(radius, 2)
        ) 1
        else 0
      )
    }

    // Function to generate the entire bitmap
    def generateBitmap(
        centerX: Int,
        centerY: Int,
        radius: Double
    ): Seq[Seq[Int]] = {
      Seq.tabulate(height)(y => generateRow(y, centerX, centerY, radius))
    }

    // Define the parameters of the ball
    val centerX = width / 2
    val centerY = height / 2
    val radius = (width / 2) + 0.1

    val map = generateBitmap(centerX, centerY, radius)

    println(map.map(_.mkString(", ")).mkString(",\n"))

    VecInit(
      map.map(row =>
        VecInit(
          row.map(bit => bit.B)
        )
      )
    )

  }
}

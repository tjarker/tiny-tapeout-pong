package pong.etc

import chisel3._

object BcdRom {

  def apply(): Vec[UInt] = {
    VecInit.tabulate(100) { i =>
      (((i / 10) << 4) + i % 10).U(8.W)
    }
  }

}

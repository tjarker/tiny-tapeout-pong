import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
class ChiselTopTest extends AnyFlatSpec with ChiselScalatestTester {

  "ChiselTop" should "pass" in {
    test(new ChiselTop()) { dut =>
      dut.io.ui_in.poke(1.U)
      dut.io.uio_in.poke(2.U)
      dut.io.ena.poke(true.B)
      dut.clock.step(1)
      dut.io.uo_out.expect(3.U)
      dut.io.uio_oe.expect(0.U)
    }
  }
}

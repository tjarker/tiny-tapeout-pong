import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec
class ChiselTopTest extends AnyFlatSpec with ChiselScalatestTester {

  "ChiselTop" should "pass" in {
    test(new ChiselTop()).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      dut.clock.setTimeout(0)
      dut.io.ena.poke(true.B)
      dut.clock.step(1000)
    }
  }
}

import chisel3._
import chisel3.util.Cat
import pong.Pong
import pong.etc._
import pong.vga.Resolution

/** Example design in Chisel.
  * A redesign of the Tiny Tapeout example.
  */
class ChiselTop() extends Module {
  val io = IO(new Bundle {
    val ui_in = Input(UInt(8.W)) // Dedicated inputs
    val uo_out = Output(UInt(8.W)) // Dedicated outputs
    val uio_in = Input(UInt(8.W)) // IOs: Input path
    val uio_out = Output(UInt(8.W)) // IOs: Output path
    val uio_oe =
      Output(UInt(8.W)) // IOs: Enable path (active high: 0=input, 1=output)
    val ena = Input(Bool()) // will go high when the design is enabled
  })
  /*
    input  wire [7:0] ui_in,    // Dedicated inputs
    output wire [7:0] uo_out,   // Dedicated outputs
    input  wire [7:0] uio_in,   // IOs: Input path
    output wire [7:0] uio_out,  // IOs: Output path
    output wire [7:0] uio_oe,   // IOs: Enable path (active high: 0=input, 1=output)
    input  wire       ena,      // will go high when the design is enabled
    input  wire       clk,      // clock
    input  wire       rst_n     // reset_n - low to reset
   */
  io.uio_out := 0.U
  // use bi-directionals as input
  io.uio_oe := 0.U

  io.uo_out := 0.U

  val pong = Module(new Pong(Resolution.VGA640x480)(100.MHz))
  pong.io.btn := io.ui_in(3, 0)

  io.uo_out := Cat(
    pong.io.hSync,
    pong.io.rgb.b(0),
    pong.io.rgb.g(0),
    pong.io.rgb.r(0),
    pong.io.vSync,
    pong.io.rgb.b(1),
    pong.io.rgb.g(1),
    pong.io.rgb.r(1)
  )

}

object ChiselTop extends App {
  emitVerilog(new ChiselTop(), Array("--target-dir", "src"))
}

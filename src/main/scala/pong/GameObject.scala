package pong

import chisel3._
import pong.vga.Resolution

abstract class GameObject(res: Resolution) extends Module {

  val gameIO = IO(new Bundle {
    val newGame = Input(Bool())
    val tick = Input(Bool())
    val rendering = new RenderPort(res)
  })

}

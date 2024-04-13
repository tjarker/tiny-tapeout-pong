package pong.vga

import pong.etc._

case class Timings(
    active: Int,
    frontPorch: Int,
    syncPulse: Int,
    backPorch: Int
)

case class Resolution(
    width: Int,
    height: Int,
    h: Timings,
    v: Timings,
    line: Int,
    frame: Int,
    pxlClk: Hertz // pixel clock in Hz
) {
  def hSyncStart: Int = h.active + h.frontPorch
  def hSyncEnd: Int = h.active + h.frontPorch + h.syncPulse
  def vSyncStart: Int = v.active + v.frontPorch
  def vSyncEnd: Int = v.active + v.frontPorch + v.syncPulse
}

object Resolution {

  object VGA640x480
      extends Resolution(
        640,
        480,
        Timings(640, 16, 96, 48),
        Timings(480, 10, 2, 33),
        800,
        525,
        25175000.Hz
      )

}

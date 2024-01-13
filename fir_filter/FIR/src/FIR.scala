package FIR

import chisel3._

class FirFilterGenerator(length: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(8.W))
    val valid = Input(Bool())
    val out = Output(UInt(8.W))
    val consts = Input(Vec(length, UInt(8.W)))
  })
  
  /* 
   * The following is equivalent to
   * “io.out = io.in * io.consts0 + x_1 * io.consts1 + ...”
   */
  val taps = Seq(io.in) ++ Seq.fill(io.consts.length - 1)(RegInit(0.U(8.W)))
  taps.zip(taps.tail).foreach { case (a, b) => when (io.valid) { b := a } }

  io.out := taps.zip(io.consts).map { case (a, b) => a * b }.reduce(_ + _)
}

/***
 * This module helps to print Verilog of the generated module.
 * To run this main function with mill from the terminal:
 *   mill FIR
 */ 
object FIRVerilogEmitter extends App {
  println(getVerilogString(new FirFilterGenerator(4)))
}

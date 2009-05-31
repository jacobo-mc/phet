package edu.colorado.phet.cckscala.tests


object TestRLCCircuit {
  def main(args: Array[String]) {
    var dynamicCircuit = new FullCircuit(Nil, Resistor(0, 1, 5.0) :: Nil, Capacitor(1, 2, 5E-6, 0, 5) :: Nil, Inductor(2, 0, 1, 20, 5) :: Nil)

    val dt = 1E-4
    //    var dynamicCircuit = circuit.getInitializedCircuit
    println("init circuit=" + dynamicCircuit)
    val v0 = dynamicCircuit.solve(dt).getVoltage(Resistor(1, 2, 10.0))
    println("voltage")
    for (i <- 0 until 10000) {
      val t = i * dt
      val comp = dynamicCircuit.getCompanionModel(dt)
      val compSol = comp.circuit.solve
      val solution = dynamicCircuit.solve(dt)
      val voltage = solution.getVoltage(Resistor(0, 1, 10.0))
      println(voltage)
      dynamicCircuit = dynamicCircuit.stepInTime(dt)
    }
  }
}

object TestRLCircuit {
  def main(args: Array[String]) {
    val L = 1
    val R = 10
    val V = 5.0
    val circuit = new FullCircuit(Battery(0, 1, V) :: Nil, Resistor(1, 2, R) :: Nil, Nil, Inductor(2, 0, L, 0, 0) :: Nil)

    val dt = 1E-4
    var dynamicCircuit = circuit.getInitializedCircuit
    println("init circuit=" + dynamicCircuit)
    val v0 = dynamicCircuit.solve(dt).getVoltage(Resistor(1, 2, 10.0))
    println("voltage\tdesiredVoltage")
    for (i <- 0 until 1000) {
      val t = i * dt
      val comp = dynamicCircuit.getCompanionModel(dt)
      //      println("companion=" + comp)
      val compSol = comp.circuit.solve
      //      println("companion sol=" + compSol)
      val solution = dynamicCircuit.solve(dt)
      val voltage = solution.getVoltage(Resistor(1, 2, 10.0))
      val desiredVoltage = -V * (1 - exp(-t * R / L)) //see http://en.wikipedia.org/wiki/Lr_circuit
      println(voltage + "\t" + desiredVoltage)
      val error = abs(voltage - desiredVoltage)
      dynamicCircuit = dynamicCircuit.stepInTime(dt)
    }
  }
}

object TestRCCircuit {
  def main(args: Array[String]) {
    val circuit = new FullCircuit(Battery(0, 1, 5.0) :: Nil, Resistor(1, 2, 10.0) :: Nil, Capacitor(2, 0, 1.0E-2, 0.0, 0.0) :: Nil, Nil)
    val inited = circuit.getInitializedCircuit
    val v0 = -5 //todo: make sure in sync with inited circuit
    println("inited=" + inited)

    val dt = 1E-4
    var dynamicCircuit = inited
    println("time\tcurrent\tvoltage\tdesiredVoltage\terror")
    for (i <- 0 until 10000) {
      val t = i * dt
      val solution = dynamicCircuit.solve(dt)
      val current = solution.getCurrent(Battery(0, 1, 5.0))
      val voltage = solution.getVoltage(Resistor(1, 2, 10.0))
      val desiredVoltage = v0 * exp(-t / 10.0 / 1.0E-2)
      val error = voltage - desiredVoltage
      println(t + "\t" + current + "\t" + voltage + "\t" + desiredVoltage + "\t" + error)
      dynamicCircuit = dynamicCircuit.stepInTime(dt)
    }
  }
}
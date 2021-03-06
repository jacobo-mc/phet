// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.simsharinganalysis.scripts.StateEntry
import java.awt.geom.Rectangle2D
import java.awt.Color
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction
import edu.colorado.phet.common.piccolophet.nodes.{PhetPText, PhetPPath}
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox
import edu.umd.cs.piccolo.nodes.PText
import edu.umd.cs.piccolo.event.{PInputEvent, PBasicInputEventHandler}

/**
 * @author Sam Reid
 */
class TimelineNode(states: List[StateEntry[SimState]], minTime: Long, maxTime: Long) extends PNode {
  //  val startTime = states.head.entry.time
  //  val endTime = states.last.entry.time
  //  val function = new LinearFunction(startTime, Math.max(endTime, startTime + 1000 * 60 * 3), 0, 1024)
  val function = new LinearFunction(minTime, maxTime, 0, 1024)
  val lightRed = new Color(255, 147, 147)
  val lightBlue = new Color(96, 216, 255)
  val lightGreen = new Color(144, 255, 191)
  for ( i <- 0 until states.length; val state = states(i) ) {
    val pair = state.start.tab match {
      case 0 => (lightRed, 10)
      case 1 => (lightGreen, 5)
      case 2 => (lightBlue, 0)
      case _ => throw new RuntimeException("tab not found")
    }
    val t0 = function.evaluate(state.entry.time)
    val t1 = if ( i + 1 <= states.length - 1 ) function.evaluate(states(i + 1).entry.time) else t0
    val width = t1 - t0
    addChild(new PhetPPath(new Rectangle2D.Double(function.evaluate(state.entry.time), pair._2, width, 10), pair._1))
    val tick = new Rectangle2D.Double(function.evaluate(state.entry.time), 20, 1, 10)
    addChild(new PhetPPath(tick, Color.black))
    if ( state.entry.matches("checkButton", "pressed") ) {
      val text = new PhetPText(state.entry("attempts"))
      val text2 = new PhetPText(state.entry("correct").toBoolean match {
                                  case true => "+"
                                  case false => "-"
                                })
      addChild(new VBox(0, text, text2) {
        setOffset(tick.getCenterX - getFullBounds.getWidth / 2, tick.getMaxY)
      })
    }
  }

  addChild(new PText("Export to console") {
    setOffset(10, 42)
    addInputEventListener(new PBasicInputEventHandler {
      override def mousePressed(event: PInputEvent) {
        println("i\ttime\ttab\tattempts\tcorrect")
        for ( i <- 0 until states.length; val state = states(i) ) {
          val attempts = if ( state.entry.matches("checkButton", "pressed") ) state.entry("attempts") else ""
          val correct = if ( state.entry.matches("checkButton", "pressed") ) state.entry("correct").toBoolean match {
            case true => "+"
            case false => "-"
          } else ""
          println(i + "\t" + state.entry.time + "\t" + state.start.tab + "\t" + attempts + "\t" + correct)
        }
      }
    })
  })
}
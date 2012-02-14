// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import edu.colorado.phet.simsharinganalysis.RealTimeAnalysis

/**
 * @author Sam Reid
 */
object ABSRealTimeAnalysis extends App {
  new RealTimeAnalysis(log => ( AcidBaseSolutionSpring2012AnalysisReport toReport log ).toString).main(args)
}
package edu.colorado.phet.graphics.gauges;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Scaling implements ActionListener {
    IGauge gauge;
    String name;
    float  min;
    float  max;
    int numMaj;
    int numMin;

    public Scaling( IGauge gauge, String name, float  min, float  max, int numMaj, int numMin ) {
        this.gauge = gauge;
        this.name = name;
        this.min = min;
        this.max = max;
        this.numMaj = numMaj;
        this.numMin = numMin;
    }

    public String getName() {
        return name;
    }

    public void actionPerformed( ActionEvent ae ) {
        gauge.setMin( min );
        gauge.setMax( max );
        //gauge.setNumMaj(numMaj);
        //gauge.setNumMin(numMin);
    }
}

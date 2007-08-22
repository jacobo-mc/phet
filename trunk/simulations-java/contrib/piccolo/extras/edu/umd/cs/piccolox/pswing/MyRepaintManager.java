// Copyright 2007 University of Colorado
package edu.umd.cs.piccolox.pswing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class MyRepaintManager extends RepaintManager {
    private final HashMap componentToDirtyRects = new HashMap();

    public void doUpdateNow() {
        Set keys = componentToDirtyRects.keySet();
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            JComponent jComponent = (JComponent)iterator.next();
            ArrayList origRect = (ArrayList)componentToDirtyRects.get(jComponent);
            ArrayList rect=consolidateList(origRect);
            for (int i = 0; i < rect.size(); i++) {
                Rectangle rectangle = (Rectangle)rect.get(i);
                jComponent.paintImmediately(rectangle);
            }
        }
        componentToDirtyRects.clear();

    }

    private ArrayList consolidateList(ArrayList origRect) {
        ArrayList newList=new ArrayList(origRect);

        for (int i=0;i<newList.size();i++){
            Rectangle a= (Rectangle)newList.get(i);

            for (int j = 0; j < newList.size(); j++) {
                Rectangle b = (Rectangle)newList.get(j);

                if (a != b && a.contains(b)) {
                    newList.remove(j);

                    --j;
                }
            }
        }

        //System.out.println("original size="+origRect.size()+", new size="+newList.size());
        
        return newList;
    }

    public synchronized void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        if (!componentToDirtyRects.containsKey(c)) {
            componentToDirtyRects.put(c, new ArrayList());
        }
        ArrayList list = (ArrayList)componentToDirtyRects.get(c);
        list.add(new Rectangle(x, y, w, h));
    }
}

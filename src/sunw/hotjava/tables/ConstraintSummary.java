// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ConstraintSummary.java

package sunw.hotjava.tables;

import sunw.hotjava.doc.Measurement;

class ConstraintSummary
{

    ConstraintSummary(int i, Measurement measurement, int j)
    {
        cellsSpanned = i;
        minPercent = j;
        minSize = measurement.getMinWidth();
        prefSize = measurement.getPreferredWidth();
    }

    void accumulateInfo(Measurement measurement, int i)
    {
        minSize = Math.max(minSize, measurement.getMinWidth());
        prefSize = Math.max(prefSize, measurement.getPreferredWidth());
        if(i >= 0 && i < minPercent)
            minPercent = i;
    }

    ConstraintSummary next;
    int minSize;
    int prefSize;
    int cellsSpanned;
    int minPercent;
}

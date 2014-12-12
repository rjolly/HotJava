// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Measurement.java

package sunw.hotjava.doc;

import java.io.Serializable;

public class Measurement
    implements Serializable
{

    public Measurement()
    {
    }

    public void reset()
    {
        floaterMinWidth = floaterPrefWidth = minWidth = prefWidth = 0;
    }

    public void adjustBy(int i)
    {
        minWidth += i;
        prefWidth += i;
    }

    public void adjustMinBy(int i)
    {
        minWidth += i;
    }

    public int getMinWidth()
    {
        return minWidth;
    }

    public void setMinWidth(int i)
    {
        minWidth = minWidth < i ? i : minWidth;
    }

    public int getPreferredWidth()
    {
        return prefWidth;
    }

    public void setPreferredWidth(int i)
    {
        prefWidth = prefWidth < i ? i : prefWidth;
    }

    public void setFloaterMinWidth(int i)
    {
        floaterMinWidth = floaterMinWidth < i ? i : floaterMinWidth;
    }

    public void setFloaterPreferredWidth(int i)
    {
        floaterPrefWidth = floaterPrefWidth < i ? i : floaterPrefWidth;
    }

    public void adjustForFloaters()
    {
        minWidth = floaterMinWidth < minWidth ? minWidth : floaterMinWidth;
        prefWidth = floaterPrefWidth < prefWidth ? prefWidth : floaterPrefWidth;
    }

    public void adjustForFloatersNoBreak()
    {
        minWidth = floaterMinWidth + minWidth;
        prefWidth = floaterPrefWidth + prefWidth;
    }

    public void append(Measurement measurement)
    {
        minWidth = minWidth < measurement.minWidth ? measurement.minWidth : minWidth;
        prefWidth += measurement.prefWidth;
        floaterMinWidth = floaterMinWidth < measurement.floaterMinWidth ? measurement.floaterMinWidth : floaterMinWidth;
        floaterPrefWidth = floaterPrefWidth < measurement.floaterPrefWidth ? measurement.floaterPrefWidth : floaterPrefWidth;
    }

    public void appendNoBreak(Measurement measurement)
    {
        minWidth += measurement.minWidth;
        prefWidth += measurement.prefWidth;
        floaterMinWidth = floaterMinWidth < measurement.floaterMinWidth ? measurement.floaterMinWidth : floaterMinWidth;
        floaterPrefWidth = floaterPrefWidth < measurement.floaterPrefWidth ? measurement.floaterPrefWidth : floaterPrefWidth;
    }

    public void unify(Measurement measurement)
    {
        minWidth = minWidth < measurement.minWidth ? measurement.minWidth : minWidth;
        prefWidth = prefWidth < measurement.prefWidth ? measurement.prefWidth : prefWidth;
        floaterMinWidth = floaterMinWidth < measurement.floaterMinWidth ? measurement.floaterMinWidth : floaterMinWidth;
        floaterPrefWidth = floaterPrefWidth < measurement.floaterPrefWidth ? measurement.floaterPrefWidth : floaterPrefWidth;
    }

    public String toString()
    {
        return "Measurement[minWidth=" + minWidth + ", prefWidth=" + prefWidth + "]";
    }

    static final long serialVersionUID = 0x1258174296bc0fa7L;
    private int minWidth;
    private int prefWidth;
    private int floaterMinWidth;
    private int floaterPrefWidth;
}

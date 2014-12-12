// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FormatterVBreakInfo.java

package sunw.hotjava.doc;

import java.util.Enumeration;
import java.util.Vector;

// Referenced classes of package sunw.hotjava.doc:
//            CompoundVBreakInfo, FloaterInfo

public class FormatterVBreakInfo extends CompoundVBreakInfo
{

    public int getStartLine()
    {
        return startLine;
    }

    public void setStartLine(int i)
    {
        startLine = i;
    }

    public int getEndLine()
    {
        return endLine;
    }

    public void setEndLine(int i)
    {
        endLine = i;
    }

    public void addFloater(FloaterInfo floaterinfo)
    {
        if(floaterBreakInfo == null)
            floaterBreakInfo = new Vector();
        floaterBreakInfo.addElement(floaterinfo);
    }

    public Enumeration enumerateFloaters()
    {
        if(floaterBreakInfo == null)
            return null;
        else
            return floaterBreakInfo.elements();
    }

    public FormatterVBreakInfo()
    {
    }

    private int startLine;
    private int endLine;
    private Vector floaterBreakInfo;
}

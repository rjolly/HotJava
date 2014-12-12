// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Floatable.java

package sunw.hotjava.doc;

import java.awt.Graphics;

// Referenced classes of package sunw.hotjava.doc:
//            Formatter, VBreakInfo

public interface Floatable
{

    public abstract int paint(Formatter formatter, Graphics g, int i, int j);

    public abstract int print(Formatter formatter, Graphics g, int i, int j, VBreakInfo vbreakinfo);
}

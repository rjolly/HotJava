// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentState.java

package sunw.hotjava.doc;

import java.awt.Color;

// Referenced classes of package sunw.hotjava.doc:
//            DocumentBackground, Formatter, DocStyle, DocumentFormatter

public class DocumentState
{

    public DocumentState()
    {
        showcur = false;
        showsel = true;
        paintingScreen = false;
    }

    public DocumentBackground bg;
    public Color background;
    public boolean showcur;
    public boolean showsel;
    public int selStart;
    public int selEnd;
    public Formatter selecter;
    public boolean started;
    public boolean paintingScreen;
    public DocStyle docStyle;
    public DocumentFormatter topFormatter;
}

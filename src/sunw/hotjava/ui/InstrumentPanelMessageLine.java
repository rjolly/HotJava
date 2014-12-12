// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   InstrumentPanelMessageLine.java

package sunw.hotjava.ui;

import java.awt.Component;
import java.awt.Container;

// Referenced classes of package sunw.hotjava.ui:
//            InstrumentPanelPart, InstrumentPanelMessageLayout, MessageLine

public class InstrumentPanelMessageLine extends InstrumentPanelPart
{

    public InstrumentPanelMessageLine()
    {
        setLayout(new InstrumentPanelMessageLayout());
    }

    public boolean isEmpty()
    {
        return !hasMessageLine() && !hasClock();
    }

    public boolean hasMessageLine()
    {
        return messageLine != null;
    }

    public void add(MessageLine messageline)
    {
        messageLine = messageline;
        invalidate();
        add("MessageLine", messageline);
        validate();
        setVisible(true);
    }

    public MessageLine removeMessageLine()
    {
        MessageLine messageline = messageLine;
        invalidate();
        remove(messageLine);
        messageLine = null;
        validate();
        setVisible(false);
        return messageline;
    }

    MessageLine messageLine;
}

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ISINDEX.java

package sunw.hotjava.tags;

import java.awt.*;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.Globals;

// Referenced classes of package sunw.hotjava.tags:
//            IsindexPanel

public class ISINDEX extends EmptyTagItem
{

    public boolean isBlock()
    {
        return true;
    }

    public void init(Document document)
    {
        super.init(document);
        prompt = getAttribute("prompt");
        action = getAttribute("action");
        if(action == null)
            action = getAttribute("href");
    }

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        if(formatstate.state == 0)
        {
            int i = formatstate.maxWidth;
            int j = 40;
            Panel panel = (Panel)formatter.getPanel(this);
            if(panel != null)
            {
                panel.layout();
                Dimension dimension = panel.preferredSize();
                panel.setSize(dimension);
                j = dimension.height;
            }
            formatstate.format = 3;
            formatstate.width = i;
            formatstate.ascent = 10 + j;
            formatstate.descent = 10;
            formatstate.pos += 4096;
        }
        return true;
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        int k = docline.width;
        byte byte0 = 4;
        j += byte0;
        DocumentState documentstate = formatter.displayStyle.win.getDocumentState();
        g.setColor(Globals.getVisible3DColor(documentstate.background));
        g.draw3DRect(i, j, k, 2, true);
        formatter.displayPos += 4096;
        j += byte0;
        Panel panel = (Panel)formatter.getPanel(this);
        if(panel != null)
        {
            panel.setLocation(i, j + byte0);
            panel.validate();
            panel.show();
            Dimension dimension = panel.getSize();
            j += dimension.height + byte0;
        }
        j += byte0;
        g.draw3DRect(i, j, k, 2, true);
        return k;
    }

    public int print(Formatter formatter, Graphics g, int i, int j, DocLine docline, VBreakInfo vbreakinfo)
    {
        paint(formatter, g, i, j, docline);
        return 0;
    }

    public boolean needsActivation()
    {
        return true;
    }

    public boolean measureItem(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurement.setMinWidth(formatstate.maxWidth);
        measurement.setPreferredWidth(formatstate.maxWidth);
        measurestate.pos += 4096;
        return true;
    }

    public Component createView(Formatter formatter, Document document)
    {
        IsindexPanel isindexpanel = new IsindexPanel(document, prompt, action);
        return isindexpanel;
    }

    public ISINDEX()
    {
    }

    private String action;
    private String prompt;
}

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FRAMESET.java

package sunw.hotjava.tags;

import java.awt.*;
import java.util.StringTokenizer;
import sunw.hotjava.doc.*;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.tags:
//            FRAME, FrameSetPanel

public class FRAMESET extends TagItem
{

    public boolean formatStartTag(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.pos += getOffset() + 1 << 12;
        return false;
    }

    public boolean measureStartTag(Formatter formatter, FormatState formatstate, Measurement measurement, MeasureState measurestate)
    {
        measurestate.pos += getOffset() + 1 << 12;
        return false;
    }

    public void init(Document document)
    {
        super.init(document);
        document.setIHaveFrameSet();
    }

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline, boolean flag)
    {
        int k = getOffset();
        if(k > 0)
        {
            FrameSetPanel framesetpanel = (FrameSetPanel)formatter.getPanel(this);
            if(framesetpanel != null)
            {
                Dimension dimension = framesetpanel.getSize();
                if(dimension.width != formatter.width || dimension.height != formatter.height)
                {
                    framesetpanel.setSize(formatter.width + formatter.getVScrollBarWidth(), formatter.height);
                    framesetpanel.unsafeValidate();
                    framesetpanel.setVisible(true);
                }
            }
        } else
        {
            k = 1;
        }
        formatter.displayPos += k << 12;
        return 0;
    }

    public int getNeededFrames(Document document)
    {
        Attributes attributes = getAttributes();
        String s = "*";
        if(attributes != null && attributes.get("rows") != null)
            s = attributes.get("rows");
        StringTokenizer stringtokenizer = new StringTokenizer(s, ",");
        int j = stringtokenizer.countTokens();
        s = "*";
        if(attributes != null && attributes.get("cols") != null)
            s = attributes.get("cols");
        stringtokenizer = new StringTokenizer(s, ",");
        int k = stringtokenizer.countTokens();
        int l;
        if(j * k != 0)
            l = j * k;
        else
            l = Math.max(j, k);
        int i = getIndex() + 1;
        int i1 = 0;
        for(; i < getIndex() + getOffset(); i++)
            if(document.items[i] instanceof FRAME)
                i1++;
            else
            if(document.items[i] instanceof FRAMESET)
            {
                i1++;
                i += document.items[i].getOffset();
            }

        if(i1 < l)
            return l - i1;
        else
            return 0;
    }

    public Container getPanel()
    {
        return myPanel;
    }

    public boolean needsActivation()
    {
        return true;
    }

    public Component createView(Formatter formatter, Document document)
    {
        int i = 3;
        if(super.atts != null)
        {
            String s = super.atts.get("border");
            if(s != null)
                try
                {
                    i = Integer.parseInt(s);
                }
                catch(Exception _ex) { }
            s = super.atts.get("frameborder");
            if(s != null && ("no".equalsIgnoreCase(s) || "0".equals(s)))
                i = 0;
        }
        myPanel = new FrameSetPanel(formatter, this, i);
        return myPanel;
    }

    public FRAMESET()
    {
    }

    private Container myPanel;
}

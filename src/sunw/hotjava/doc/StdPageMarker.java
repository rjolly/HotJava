// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StdPageMarker.java

package sunw.hotjava.doc;

import java.awt.*;
import java.net.URL;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.doc:
//            Document, PageMargins, PageMarker

class StdPageMarker
    implements PageMarker
{

    StdPageMarker()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        String s = hjbproperties.getProperty("hotjava.print.label.font", "SansSerif-8");
        labelFont = Font.decode(s);
    }

    public void adjustMargins(PageMargins pagemargins)
    {
        margins = pagemargins;
    }

    public void markBefore(Document document, Graphics g, PrintJob printjob, int i, int j)
    {
    }

    public void markAfter(Document document, Graphics g, PrintJob printjob, int i, int j)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        Dimension dimension = printjob.getPageDimension();
        int k = printjob.getPageResolution();
        String s = "hotjava.print.header.spacing";
        int l = (hjbproperties.getInteger(s, 18) * k) / 72;
        s = "hotjava.print.footer.spacing";
        int i1 = (hjbproperties.getInteger(s, 18) * k) / 72;
        s = "hotjava.print.header.margin";
        int j1 = (hjbproperties.getInteger(s, 54) * k) / 72;
        try
        {
            g.setFont(labelFont);
            g.setColor(Color.black);
            if(fm == null)
                fm = g.getFontMetrics();
            String s1 = document.getTitle();
            int k1 = j1;
            int l1 = margins.getTopMargin() - (l + fm.getAscent());
            if(s1 != null)
            {
                g.drawString(s1, k1, l1);
                k1 += 20 + fm.stringWidth(s1);
            }
            URL url = document.getURL();
            if(url != null)
            {
                String s2 = url.toString();
                int i2 = fm.stringWidth(s2);
                int k2 = dimension.width - j1 - i2;
                if(k2 < k1)
                    k2 = k1;
                g.drawString(s2, k2, l1);
            }
            String s3 = "- " + String.valueOf(i + 1) + " -";
            int j2 = fm.stringWidth(s3);
            k1 = (dimension.width - j2) / 2;
            int l2 = dimension.height - fm.getDescent() - (margins.getBottomMargin() - i1);
            g.drawString(s3, k1, l2);
        }
        finally
        {
            g.dispose();
        }
    }

    private Font labelFont;
    private FontMetrics fm;
    PageMargins margins;
}

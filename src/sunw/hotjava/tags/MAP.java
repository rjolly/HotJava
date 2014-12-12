// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MAP.java

package sunw.hotjava.tags;

import java.awt.Component;
import java.awt.Graphics;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import sunw.hotjava.doc.*;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.tags:
//            AREA, ImageMap

public class MAP extends TagItem
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

    public int paint(Formatter formatter, Graphics g, int i, int j, DocLine docline)
    {
        int k = getOffset();
        if(k < 0)
            k = 1;
        formatter.displayPos += k << 12;
        return 0;
    }

    public boolean needsActivation()
    {
        return imap == null;
    }

    public Component createView(Formatter formatter, Document document)
    {
        if(imap == null)
        {
            Attributes attributes = getAttributes();
            if(attributes == null)
                return null;
            name = attributes.get("name");
            URL url;
            try
            {
                url = new URL(document.getBaseURL(), "#" + name);
            }
            catch(MalformedURLException _ex)
            {
                return null;
            }
            imap = new ImageMap(url);
            document.getImageMaps().put(url, imap);
            int i = getIndex();
            for(int j = i + getOffset(); i < j; i++)
            {
                DocItem docitem = document.getItem(i);
                if(docitem instanceof AREA)
                {
                    Attributes attributes1 = ((AREA)docitem).getAttributes();
                    String s = attributes1.get("shape");
                    String s1 = attributes1.get("coords");
                    String s2 = filterString(attributes1.get("href"));
                    String s3 = attributes1.get("nohref");
                    String s4 = attributes1.get("alt");
                    String s5 = attributes1.get("target");
                    imap.addArea(document.getBaseURL(), s, s1, s2, s3 != null, s4, s5, (TagItem)docitem);
                }
            }

        }
        return null;
    }

    public MAP()
    {
    }

    String name;
    ImageMap imap;
}

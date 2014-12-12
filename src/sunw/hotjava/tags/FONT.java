// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FONT.java

package sunw.hotjava.tags;

import java.awt.Color;
import java.util.StringTokenizer;
import java.util.Vector;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.Globals;

public class FONT extends FlowTagItem
{

    public void init(Document document)
    {
        super.init(document);
        String s = getAttribute("size");
        try
        {
            if(s != null)
            {
                if(s.startsWith("+"))
                    relativeSize = Integer.parseInt(s.substring(1));
                else
                if(s.startsWith("-"))
                    relativeSize = -Integer.parseInt(s.substring(1));
                else
                    index = Integer.parseInt(s) + 2;
                sizeSpecified = true;
            }
        }
        catch(NumberFormatException _ex) { }
        s = getAttribute("color");
        if(s != null)
            col = Globals.mapNamedColor(s);
        s = getAttribute("face");
        if(s != null)
        {
            faces = new Vector();
            for(StringTokenizer stringtokenizer = new StringTokenizer(s, ","); stringtokenizer.hasMoreTokens(); faces.addElement(stringtokenizer.nextToken().trim()));
        }
    }

    public void modifyStyle(DocStyle docstyle)
    {
        if(sizeSpecified)
        {
            int i = index;
            if(i == 0)
                i = docstyle.doc.basefontSizeAt(docstyle, getIndex()) + relativeSize;
            else
                i = docstyle.adjustFontSize(i);
            docstyle.font = docstyle.font.getIndex(i);
        }
        if(col != null)
            docstyle.color = col;
        if(faces != null)
        {
            if(faceFound != null)
            {
                docstyle.font = docstyle.font.getName(faceFound);
                return;
            }
            for(int j = 0; j < faces.size(); j++)
            {
                String s = (String)faces.elementAt(j);
                if(docstyle.font.getName(s) != null)
                {
                    faceFound = s;
                    docstyle.font = docstyle.font.getName(faceFound);
                    return;
                }
            }

        }
    }

    public FONT()
    {
        sizeSpecified = false;
    }

    private Color col;
    private int relativeSize;
    private boolean sizeSpecified;
    private int index;
    private Vector faces;
    private String faceFound;
}

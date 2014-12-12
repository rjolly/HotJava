// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HTMLOutputWriter.java

package sunw.html;

import java.io.*;

// Referenced classes of package sunw.html:
//            AttributeList, Attributes, DTD, DTDConstants, 
//            Element, Entity, Tag

public final class HTMLOutputWriter extends FilterWriter
    implements DTDConstants
{

    public HTMLOutputWriter(Writer writer, DTD dtd1)
    {
        super(writer);
        dtd = dtd1;
    }

    public void write(int i)
        throws IOException
    {
        super.out.write(i);
        col = i != 10 ? col + 1 : 0;
    }

    public void write(String s)
        throws IOException
    {
        int i = s.length();
        for(int j = 0; j < i; j++)
            write(s.charAt(j));

    }

    void writeEscaped(int i)
        throws IOException
    {
        switch(i)
        {
        case 60: // '<'
            write("&lt;");
            return;

        case 62: // '>'
            write("&gt;");
            return;

        case 38: // '&'
            write("&amp;");
            return;

        case 34: // '"'
            write("&quot;");
            return;
        }
        if(i < 32 || i > 127)
        {
            Entity entity = dtd.getEntity(i);
            write(38);
            if(entity != null)
            {
                write(entity.getName());
            } else
            {
                write(35);
                write(String.valueOf(i));
            }
            write(59);
            return;
        } else
        {
            write(i);
            return;
        }
    }

    void writeText(int i)
        throws IOException
    {
        switch(i)
        {
        case 9: // '\t'
        case 10: // '\n'
        case 32: // ' '
            if(pre == 0)
            {
                space = !eatspace;
                return;
            } else
            {
                write(i);
                return;
            }
        }
        if(space)
        {
            write(!newline && col < 60 ? 32 : 10);
            space = false;
        }
        eatspace = newline = false;
        writeEscaped(i);
    }

    void writeTag(Element element, Attributes attributes)
        throws IOException
    {
        write(60);
        write(element.getName());
        if(attributes != null)
        {
            int i = attributes.length();
            for(int j = 0; j < i; j++)
            {
                String s = attributes.getName(j);
                String s1 = attributes.get(j);
                AttributeList attributelist = element.getAttribute(s);
                int k = attributelist != null ? attributelist.getType() : 1;
                write(32);
                write(s);
                int l = s1.length();
                switch(k)
                {
                case 4: // '\004'
                case 7: // '\007'
                case 9: // '\t'
                case 12: // '\f'
                case 14: // '\016'
                    write(61);
                    for(int i1 = 0; i1 < l; i1++)
                        writeEscaped(s1.charAt(i1));

                    break;

                default:
                    write(61);
                    write(34);
                    for(int j1 = 0; j1 < l; j1++)
                        writeEscaped(s1.charAt(j1));

                    write(34);
                    break;
                }
            }

        }
        write(62);
    }

    public void startTag(Tag tag)
        throws IOException
    {
        if(pre == 0 && (space || tag.isBlock()) && !eatspace)
            write(10);
        newline = space = false;
        writeTag(tag.getElement(), tag.getAttributes());
        if(pre == 0)
            write(10);
        if(tag.isPreformatted())
            pre++;
        else
        if(tag.getElement().getType() == 1)
            plain++;
        eatspace = pre == 0 && tag.isBlock();
    }

    public void endTag(Tag tag)
        throws IOException
    {
        if(tag.isPreformatted())
            pre--;
        else
        if(tag.getElement().getType() == 1)
            plain--;
        newline = space = false;
        if(pre == 0 && !eatspace)
            write(10);
        write(60);
        write(47);
        write(tag.getElement().getName());
        write(62);
        if(pre == 0)
        {
            if(tag.isBlock())
            {
                write(10);
                eatspace = true;
                return;
            }
            newline = true;
        }
    }

    public void endTag(Element element)
        throws IOException
    {
        write(10);
        write(60);
        write(47);
        write(element.getName());
        write(62);
    }

    public void emptyTag(Tag tag)
        throws IOException
    {
        if(pre == 0 && (space || newline || tag.isBlock()) && !eatspace)
            write(10);
        eatspace = newline = space = false;
        writeTag(tag.getElement(), tag.getAttributes());
        if(pre == 0)
        {
            if(tag.isBlock())
            {
                write(10);
                eatspace = true;
                return;
            }
            newline = true;
        }
    }

    public void emptyTag(Element element, Attributes attributes)
        throws IOException
    {
        writeTag(element, attributes);
    }

    public void text(char ac[])
        throws IOException
    {
        text(ac, ac.length);
    }

    public void text(char ac[], int i)
        throws IOException
    {
        if(plain == 0)
        {
            for(int j = 0; j < i; j++)
            {
                char c = ac[j];
                if(c > '\377')
                    writeEscaped(c);
                else
                    writeText(c);
            }

            return;
        }
        for(int k = 0; k < i; k++)
        {
            char c1 = ac[k];
            if(c1 > '\377')
                writeEscaped(c1);
            else
                write(c1);
        }

    }

    public void text(String s)
        throws IOException
    {
        if(plain == 0)
        {
            int i = s.length();
            for(int j = 0; j < i; j++)
                writeText(s.charAt(j));

            return;
        } else
        {
            write(s);
            return;
        }
    }

    public void comment(String s)
        throws IOException
    {
        if(newline)
            write(10);
        write(60);
        write(33);
        write(45);
        write(45);
        write(s);
        write(45);
        write(45);
        write(62);
        eatspace = space = false;
        newline = pre == 0;
    }

    public void doctype(String s)
        throws IOException
    {
        write(60);
        write(33);
        write("DOCTYPE ");
        write(s);
        write(62);
        write(10);
    }

    DTD dtd;
    int col;
    int plain;
    int pre;
    boolean newline;
    boolean space;
    boolean eatspace;
    final int MAXCOL = 60;
}

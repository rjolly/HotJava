// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MultiLineLabel.java

package sunw.hotjava.ui;

import java.awt.*;
import java.util.StringTokenizer;
import java.util.Vector;
import sunw.hotjava.misc.HJBProperties;

public class MultiLineLabel extends Canvas
{
    public class LineFont
    {

        public String line;
        public Font font;

        public LineFont(String s, Font font1)
        {
            line = s;
            font = font1;
        }
    }


    public MultiLineLabel(String s, int i, int j, int k)
    {
        label = "";
        initialLineFonts = new Vector();
        lineFonts = new Vector();
        alignment = 0;
        measured = false;
        properties = HJBProperties.getHJBProperties("hjbrowser");
        if(s != null && s.length() > 0)
            parseIntoLines(s);
        label = s;
        marginWidth = i;
        marginHeight = j;
        alignment = k;
    }

    public MultiLineLabel(String s, int i, int j, int k, Font font)
    {
        this(s, i, j, k);
        setFont(font);
    }

    public MultiLineLabel(String s, int i, int j)
    {
        this(s, i, j, 0);
    }

    public MultiLineLabel(String s, int i)
    {
        this(s, 10, 10, i);
    }

    public MultiLineLabel(String s)
    {
        this(s, 10, 10, 0);
    }

    public MultiLineLabel(String s, Font font)
    {
        this(s, 10, 10, 0);
        setFont(font);
    }

    public MultiLineLabel()
    {
        this("");
    }

    public void setLabel(String s)
    {
        label = s;
        parseIntoLines(s);
        measured = false;
        parseIntoLines(s);
        repaint();
    }

    public void setFont(Font font)
    {
        super.setFont(font);
        measured = false;
        parseIntoLines(label);
        repaint();
    }

    public void setForeground(Color color)
    {
        super.setForeground(color);
        repaint();
    }

    public void setBackground(Color color)
    {
        super.setBackground(color);
        repaint();
    }

    public int getAlignment()
    {
        return alignment;
    }

    public int getMarginWidth()
    {
        return marginWidth;
    }

    public int getMarginHeight()
    {
        return marginHeight;
    }

    public void setAlignment(int i)
    {
        alignment = i;
        repaint();
    }

    public void setMarginWidth(int i)
    {
        marginWidth = i;
        repaint();
    }

    public void setMarginHeight(int i)
    {
        marginHeight = i;
        repaint();
    }

    public Dimension preferredSize()
    {
        if(!measured)
            measure();
        return new Dimension(maxWidth + 2 * marginWidth, numberOfLines * lineHeight + 2 * marginHeight);
    }

    public Dimension minimumSize()
    {
        measure();
        return new Dimension(maxWidth, numberOfLines * lineHeight);
    }

    public void update(Graphics g)
    {
        g.setColor(getForeground());
        paint(g);
    }

    public void paint(Graphics g)
    {
        Dimension dimension = size();
        if(!measured)
            measure();
        int j = lineAscent + (dimension.height - numberOfLines * lineHeight) / 2;
        for(int k = 0; k < lineFonts.size();)
        {
            LineFont linefont = (LineFont)lineFonts.elementAt(k);
            String s = linefont.line;
            if(s == null)
                s = "";
            g.setFont(linefont.font);
            int i;
            switch(alignment)
            {
            case 0: // '\0'
                i = Math.min(marginWidth, Math.max((dimension.width - maxWidth) / 2, 0));
                break;

            case 1: // '\001'
            default:
                i = (dimension.width - lineWidths[k]) / 2;
                break;

            case 2: // '\002'
                i = Math.max(dimension.width - marginWidth - lineWidths[k], (dimension.width + marginWidth) / 2 - lineWidths[k]);
                break;
            }
            g.drawString(s, i, j);
            k++;
            j += lineHeight;
        }

    }

    protected void parseIntoLines(String s)
    {
        if(s == null)
            return;
        if(s.indexOf('\n') < 0)
            s = s + "\n";
        StringTokenizer stringtokenizer = new StringTokenizer(s, "\n");
        numberOfLines = stringtokenizer.countTokens();
        if(super.getFont() == null)
            super.setFont(new Font("Dialog", 0, 12));
        Font font = super.getFont();
        initialLineFonts.removeAllElements();
        lineWidths = new int[numberOfLines];
        for(int i = 0; i < numberOfLines; i++)
        {
            Font font1 = font;
            String s1 = stringtokenizer.nextToken();
            if(s1.startsWith("<font"))
            {
                String s2 = s1.substring(5, s1.indexOf(">"));
                String s3 = "";
                int j = -1;
                int k = -1;
                for(StringTokenizer stringtokenizer1 = new StringTokenizer(s2, ";"); stringtokenizer1.hasMoreTokens();)
                {
                    String s4 = stringtokenizer1.nextToken();
                    int l = 0;
                    if((l = s4.indexOf("=")) > 0)
                    {
                        if(s4.trim().startsWith("name"))
                        {
                            String s5 = s4.substring(l + 1).trim();
                            s3 = substitute_entities(s5);
                        }
                        if(s4.trim().startsWith("size"))
                        {
                            String s6 = s4.substring(l + 1).trim();
                            j = Integer.parseInt(substitute_entities(s6));
                        }
                        if(s4.trim().startsWith("style"))
                        {
                            String s7 = s4.substring(l + 1).trim();
                            String s8 = substitute_entities(s7);
                            if(s8.equalsIgnoreCase("bold"))
                                k = 1;
                            else
                            if(s8.equalsIgnoreCase("italic"))
                                k = 2;
                            else
                                k = 0;
                        }
                    }
                }

                if(s3.length() == 0)
                    s3 = font.getName();
                if(j == -1)
                    j = font.getSize();
                if(k == -1)
                    k = font.getStyle();
                s1 = s1.substring(s1.indexOf(">") + 1);
                font1 = new Font(s3, k, j);
            }
            initialLineFonts.addElement(new LineFont(s1, font1));
        }

    }

    private String substitute_entities(String s)
    {
        boolean flag = false;
        boolean flag1 = false;
        int i;
        String s2;
        String s3;
        String s4;
        for(; s != null && (i = s.indexOf('&')) >= 0; s = s2 + s4 + s3)
        {
            int j = s.indexOf(';', i);
            if(j < 0)
                j = s.length();
            String s1 = s.substring(i + 1, j);
            s2 = s.substring(0, i);
            s3 = "";
            if(j == s.length())
                s3 = s.substring(j);
            else
                s3 = s.substring(j + 1);
            s4 = properties.getProperty(s1, "0");
        }

        return s;
    }

    protected void measure()
    {
        java.awt.Container container = getParent();
        int i = -1;
        if(container != null)
            i = container.getSize().width - 2 * marginWidth;
        FontMetrics fontmetrics = getToolkit().getFontMetrics(getFont());
        if(fontmetrics == null)
            return;
        lineFonts.removeAllElements();
        lineHeight = fontmetrics.getHeight();
        lineAscent = fontmetrics.getAscent();
        maxWidth = 0;
        Vector vector = new Vector();
        if(i > 0)
        {
            for(int j = 0; j < initialLineFonts.size(); j++)
            {
                LineFont linefont = (LineFont)initialLineFonts.elementAt(j);
                String s = linefont.line;
                Font font = linefont.font;
                FontMetrics fontmetrics1 = getToolkit().getFontMetrics(font);
                int i1 = 0;
                for(int j1 = 0; j1 < s.length();)
                {
                    int k1;
                    for(k1 = 0; k1 < i && j1 < s.length(); k1 += fontmetrics1.charWidth(s.charAt(j1++)));
                    int l1 = j1;
                    if(j1 > 1 && j1 < s.length() && s.charAt(j1) != ' ')
                        for(; j1 > 1 && s.charAt(j1) != ' '; j1--);
                    if(i1 >= j1)
                        j1 = l1;
                    vector.addElement(new Integer(k1));
                    lineFonts.addElement(new LineFont(s.substring(i1, j1), font));
                    if(j1 > 0 && j1 < s.length() && s.charAt(j1) == ' ')
                        j1++;
                    i1 = j1;
                }

            }

        } else
        {
            for(int k = 0; k < initialLineFonts.size(); k++)
            {
                LineFont linefont1 = (LineFont)initialLineFonts.elementAt(k);
                String s1 = linefont1.line;
                Font font1 = linefont1.font;
                FontMetrics fontmetrics2 = getToolkit().getFontMetrics(font1);
                vector.addElement(new Integer(fontmetrics2.stringWidth(s1)));
                lineFonts.addElement(new LineFont(s1, font1));
            }

        }
        numberOfLines = vector.size();
        lineWidths = new int[numberOfLines];
        for(int l = 0; l < vector.size(); l++)
        {
            lineWidths[l] = ((Integer)vector.elementAt(l)).intValue();
            if(lineWidths[l] > maxWidth)
                maxWidth = lineWidths[l];
        }

        measured = true;
    }

    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    protected String label;
    protected Vector initialLineFonts;
    protected Vector lineFonts;
    protected int lineWidths[];
    protected int numberOfLines;
    protected int marginWidth;
    protected int marginHeight;
    protected int lineHeight;
    protected int lineAscent;
    protected int maxWidth;
    protected int alignment;
    protected boolean measured;
    private HJBProperties properties;
}

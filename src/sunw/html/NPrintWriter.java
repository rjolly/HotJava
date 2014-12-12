// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TagStack.java

package sunw.html;

import java.io.PrintWriter;

// Referenced classes of package sunw.html:
//            TagStack

class NPrintWriter extends PrintWriter
{

    public NPrintWriter(int i)
    {
        super(System.out);
        numLines = 5;
        numLines = i;
    }

    public void println(char ac[])
    {
        if(numPrinted >= numLines)
            return;
        char ac1[] = null;
        for(int i = 0; i < ac.length; i++)
        {
            if(ac[i] == '\n')
                numPrinted++;
            if(numPrinted == numLines)
                System.arraycopy(ac, 0, ac1, 0, i);
        }

        if(ac1 != null)
            super.print(ac1);
        if(numPrinted == numLines)
        {
            return;
        } else
        {
            super.println(ac);
            numPrinted++;
            return;
        }
    }

    private int numLines;
    private int numPrinted;
}

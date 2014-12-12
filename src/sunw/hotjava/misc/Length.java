// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Length.java

package sunw.hotjava.misc;

import java.io.Serializable;

public class Length
    implements Serializable
{

    public Length()
    {
        isSet = false;
    }

    public Length(String s)
    {
        isSet = false;
        if(s != null)
        {
            isSet = true;
            s = s.trim();
            if(s.endsWith("%"))
            {
                isPercentage = true;
                s = s.substring(0, s.length() - 1);
            }
            try
            {
                value = Integer.parseInt(s);
                return;
            }
            catch(Exception _ex)
            {
                isSet = false;
            }
            return;
        } else
        {
            return;
        }
    }

    public boolean isPercentage()
    {
        return isPercentage;
    }

    public boolean isSet()
    {
        return isSet;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int i)
    {
        value = i;
    }

    public String toString()
    {
        if(!isSet)
            return "unset";
        String s = String.valueOf(value);
        if(isPercentage)
            s = s + " %";
        return s;
    }

    private boolean isSet;
    private boolean isPercentage;
    private int value;
    static final long serialVersionUID = 0x641d56091130bcdL;
}

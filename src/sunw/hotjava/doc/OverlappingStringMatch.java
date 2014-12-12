// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OverlappingStringMatch.java

package sunw.hotjava.doc;

import java.io.PrintStream;

public class OverlappingStringMatch
{

    public static OverlappingStringMatch findMatch(String s, String s1)
    {
        return findMatch(s, s1, true);
    }

    public static OverlappingStringMatch findMatch(String s, String s1, boolean flag)
    {
        boolean flag1 = false;
        int j = s.length();
        int k = s1.length();
        char c = s1.charAt(0);
        for(int i = s.indexOf(c, 0); i > 0; i = s.indexOf(c, i + 1))
            if(s.regionMatches(flag, i, s1, 0, j - i <= k ? j - i : k))
            {
                String s2 = null;
                if(j - i > k)
                    s2 = s1;
                else
                    s2 = s1.substring(0, j - i);
                return new OverlappingStringMatch(s2, i, s2.length());
            }

        return null;
    }

    public OverlappingStringMatch()
    {
        overlappingMatch = null;
        offsetIntoFirstString = 0;
        matchLength = 0;
    }

    public OverlappingStringMatch(String s, int i, int j)
    {
        overlappingMatch = s;
        offsetIntoFirstString = i;
        matchLength = j;
    }

    public static void main(String args[])
    {
        OverlappingStringMatch overlappingstringmatch = findMatch(args[0], args[1], true);
        if(overlappingstringmatch != null)
        {
            System.out.println("Match <" + overlappingstringmatch.overlappingMatch + "> found at " + overlappingstringmatch.offsetIntoFirstString + " of length " + overlappingstringmatch.matchLength);
            return;
        } else
        {
            System.out.println("No match found.");
            return;
        }
    }

    public String overlappingMatch;
    public int offsetIntoFirstString;
    public int matchLength;
}

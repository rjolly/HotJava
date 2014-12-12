// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FrameToken.java

package sunw.hotjava.bean;


public class FrameToken
{

    FrameToken(FrameToken frametoken, int ai[])
    {
        this(frametoken, ai, null);
    }

    FrameToken(FrameToken frametoken, int ai[], String s)
    {
        parent = frametoken;
        indexSet = ai;
        name = s;
    }

    void setChildren(FrameToken aframetoken[])
    {
        children = aframetoken;
    }

    public String getName()
    {
        return name;
    }

    public int getNumberOfChildren()
    {
        if(children != null)
            return children.length;
        else
            return 0;
    }

    public boolean hasChildren()
    {
        return children != null;
    }

    public FrameToken getChildFrame(int i)
    {
        if(children != null && i < children.length)
            return children[i];
        else
            return null;
    }

    public FrameToken getChildFrame(String s)
    {
        if(children != null)
        {
            FrameToken frametoken = null;
            for(int i = 0; i < children.length; i++)
            {
                if(children[i].getName() != null && children[i].getName().equals(s))
                {
                    frametoken = children[i];
                    break;
                }
                if(!children[i].hasChildren())
                    continue;
                frametoken = children[i].getChildFrame(s);
                if(frametoken != null)
                    break;
            }

            return frametoken;
        } else
        {
            return null;
        }
    }

    public FrameToken getParentFrame()
    {
        return parent;
    }

    public int getIndex()
    {
        if(indexSet.length > 0)
            return indexSet[indexSet.length - 1];
        else
            return 0;
    }

    int[] getIndexSet()
    {
        return indexSet;
    }

    private FrameToken parent;
    private int indexSet[];
    private String name;
    private FrameToken children[];
}

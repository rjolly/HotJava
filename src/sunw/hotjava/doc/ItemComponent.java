// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ItemComponent.java

package sunw.hotjava.doc;

import java.awt.Component;

public class ItemComponent
{

    public ItemComponent(Component component1, int i)
    {
        component = component1;
        itemIndex = i;
    }

    public int getIndex()
    {
        return itemIndex;
    }

    public Component getComponent()
    {
        return component;
    }

    private Component component;
    private int itemIndex;
}

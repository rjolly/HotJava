// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PlacesMessageLine.java

package sunw.hotjava.ui;

import java.awt.*;

public class PlacesMessageLine extends Panel
{

    public PlacesMessageLine()
    {
        setLayout(new BorderLayout());
        add("Center", message = new Label(""));
    }

    public void setMessage(String s)
    {
        message.setText(s);
    }

    public String getMessage()
    {
        return message.getText();
    }

    Label message;
}

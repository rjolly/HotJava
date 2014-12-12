// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MessageRequest.java

package sunw.hotjava.ui;


// Referenced classes of package sunw.hotjava.ui:
//            MessageLine

public class MessageRequest extends sunw.hotjava.misc.RequestProcessor.Request
{

    MessageRequest(MessageLine messageline, String s)
    {
        msgline = messageline;
        message = s;
    }

    public void execute()
    {
        msgline.setMessage(message);
    }

    MessageLine msgline;
    String message;
}

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AnimationIcon.java

package sunw.hotjava.ui;


// Referenced classes of package sunw.hotjava.ui:
//            AnimationIcon

class StopRequest extends sunw.hotjava.misc.RequestProcessor.Request
{

    public StopRequest(AnimationIcon animationicon)
    {
        icon = animationicon;
    }

    public void execute()
    {
        if(icon.isLastRequest(this))
            icon.checkCount();
    }

    private AnimationIcon icon;
}

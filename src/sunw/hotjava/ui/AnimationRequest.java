// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AnimationRequest.java

package sunw.hotjava.ui;


// Referenced classes of package sunw.hotjava.ui:
//            AnimationIcon

public class AnimationRequest extends sunw.hotjava.misc.RequestProcessor.Request
{

    AnimationRequest(AnimationIcon animationicon)
    {
        iconArea = animationicon;
    }

    public void execute()
    {
        iconArea.paintUpdate();
    }

    AnimationIcon iconArea;
}

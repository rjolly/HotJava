// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MeasureState.java

package sunw.hotjava.doc;


// Referenced classes of package sunw.hotjava.doc:
//            TraversalState

public class MeasureState extends TraversalState
{

    public MeasureState()
    {
        measurementInvalid = false;
    }

    public boolean measurementInvalid;
    public int margin;
}

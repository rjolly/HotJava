// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ContentModelState.java

package sunw.html;


// Referenced classes of package sunw.html:
//            ContentModel, Element

final class ContentModelState
{

    public ContentModelState(ContentModel contentmodel)
    {
        this(contentmodel, null, 0L);
    }

    ContentModelState(Object obj, ContentModelState contentmodelstate)
    {
        this(obj, contentmodelstate, 0L);
    }

    ContentModelState(Object obj, ContentModelState contentmodelstate, long l)
    {
        model = (ContentModel)obj;
        next = contentmodelstate;
        value = l;
    }

    public ContentModel getModel()
    {
        ContentModel contentmodel = model;
        for(int i = 0; (long)i < value; i++)
            if(contentmodel.next != null)
                contentmodel = contentmodel.next;
            else
                return null;

        return contentmodel;
    }

    public boolean terminate()
    {
        switch(model.type)
        {
        case 43: // '+'
            if(value == 0L && !model.empty())
                return false;
            // fall through

        case 42: // '*'
        case 63: // '?'
            return next == null || next.terminate();

        case 124: // '|'
            for(ContentModel contentmodel = (ContentModel)model.content; contentmodel != null; contentmodel = contentmodel.next)
                if(contentmodel.empty())
                    return next == null || next.terminate();

            return false;

        case 38: // '&'
            ContentModel contentmodel1 = (ContentModel)model.content;
            int i = 0;
            for(; contentmodel1 != null; contentmodel1 = contentmodel1.next)
            {
                if((value & 1L << i) == 0L && !contentmodel1.empty())
                    return false;
                i++;
            }

            return next == null || next.terminate();

        case 44: // ','
            ContentModel contentmodel2 = (ContentModel)model.content;
            for(int j = 0; (long)j < value;)
            {
                j++;
                contentmodel2 = contentmodel2.next;
            }

            for(; contentmodel2 != null && contentmodel2.empty(); contentmodel2 = contentmodel2.next);
            if(contentmodel2 != null)
                return false;
            return next == null || next.terminate();

        default:
            return false;
        }
    }

    public Element first()
    {
        switch(model.type)
        {
        case 38: // '&'
        case 42: // '*'
        case 63: // '?'
        case 124: // '|'
            return null;

        case 43: // '+'
            return model.first();

        case 44: // ','
            ContentModel contentmodel = (ContentModel)model.content;
            for(int i = 0; (long)i < value;)
            {
                i++;
                contentmodel = contentmodel.next;
            }

            return contentmodel.first();
        }
        return model.first();
    }

    public ContentModelState advance(Object obj)
    {
        switch(model.type)
        {
        case 43: // '+'
            if(model.first(obj))
                return (new ContentModelState(model.content, new ContentModelState(model, next, value + 1L))).advance(obj);
            if(value != 0L)
                if(next != null)
                    return next.advance(obj);
                else
                    return null;
            break;

        case 42: // '*'
            if(model.first(obj))
                return (new ContentModelState(model.content, this)).advance(obj);
            if(next != null)
                return next.advance(obj);
            else
                return null;

        case 63: // '?'
            if(model.first(obj))
                return (new ContentModelState(model.content, next)).advance(obj);
            if(next != null)
                return next.advance(obj);
            else
                return null;

        case 124: // '|'
            for(ContentModel contentmodel = (ContentModel)model.content; contentmodel != null; contentmodel = contentmodel.next)
                if(contentmodel.first(obj))
                    return (new ContentModelState(contentmodel, next)).advance(obj);

            break;

        case 44: // ','
            ContentModel contentmodel1 = (ContentModel)model.content;
            for(int i = 0; (long)i < value;)
            {
                i++;
                contentmodel1 = contentmodel1.next;
            }

            if(!contentmodel1.first(obj) && !contentmodel1.empty())
                break;
            if(contentmodel1.next == null)
                return (new ContentModelState(contentmodel1, next)).advance(obj);
            else
                return (new ContentModelState(contentmodel1, new ContentModelState(model, next, value + 1L))).advance(obj);

        case 38: // '&'
            ContentModel contentmodel2 = (ContentModel)model.content;
            boolean flag = true;
            int j = 0;
            for(; contentmodel2 != null; contentmodel2 = contentmodel2.next)
            {
                if((value & 1L << j) == 0L)
                {
                    if(contentmodel2.first(obj))
                        return (new ContentModelState(contentmodel2, new ContentModelState(model, next, value | 1L << j))).advance(obj);
                    if(!contentmodel2.empty())
                        flag = false;
                }
                j++;
            }

            if(!flag)
                break;
            if(next != null)
                return next.advance(obj);
            else
                return null;

        default:
            if(model.content == obj)
                return next;
            break;
        }
        return null;
    }

    private ContentModel model;
    private long value;
    private ContentModelState next;
}

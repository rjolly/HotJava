// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FlexyText.java

package sunw.hotjava.doc;

import java.text.CharacterIterator;

// Referenced classes of package sunw.hotjava.doc:
//            FlexyText

class TextCharIterator
    implements CharacterIterator
{

    TextCharIterator(char ac[], int i)
    {
        data = ac;
        offset = i;
        startOffset = i;
    }

    public Object clone()
    {
        return new TextCharIterator(data, offset);
    }

    public char current()
    {
        if(offset >= data.length)
            return '\uFFFF';
        else
            return data[offset];
    }

    public int getBeginIndex()
    {
        return startOffset;
    }

    public int getEndIndex()
    {
        return data.length;
    }

    public int getIndex()
    {
        return offset;
    }

    public char first()
    {
        offset = startOffset;
        return data[offset];
    }

    public char last()
    {
        offset = data.length - 1;
        return data[offset];
    }

    public char next()
    {
        offset++;
        if(offset >= data.length)
        {
            offset = data.length;
            return '\uFFFF';
        } else
        {
            return data[offset];
        }
    }

    public char previous()
    {
        offset--;
        if(offset < startOffset)
        {
            offset = startOffset;
            return '\uFFFF';
        } else
        {
            return data[offset];
        }
    }

    public char setIndex(int i)
    {
        offset = i;
        if(offset < startOffset)
            offset = startOffset;
        else
        if(offset >= data.length)
            offset = data.length - 1;
        return data[offset];
    }

    char data[];
    int startOffset;
    int offset;
}

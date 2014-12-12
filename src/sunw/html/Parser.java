// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Parser.java

package sunw.html;

import java.io.*;
import java.util.*;

// Referenced classes of package sunw.html:
//            AttributeList, Attributes, ContentModel, ContentModelState, 
//            DTD, DTDConstants, Element, Entity, 
//            IncorrectReaderException, Tag, TagStack, UnknownTag

public class Parser
    implements DTDConstants
{

    protected int getCurrentLine()
    {
        return ln;
    }

    protected char[] getText()
    {
        return text;
    }

    protected String getTextAsString()
    {
        return new String(text, 0, textpos);
    }

    protected boolean isInContext(String s)
    {
        TagStack tagstack;
        for(tagstack = stack; tagstack != null && !tagstack.elem.getName().equals(s); tagstack = tagstack.next);
        return tagstack != null;
    }

    protected Tag makeTag(Element element, Attributes attributes)
    {
        return new UnknownTag(element, attributes);
    }

    private void handleText(char ac[])
    {
        handleText(ac, null);
    }

    protected void handleText(char ac[], Tag tag)
    {
        Thread.dumpStack();
        System.out.println("##Parser.handleText: ");
    }

    protected void handleTitle(char ac[], Tag tag)
    {
        handleText(ac, tag);
    }

    protected void handleCommentOrScript(char ac[])
    {
    }

    protected void handleEOFInComment()
    {
        int i = strIndexOf('\n');
        if(i >= 0)
        {
            handleCommentOrScript(getChars(0, i));
            try
            {
                in.close();
                in = new CharArrayReader(getChars(i + 1));
                ch = 62;
            }
            catch(IOException _ex)
            {
                error("ioexception");
            }
            resetStrBuffer();
            return;
        } else
        {
            error("eof.comment");
            return;
        }
    }

    protected void handleEmptyTag(Tag tag)
        throws IncorrectReaderException
    {
    }

    protected void handleStartTag(Tag tag)
    {
        System.out.println("##handleStartTag: " + tag);
    }

    protected void handleBodyTagAttributes(Attributes attributes)
    {
    }

    protected void handleFormTagInTable(Tag tag)
    {
    }

    protected void handleEndTag(Tag tag)
    {
    }

    protected void handleError(int i, String s)
    {
        System.out.println("**** " + stack);
        System.out.println("line " + i + ": error: " + s);
        System.out.println();
    }

    private void handleText(Tag tag)
    {
        if(tag.isBlock())
            space = false;
        if(textpos == 0 && (!space || stack == null || last.isBlock() || !stack.advance(dtd.pcdata)))
        {
            last = tag;
            space = false;
            return;
        }
        if(space)
        {
            if(textpos + 1 > text.length)
            {
                char ac[] = new char[text.length + 200];
                System.arraycopy(text, 0, ac, 0, text.length);
                text = ac;
            }
            text[textpos++] = ' ';
            space = false;
        }
        char ac1[] = new char[textpos];
        System.arraycopy(text, 0, ac1, 0, textpos);
        if(tag.getElement().getName().equals("title"))
            handleTitle(ac1, tag);
        else
            handleText(ac1, tag);
        textpos = 0;
        last = tag;
        space = false;
    }

    protected void error(String s, String s1, String s2, String s3)
    {
        handleError(ln, s + s1 + s2 + s3);
    }

    protected void error(String s, String s1, String s2)
    {
        error(s, s1, s2, "?");
    }

    protected void error(String s, String s1)
    {
        error(s, s1, "?", "?");
    }

    protected void error(String s)
    {
        error(s, "?", "?", "?");
    }

    protected Tag getEnclosingForm()
    {
        TagStack tagstack;
        for(tagstack = stack; tagstack != null && !tagstack.elem.getName().equals("form"); tagstack = tagstack.next);
        if(tagstack != null)
            return tagstack.tag;
        else
            return null;
    }

    protected void startTag(Tag tag)
        throws IncorrectReaderException
    {
        Element element = tag.getElement();
        Attributes attributes = tag.getAttributes();
        if(!isNonFramed && !isFramed && stack != null && stack.elem == dtd.body && !element.getName().equals("frameset") && !element.getName().equals("noscript") && !element.getName().equals("script"))
            isNonFramed = true;
        handleText(tag);
        for(AttributeList attributelist = element.atts; attributelist != null; attributelist = attributelist.next)
            if(attributelist.modifier == 2 && (attributes == null || attributes.get(attributelist.name) == null))
                error("req.att", attributelist.getName(), element.getName());

        if(element.isEmpty())
        {
            handleEmptyTag(tag);
        } else
        {
            recent = element;
            stack = new TagStack(tag, stack);
            handleStartTag(tag);
        }
        if(haveOverlaps)
        {
            haveOverlaps = false;
            while(!overlapTags.empty()) 
            {
                Tag tag1 = (Tag)overlapTags.pop();
                if(needToReopenTag(tag, tag1) && stack.advance(tag1.getElement()))
                    startTag(makeTag(tag1.getElement(), tag1.getAttributes()));
            }
        }
    }

    protected boolean needToReopenTag(Tag tag, Tag tag1)
    {
        return false;
    }

    protected void endTag(boolean flag)
    {
        handleText(stack.tag);
        if(flag && !stack.elem.omitEnd())
            error("end.missing", stack.elem.getName());
        else
        if(!stack.terminate())
            error("end.unexpected", stack.elem.getName());
        handleEndTag(stack.tag);
        stack = stack.next;
        recent = stack == null ? null : stack.elem;
    }

    private boolean ignoreElement(Element element)
    {
        String s = stack.elem.getName();
        String s1 = element.getName();
        if(s1.equals("html") && seenHtml || s1.equals("head") && seenHead || s1.equals("body") && seenBody)
            return true;
        if(s1.equals("dt") || s1.equals("dd"))
        {
            TagStack tagstack;
            for(tagstack = stack; tagstack != null && !tagstack.elem.getName().equals("dl"); tagstack = tagstack.next);
            if(tagstack == null)
                return true;
        }
        return s1.equals("font") && (s.equals("ul") || s.equals("ol")) || s1.equals("meta") && stack != null && !s.equals("html") || s1.equals("style") || s.equals("frameset");
    }

    protected void setTableSurroundedByForm(Tag tag)
    {
    }

    protected void incrementNumberFormsToEnd(Tag tag)
    {
    }

    protected void checkForSurroundedTable(Element element, Tag tag)
    {
    }

    protected void doNecessarySurroundedTableCleanup()
    {
    }

    protected void markFirstTime(Element element)
    {
        String s = element.getName();
        if(s.equals("html"))
        {
            seenHtml = true;
            return;
        }
        if(s.equals("head"))
        {
            seenHead = true;
            return;
        }
        if(s.equals("body"))
        {
            seenBody = true;
            return;
        }
        if(s.equals("frameset") && !isNonFramed)
            isFramed = true;
    }

    private boolean legalElementContext(Element element)
        throws IncorrectReaderException
    {
        if(stack == null)
            if(element != dtd.html)
            {
                startTag(makeTag(dtd.html, null));
                seenHtml = true;
                return legalElementContext(element);
            } else
            {
                return true;
            }
        if(isFramed)
        {
            ContentModelState contentmodelstate = (new ContentModelState(dtd.frameset.getContent())).advance(element);
            if(contentmodelstate == null)
            {
                skipTag = true;
                return true;
            }
        }
        if((isNonFramed || textpos != 0 || isFramed && !stack.elem.getName().equals("frameset")) && element.getName().equals("frameset"))
        {
            isNonFramed = true;
            skipTag = true;
            return true;
        }
        if(element.getName().equals("form"))
        {
            TagStack tagstack;
            for(tagstack = stack; tagstack != null && !tagstack.elem.getName().equals("table"); tagstack = tagstack.next);
            if(tagstack != null)
            {
                setTableSurroundedByForm(tagstack.tag);
                ignoreFormEnd = true;
                formNeedsPlacing = true;
                return true;
            }
        }
        if(stack.elem == dtd.head && element == dtd.head)
        {
            skipTag = true;
            return true;
        }
        if(stack.advance(element))
        {
            markFirstTime(element);
            return true;
        }
        if(element.getName().equals("frame"))
        {
            skipTag = true;
            return false;
        }
        boolean flag = false;
        String s = stack.elem.getName();
        String s1 = element.getName();
        if(s1.equals("script"))
            return true;
        if(s.equals("table") && s1.equals("td") || s.equals("table") && s1.equals("th") || s.equals("tr") && !s1.equals("tr"))
            flag = true;
        if(!flag && (stack.elem.getName() != element.getName() || element.getName().equals("body") || element.getName().equals("table")) && (skipTag = ignoreElement(element)))
        {
            error("tag.ignore", element.getName());
            return skipTag;
        }
        if(s.equals("table") && !s1.equals("tr") && !s1.equals("td") && !s1.equals("th") && !s1.equals("caption"))
        {
            Element element1 = dtd.getElement("td");
            Tag tag = makeTag(element1, null);
            legalTagContext(tag);
            startTag(tag);
            error("start.missing", element.getName());
            return legalElementContext(element);
        }
        if(s.equals("option") && !s1.equals("option"))
        {
            skipTag = true;
            return skipTag;
        }
        boolean flag1 = false;
        if(!flag && stack.terminate())
        {
            for(TagStack tagstack1 = stack.next; tagstack1 != null; tagstack1 = tagstack1.next)
            {
                if(tagstack1.advance(element))
                {
                    while(stack != tagstack1) 
                    {
                        overlapTags.push(stack.tag);
                        haveOverlaps = true;
                        endTag(true);
                    }
                    return true;
                }
                if(tagstack1.terminate() && tagstack1.next != null)
                    continue;
                flag1 = true;
                break;
            }

        }
        if(stack != null && stack.elem.getName().equalsIgnoreCase("select") && element.getName().equalsIgnoreCase("#pcdata"))
            return false;
        Element element2 = stack.first();
        if(element2 != null && (element2 != dtd.head || element != dtd.pcdata) && (element2 != dtd.head || element != dtd.script))
        {
            Tag tag1 = makeTag(element2, null);
            legalTagContext(tag1);
            startTag(tag1);
            if(!element2.omitStart())
                error("start.missing", element.getName());
            return legalElementContext(element);
        }
        ContentModel contentmodel = stack.contentModel();
        Vector vector = new Vector();
        if(contentmodel != null)
        {
            contentmodel.getElements(vector);
            for(Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();)
            {
                Element element3 = (Element)enumeration.nextElement();
                if(!stack.excluded(element3.getIndex()))
                {
                    boolean flag2 = false;
                    for(AttributeList attributelist = element3.getAttributes(); attributelist != null; attributelist = attributelist.next)
                    {
                        if(attributelist.modifier != 2)
                            continue;
                        flag2 = true;
                        break;
                    }

                    if(!flag2)
                    {
                        ContentModel contentmodel1 = element3.getContent();
                        if(contentmodel1 != null && contentmodel1.first(element))
                        {
                            Tag tag2 = makeTag(element3, null);
                            legalTagContext(tag2);
                            startTag(tag2);
                            error("start.missing", element3.getName());
                            return legalElementContext(element);
                        }
                    }
                }
            }

        }
        if(flag1 && seenBody)
        {
            skipTag = true;
            return true;
        }
        if(stack.terminate() && stack.elem != dtd.body)
        {
            if(!stack.elem.omitEnd())
                error("end.missing", element.getName());
            endTag(true);
            return legalElementContext(element);
        } else
        {
            return false;
        }
    }

    private void legalTagContext(Tag tag)
        throws IncorrectReaderException
    {
        skipTag = false;
        if(legalElementContext(tag.getElement()))
        {
            markFirstTime(tag.getElement());
            return;
        }
        if(tag.isBlock() && stack != null && !stack.tag.isBlock())
        {
            endTag(true);
            legalTagContext(tag);
            return;
        }
        for(TagStack tagstack = stack; tagstack != null; tagstack = tagstack.next)
            if(tagstack.tag.getElement() == dtd.head)
            {
                while(stack != tagstack) 
                    endTag(true);
                endTag(true);
                legalTagContext(tag);
                return;
            }

        error("tag.unexpected", tag.getElement().getName());
    }

    private void errorContext()
        throws IncorrectReaderException
    {
        for(; stack != null && stack.tag.getElement() != dtd.body; stack = stack.next)
            handleEndTag(stack.tag);

        if(stack == null)
        {
            legalElementContext(dtd.body);
            startTag(makeTag(dtd.body, null));
        }
    }

    private void addString(int i)
    {
        if(strpos == str.length)
        {
            char ac[] = new char[str.length + 128];
            System.arraycopy(str, 0, ac, 0, str.length);
            str = ac;
        }
        str[strpos++] = (char)i;
    }

    private String getString(int i)
    {
        int j = strpos - i;
        strpos = i;
        return new String(str, i, j);
    }

    private char[] getChars(int i)
    {
        char ac[] = new char[strpos - i];
        System.arraycopy(str, i, ac, 0, strpos - i);
        strpos = i;
        return ac;
    }

    private char[] getChars(int i, int j)
    {
        char ac[] = new char[j - i];
        System.arraycopy(str, i, ac, 0, j - i);
        return ac;
    }

    private void resetStrBuffer()
    {
        strpos = 0;
    }

    private int strIndexOf(char c)
    {
        for(int i = 0; i < strpos; i++)
            if(str[i] == c)
                return i;

        return -1;
    }

    private void skipSpace()
        throws IOException
    {
label0:
        do
            switch(ch)
            {
            default:
                break label0;

            case 10: // '\n'
                ln++;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;

            case 13: // '\r'
                ln++;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(ch == 10)
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;

            case 9: // '\t'
            case 32: // ' '
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;
            }
        while(true);
    }

    private void skipTillAttributeEnd()
        throws IOException
    {
        do
        {
            if(Character.isLetter((char)ch))
                return;
            switch(ch)
            {
            case 9: // '\t'
            case 10: // '\n'
            case 13: // '\r'
            case 32: // ' '
            case 60: // '<'
            case 62: // '>'
                return;
            }
            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
        } while(true);
    }

    private boolean parseIdentifier(boolean flag)
        throws IOException
    {
        int i = str.length;
        if((ch < 97 || ch > 122) && (ch < 65 || ch > 90))
            return false;
        for(; ch >= 97 && ch <= 122 || ch >= 65 && ch <= 90 || ch >= 48 && ch <= 57 || ch == 46 || ch == 45 || ch == 95; ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++])))
        {
            if(flag && ch >= 65 && ch <= 90)
                ch = 97 + (ch - 65);
            if(strpos < i)
            {
                str[strpos++] = (char)ch;
            } else
            {
                addString(ch);
                i = str.length;
            }
        }

        return true;
    }

    protected char[] handleScript(char ac[], String s)
    {
        return null;
    }

    private char[] parseEntityReference()
        throws IOException
    {
        int i = strpos;
        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
        if(ch == 35)
        {
            int j = 0;
            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
            if(ch >= 48 && ch <= 57)
            {
                for(; ch >= 48 && ch <= 57; ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++])))
                    j = (j * 10 + ch) - 48;

                switch(ch)
                {
                case 10: // '\n'
                    ln++;
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    break;

                case 13: // '\r'
                    ln++;
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    if(ch == 10)
                        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    break;

                case 59: // ';'
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    break;
                }
                if(j > 129 && j < 160)
                    j = map[j - 130];
                char ac1[] = {
                    (char)j
                };
                return ac1;
            }
            addString(35);
            if(!parseIdentifier(false))
            {
                error("ident.expected");
                strpos = i;
                char ac2[] = {
                    '&', '#'
                };
                return ac2;
            }
        } else
        {
            if(ch == 123)
            {
                StringBuffer stringbuffer = new StringBuffer();
                for(ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++])); ch != 125; ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++])))
                    stringbuffer.append((char)ch);

                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(ch == 59)
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                return handleScript(stringbuffer.toString().toCharArray(), null);
            }
            if(!parseIdentifier(false))
            {
                char ac[] = {
                    '&'
                };
                return ac;
            }
        }
        switch(ch)
        {
        default:
            break;

        case 10: // '\n'
            ln++;
            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
            break;

        case 13: // '\r'
            ln++;
            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
            if(ch == 10)
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
            break;

        case 59: // ';'
            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
            break;
        }
        String s = getString(i);
        Entity entity = dtd.getEntity(s);
        if(entity == null)
            entity = dtd.getEntity(s.toLowerCase());
        if(entity == null || !entity.isGeneral())
        {
            String s1 = null;
            if(props == null || !matchesInterestedType() || (s1 = props.getProperty(s)) == null)
            {
                if(s.length() == 0)
                {
                    error("invalid.entref", s);
                    return new char[0];
                }
                String s2 = null;
                if(props != null)
                    s2 = (String)props.get("url");
                if(matchesInterestedType() || s2 != null && s2.startsWith("doc"))
                {
                    if(props != null)
                        s1 = props.getProperty(s);
                    if(s1 == null)
                        s1 = System.getProperty(s);
                }
                if(s1 == null)
                    s1 = "&" + s;
            }
            char ac3[] = new char[s1.length()];
            s1.getChars(0, ac3.length, ac3, 0);
            return ac3;
        } else
        {
            return entity.getData();
        }
    }

    private void parseComment()
        throws IOException
    {
        int i = 0;
        boolean flag = false;
        int j = 0;
        int k = 0;
        for(; ch == 45; ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++])))
            k++;

        if(k > 1)
            switch(ch)
            {
            case 62: // '>'
                return;

            case 33: // '!'
                if((ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]))) == 62)
                    return;
                break;
            }
        boolean flag1 = false;
        do
        {
            int l = ch;
            switch(l)
            {
            case 45: // '-'
                if(flag)
                {
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    break;
                }
                if(strpos != 0 && str[strpos - 1] == '-' || flag1)
                {
                    flag1 = false;
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    if(ch == 62)
                        return;
                    if(ch == 33)
                    {
                        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                        if(ch == 62)
                            return;
                        if(i != 0)
                        {
                            addString(45);
                            addString(33);
                        }
                    } else
                    {
                        if(ch == 45 && i == 0)
                            flag1 = true;
                        break;
                    }
                } else
                {
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    if(ch != 45)
                        break;
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    if(ch == 62)
                        return;
                    if(ch == 33)
                    {
                        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                        if(ch == 62)
                            return;
                        if(i != 0)
                        {
                            addString(45);
                            addString(33);
                        }
                    } else
                    {
                        if(i != 0)
                            addString(45);
                        else
                            flag1 = true;
                        break;
                    }
                }
                continue;

            case -1: 
                handleEOFInComment();
                return;

            case 10: // '\n'
                ln++;
                i++;
                flag = false;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;

            case 62: // '>'
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;

            case 13: // '\r'
                ln++;
                i++;
                flag = false;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(ch == 10)
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                l = 10;
                break;

            case 34: // '"'
            case 39: // '\''
                if(i != 0)
                {
                    if(parsingScript)
                        if(flag)
                        {
                            if(j == l)
                                flag = false;
                        } else
                        {
                            j = l;
                            flag = true;
                        }
                    addString(l);
                }
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                continue;

            default:
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;
            }
            if(i != 0)
                addString(l);
        } while(true);
    }

    private char[] parseScript()
        throws IOException
    {
        parsingScript = true;
        do
        {
            int j = ch;
            if(j == 60)
            {
                if((ch = readCh()) == 33 && (ch = readCh()) == 45)
                {
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    parseComment();
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    continue;
                }
                if(ch == 47)
                {
                    addString(j);
                    j = ch;
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    if(ch == 115 || ch == 83)
                    {
                        addString(j);
                        j = ch;
                        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                        if(ch == 99 || ch == 67)
                        {
                            addString(j);
                            j = ch;
                            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                            if(ch == 114 || ch == 82)
                            {
                                addString(j);
                                j = ch;
                                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                                if(ch == 105 || ch == 73)
                                {
                                    addString(j);
                                    j = ch;
                                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                                    if(ch == 112 || ch == 80)
                                    {
                                        addString(j);
                                        j = ch;
                                        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                                        if(ch == 116 || ch == 84)
                                        {
                                            addString(j);
                                            j = ch;
                                            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                                            int i;
                                            for(i = 0; ch == 32 || ch == 10 || ch == 13; i++)
                                            {
                                                addString(j);
                                                j = ch;
                                                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                                            }

                                            if(ch == 62)
                                            {
                                                ch = 32;
                                                parsingScript = false;
                                                return getChars(0, strpos - "</scrip".length() - i);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else
            {
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
            }
            if(j == -1)
            {
                parsingScript = false;
                return getChars(0);
            }
            addString(j);
        } while(true);
    }

    void parseLiteral(boolean flag)
        throws IOException
    {
        do
        {
            int i = ch;
            switch(i)
            {
            case -1: 
                error("eof.literal", stack.elem.getName());
                endTag(true);
                return;

            case 62: // '>'
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                int j = textpos - (stack.elem.name.length() + 2);
                int k = 0;
                if(j >= 0 && text[j++] == '<' && text[j] == '/')
                {
                    while(++j < textpos && Character.toLowerCase(text[j]) == stack.elem.name.charAt(k++)) ;
                    if(j == textpos)
                    {
                        textpos -= stack.elem.name.length() + 2;
                        if(textpos > 0 && text[textpos - 1] == '\n')
                            textpos--;
                        if(stack.elem.name.equalsIgnoreCase("xmp"))
                            modeXMP = false;
                        endTag(false);
                        return;
                    }
                }
                break;

            case 38: // '&'
                if(modeXMP)
                {
                    ch = readCh();
                    break;
                }
                char ac1[] = parseEntityReference();
                if(textpos + ac1.length > text.length)
                {
                    char ac2[] = new char[Math.max(textpos + ac1.length + 128, text.length * 2)];
                    System.arraycopy(text, 0, ac2, 0, text.length);
                    text = ac2;
                }
                System.arraycopy(ac1, 0, text, textpos, ac1.length);
                textpos += ac1.length;
                continue;

            case 10: // '\n'
                ln++;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;

            case 13: // '\r'
                ln++;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(ch == 10)
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                i = 10;
                break;

            default:
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;
            }
            if(textpos == text.length)
            {
                char ac[] = new char[text.length + 128];
                System.arraycopy(text, 0, ac, 0, text.length);
                text = ac;
            }
            text[textpos++] = (char)i;
        } while(true);
    }

    private String parseAttributeValue(boolean flag)
        throws IOException
    {
        int i = -1;
        switch(ch)
        {
        case 34: // '"'
        case 39: // '\''
            i = ch;
            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
            break;
        }
        do
        {
            int j = ch;
            switch(j)
            {
            case 10: // '\n'
                ln++;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(i < 0)
                    return getString(0);
                break;

            case 13: // '\r'
                ln++;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(ch == 10)
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(i < 0)
                    return getString(0);
                break;

            case 9: // '\t'
                if(i < 0)
                    j = 32;
                // fall through

            case 32: // ' '
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(i < 0)
                    return getString(0);
                break;

            case 60: // '<'
            case 62: // '>'
                if(i < 0)
                    return getString(0);
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;

            case 34: // '"'
            case 39: // '\''
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(j == i)
                {
                    skipTillAttributeEnd();
                    return getString(0).trim();
                }
                if(i == -1)
                    error("attvalerr");
                break;

            case 61: // '='
                if(i < 0)
                    error("attvalerr");
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;

            case 38: // '&'
                char ac[] = parseEntityReference();
                for(int k = 0; k < ac.length; k++)
                {
                    j = ac[k];
                    addString(!flag || j < 65 || j > 90 ? j : (97 + j) - 65);
                }

                continue;

            case -1: 
                return getString(0);

            default:
                if(flag && j >= 65 && j <= 90)
                    j = (97 + j) - 65;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;
            }
            addString(j);
        } while(true);
    }

    private Attributes parseAttributeSpecificationList(Element element)
        throws IOException
    {
        Attributes attributes = null;
        do
        {
            skipSpace();
            switch(ch)
            {
            case -1: 
            case 60: // '<'
            case 62: // '>'
                return attributes;
            }
            AttributeList attributelist = null;
            String s = null;
            String s1 = null;
            if(parseIdentifier(true))
            {
                s = getString(0);
                skipSpace();
                if(ch == 61)
                {
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    skipSpace();
                    attributelist = element.getAttribute(s);
                    s1 = parseAttributeValue(attributelist != null && attributelist.type != 1 && attributelist.type != 11 && attributelist.type != 7);
                } else
                {
                    s1 = s;
                    attributelist = element.getAttributeByValue(s1);
                    if(attributelist == null)
                    {
                        attributelist = element.getAttribute(s);
                        if(attributelist != null)
                            s1 = attributelist.getValue();
                    }
                }
            } else
            {
                if(ch == 44)
                {
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    continue;
                }
                if(ch == 34)
                {
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    skipSpace();
                    if(parseIdentifier(true))
                    {
                        s = getString(0);
                        if(ch == 34)
                            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                        skipSpace();
                        if(ch == 61)
                        {
                            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                            skipSpace();
                            attributelist = element.getAttribute(s);
                            s1 = parseAttributeValue(attributelist != null && attributelist.type != 1 && attributelist.type != 11);
                        } else
                        {
                            s1 = s;
                            attributelist = element.getAttributeByValue(s1);
                            if(attributelist == null)
                            {
                                attributelist = element.getAttribute(s);
                                if(attributelist != null)
                                    s1 = attributelist.getValue();
                            }
                        }
                    } else
                    {
                        char ac[] = {
                            (char)ch
                        };
                        error("invalid.tagchar", new String(ac), element.getName());
                        if(ch != 62)
                            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                        continue;
                    }
                } else
                if(attributes == null && ch == 61)
                {
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    skipSpace();
                    s = element.getName();
                    attributelist = element.getAttribute(s);
                    s1 = parseAttributeValue(attributelist != null && attributelist.type != 1 && attributelist.type != 11);
                } else
                {
                    if(ch == 61)
                    {
                        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                        skipSpace();
                        s1 = parseAttributeValue(true);
                        error("attvalerr");
                        return attributes;
                    }
                    char ac1[] = {
                        (char)ch
                    };
                    error("invalid.tagchar", new String(ac1), element.getName());
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    continue;
                }
            }
            if(attributelist != null)
                s = attributelist.getName();
            else
                error("invalid.tagatt", s, element.getName());
            if(attributes == null)
                attributes = new Attributes();
            else
            if(attributes.get(s) != null)
                error("multi.tagatt", s, element.getName());
            if(s1 == null)
                s1 = attributelist == null || attributelist.value == null ? "" : attributelist.value;
            else
            if(attributelist != null && attributelist.values != null && !attributelist.values.contains(s1))
                error("invalid.tagattval", s, element.getName());
            attributes.append(s, s1);
        } while(true);
    }

    public String parseDTDMarkup()
        throws IOException
    {
        StringBuffer stringbuffer = new StringBuffer();
        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
        do
            switch(ch)
            {
            case 62: // '>'
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                return stringbuffer.toString();

            case -1: 
                error("invalid.markup");
                return stringbuffer.toString();

            case 10: // '\n'
                ln++;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;

            case 34: // '"'
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;

            case 13: // '\r'
                ln++;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(ch == 10)
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;

            default:
                stringbuffer.append((char)(ch & 0xff));
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;
            }
        while(true);
    }

    protected boolean parseMarkupDeclarations(StringBuffer stringbuffer)
        throws IOException
    {
        if(stringbuffer.length() == "DOCTYPE".length() && stringbuffer.toString().toUpperCase().equals("DOCTYPE"))
        {
            parseDTDMarkup();
            return true;
        } else
        {
            return false;
        }
    }

    private void parseInvalidTag()
        throws IOException
    {
        do
        {
            skipSpace();
            switch(ch)
            {
            case -1: 
            case 62: // '>'
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                return;

            case 60: // '<'
                return;
            }
            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
        } while(true);
    }

    private void parseTag()
        throws IOException
    {
        Element element = null;
        boolean flag = false;
        boolean flag1 = false;
        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
        switch(ch)
        {
        case 33: // '!'
            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
            StringBuffer stringbuffer;
            switch(ch)
            {
            case 45: // '-'
                while(true) 
                {
                    while(ch == 45) 
                    {
                        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                        parseComment();
                        handleCommentOrScript(getChars(0));
                    }
                    skipSpace();
                    switch(ch)
                    {
                    case 62: // '>'
                        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                        // fall through

                    case -1: 
                        return;

                    default:
                        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                        if(!flag1)
                        {
                            flag1 = true;
                            error("invalid.commentchar", String.valueOf((char)ch));
                        }
                        break;

                    case 45: // '-'
                        break;
                    }
                }
                // fall through

            default:
                stringbuffer = new StringBuffer();
                break;
            }
            do
            {
                stringbuffer.append((char)ch);
                if(parseMarkupDeclarations(stringbuffer))
                    return;
                switch(ch)
                {
                case 62: // '>'
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    // fall through

                case -1: 
                    error("invalid.markup");
                    return;

                case 10: // '\n'
                    ln++;
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    break;

                case 13: // '\r'
                    ln++;
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    if(ch == 10)
                        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    break;

                default:
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    break;
                }
            } while(true);

        case 47: // '/'
            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
            switch(ch)
            {
            case 62: // '>'
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                // fall through

            case 60: // '<'
                if(recent == null)
                {
                    error("invalid.shortend");
                    return;
                }
                element = recent;
                break;

            case 61: // '='
            default:
                if(!parseIdentifier(true))
                {
                    error("expected.endtagname");
                    return;
                }
                skipSpace();
                switch(ch)
                {
                case 60: // '<'
                    break;

                case 62: // '>'
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    break;

                case 61: // '='
                default:
                    error("expected", "'>'");
                    for(; ch != -1 && ch != 10 && ch != 62; ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++])));
                    if(ch == 62)
                        ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                    break;
                }
                String s = getString(0);
                if(!dtd.elementExists(s))
                {
                    error("end.unrecognized", s);
                    if(textpos > 0 && text[textpos - 1] == '\n')
                        textpos--;
                    return;
                }
                element = dtd.getElement(s);
                break;
            }
            if(stack == null)
            {
                error("end.extra.tag", element.getName());
                return;
            }
            if(textpos > 0 && text[textpos - 1] == '\n')
                if(stack.pre)
                {
                    if(textpos > 1 && text[textpos - 2] != '\n')
                        textpos--;
                } else
                {
                    textpos--;
                }
            String s1 = stack.elem.getName();
            if(element.getName().equals("form"))
            {
                TagStack tagstack = stack;
                if(ignoreFormEnd)
                {
                    ignoreFormEnd = false;
                    for(; tagstack != null && !tagstack.elem.getName().equals("table"); tagstack = tagstack.next);
                    if(tagstack != null)
                    {
                        incrementNumberFormsToEnd(tagstack.tag);
                        return;
                    }
                } else
                {
                    for(; tagstack != null && element != tagstack.elem; tagstack = tagstack.next);
                    if(tagstack == null)
                        return;
                    for(; stack != tagstack && stack.elem.omitEnd(); endTag(true))
                    {
                        s1 = stack.elem.getName();
                        if(s1.equals("tr") || s1.equals("th") || s1.equals("td"))
                            break;
                    }

                    if(stack.elem == element)
                        endTag(false);
                }
                return;
            }
            if(s1.equals("table") && !element.getName().equals(s1))
            {
                error("tag.ignore", element.getName());
                return;
            }
            if((s1.equals("tr") || s1.equals("th") || s1.equals("td")) && !element.getName().equals("table") && !element.getName().equals(s1))
            {
                error("tag.ignore", element.getName());
                return;
            }
            TagStack tagstack1;
            for(tagstack1 = stack; tagstack1 != null && element != tagstack1.elem; tagstack1 = tagstack1.next);
            if(tagstack1 == null)
            {
                error("unmatched.endtag", element.getName());
                return;
            }
            String s3 = element.getName();
            if(stack != tagstack1 && (s3.equals("font") || s3.equals("center")))
            {
                if(s3.equals("center"))
                {
                    for(TagStack tagstack3 = stack; tagstack3 != tagstack1; tagstack3 = tagstack3.next)
                        if("table".equals(tagstack3.elem.getName()))
                            return;

                }
                for(; stack.elem.omitEnd() && stack != tagstack1; endTag(true));
                if(stack.elem == element)
                    endTag(false);
                return;
            }
            while(stack != tagstack1) 
                endTag(true);
            checkForSurroundedTable(element, stack.tag);
            endTag(false);
            if(element.getName().equals("table") && stack.elem.getName().equals("form"))
            {
                doNecessarySurroundedTableCleanup();
                ignoreFormEnd = false;
            }
            return;

        case -1: 
            error("eof");
            return;
        }
        if(!parseIdentifier(true))
        {
            needAngleBracket = true;
            element = recent;
            if(ch != 62 || element == null)
            {
                error("expected.tagname");
                return;
            }
        } else
        {
            String s2 = getString(0);
            if(s2.equals("image"))
                s2 = new String("img");
            if(!dtd.elementExists(s2))
            {
                parseInvalidTag();
                error("tag.unrecognized", s2);
                return;
            }
            element = dtd.getElement(s2);
        }
        Attributes attributes = parseAttributeSpecificationList(element);
        switch(ch)
        {
        case 47: // '/'
            flag = true;
            // fall through

        case 62: // '>'
            ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
            break;

        default:
            error("expected", "'>'");
            break;

        case 60: // '<'
            break;
        }
        if(!element.isEmpty())
            if(ch == 10)
            {
                ln++;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
            } else
            if(ch == 13)
            {
                ln++;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(ch == 10)
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
            }
        Tag tag = makeTag(element, attributes);
        legalTagContext(tag);
        if(skipTag)
        {
            skipTag = false;
            if(element == dtd.body)
                handleBodyTagAttributes(attributes);
            return;
        }
        if(element == dtd.body)
            isNonFramed = true;
        if(formNeedsPlacing)
        {
            formNeedsPlacing = false;
            handleFormTagInTable(tag);
            TagStack tagstack2;
            for(tagstack2 = stack; !tagstack2.elem.getName().equals("table"); tagstack2 = tagstack2.next);
            TagStack tagstack4 = tagstack2.next;
            TagStack tagstack5 = new TagStack(tag, tagstack4);
            tagstack2.next = tagstack5;
            return;
        }
        if(element.getName().equalsIgnoreCase("xmp"))
            modeXMP = true;
        startTag(tag);
        if(!element.isEmpty())
        {
            switch(element.getType())
            {
            case 1: // '\001'
                parseLiteral(false);
                break;

            case 16: // '\020'
                parseLiteral(true);
                break;

            default:
                stack.net = flag;
                break;
            }
            if(element == dtd.script)
            {
                char ac[] = parseScript();
                handleCommentOrScript(ac);
                endTag(true);
                resetStrBuffer();
            }
        }
    }

    private void parseContent()
        throws IOException
    {
        Thread thread = Thread.currentThread();
        do
        {
            if(!ignoringInterrupts && thread.isInterrupted())
            {
                thread.interrupt();
                return;
            }
            int i = ch;
            switch(i)
            {
            case 60: // '<'
                parseTag();
                if(!needAngleBracket || !legalElementContext(dtd.pcdata))
                {
                    needAngleBracket = false;
                    continue;
                }
                break;

            case 47: // '/'
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(stack == null || !stack.net)
                    break;
                endTag(false);
                continue;

            case -1: 
                if(interruptBuffer != null)
                    ch = readCh();
                else
                    return;
                continue;

            case 38: // '&'
                if(textpos == 0)
                {
                    if(!legalElementContext(dtd.pcdata))
                        error("unexpected.pcdata");
                    if(last.isBlock())
                        space = false;
                }
                char ac[] = parseEntityReference();
                if(textpos + ac.length + 1 > text.length)
                {
                    char ac2[] = new char[Math.max(textpos + ac.length + 128, text.length * 2)];
                    System.arraycopy(text, 0, ac2, 0, text.length);
                    text = ac2;
                }
                if(space)
                {
                    space = false;
                    text[textpos++] = ' ';
                }
                System.arraycopy(ac, 0, text, textpos, ac.length);
                textpos += ac.length;
                continue;

            case 10: // '\n'
                ln++;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(stack != null && stack.pre)
                    break;
                space = true;
                continue;

            case 13: // '\r'
                ln++;
                i = 10;
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(ch == 10)
                    ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(stack != null && stack.pre)
                    break;
                space = true;
                continue;

            case 9: // '\t'
            case 32: // ' '
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                if(stack != null && stack.pre)
                    break;
                space = true;
                continue;

            default:
                if(textpos == 0)
                {
                    if(!legalElementContext(dtd.pcdata))
                        error("unexpected.pcdata");
                    if(last.isBlock())
                        space = false;
                }
                ch = pos >= len || interruptBuffer != null ? readCh() : ((int) (buf[pos++]));
                break;
            }
            if(textpos + 2 > text.length)
            {
                char ac1[] = new char[2 * text.length];
                System.arraycopy(text, 0, ac1, 0, text.length);
                text = ac1;
            }
            if(space)
            {
                text[textpos++] = ' ';
                space = false;
            }
            if(needAngleBracket)
            {
                text[textpos++] = '<';
                needAngleBracket = false;
            } else
            {
                text[textpos++] = (char)i;
            }
        } while(true);
    }

    public synchronized void parse(Reader reader, DTD dtd1)
        throws IOException
    {
        in = reader;
        dtd = dtd1;
        ln = 1;
        boolean flag = true;
        initDocumentState();
        try
        {
            try
            {
                ch = readCh();
                text = new char[1024];
                str = new char[128];
                parseContent();
                while(stack != null) 
                    endTag(true);
            }
            catch(IncorrectReaderException incorrectreaderexception1)
            {
                flag = false;
                throw incorrectreaderexception1;
            }
            finally
            {
                if(flag)
                    reader.close();
            }
        }
        catch(IncorrectReaderException incorrectreaderexception)
        {
            throw incorrectreaderexception;
        }
        catch(IOException ioexception)
        {
            errorContext();
            error("ioexception");
            throw ioexception;
        }
        catch(Exception exception2)
        {
            errorContext();
            error("exception", exception2.getClass().getName(), exception2.getMessage());
            exception2.printStackTrace();
        }
        catch(ThreadDeath threaddeath)
        {
            errorContext();
            error("terminated");
            throw threaddeath;
        }
        finally
        {
            for(; stack != null; stack = stack.next)
                handleEndTag(stack.tag);

            text = null;
            str = null;
        }
    }

    protected void interruptsIgnored(boolean flag)
    {
        ignoringInterrupts = flag;
    }

    private final int readCh()
        throws IOException
    {
        if(interruptBuffer != null)
            if(interruptPosition >= interruptBuffer.length())
            {
                interruptPosition = 0;
                interruptBuffer = null;
                return readCh();
            } else
            {
                char c = interruptBuffer.charAt(interruptPosition++);
                return c;
            }
        if(pos >= len)
        {
            do
                try
                {
                    len = in.read(buf);
                    break;
                }
                catch(InterruptedIOException interruptedioexception)
                {
                    if(!ignoringInterrupts)
                        throw interruptedioexception;
                }
            while(true);
            if(len <= 0)
                return -1;
            pos = 0;
        }
        return buf[pos++];
    }

    public void insertStringInDocument(String s)
    {
        if(interruptBuffer == null)
            interruptBuffer = new StringBuffer(s.length() * 2);
        interruptBuffer.append(s);
    }

    protected void resetParser()
    {
        pos = 0;
        len = 0;
        stack = null;
        strpos = 0;
        textpos = 0;
    }

    protected void initDocumentState()
    {
        seenHtml = false;
        seenHead = false;
        seenBody = false;
    }

    public void printImmediateDocContext()
    {
        printImmediateDocContext(5);
    }

    public void printImmediateDocContext(int i)
    {
    }

    public void printTagStack()
    {
        TagStack tagstack = stack;
        System.out.println("----------------------------");
        System.out.println("- Tag stack is");
        for(; tagstack != null; tagstack = tagstack.next)
            System.out.println("- <" + tagstack.elem.getName() + ">");

        System.out.println("----------------------------");
    }

    public boolean matchesInterestedType()
    {
        return false;
    }

    protected void timeStamp(String s)
    {
    }

    public Parser()
    {
        text = new char[1024];
        modeXMP = false;
        str = new char[128];
        skipTag = false;
        parsingScript = false;
        ignoringInterrupts = false;
        isFramed = false;
        isNonFramed = false;
        ignoreFormEnd = false;
        formNeedsPlacing = false;
        needAngleBracket = false;
        overlapTags = new Stack();
        haveOverlaps = false;
        buf = new char[1024];
    }

    private static final int BUFSIZE = 1024;
    private static final byte notext[] = new byte[0];
    private char text[];
    private int textpos;
    private Tag last;
    private boolean space;
    private boolean verbose;
    private boolean modeXMP;
    private static final boolean debugTime = false;
    private static final boolean debugTimeTags = false;
    private char str[];
    private int strpos;
    protected Properties props;
    protected DTD dtd;
    private int ch;
    private int ln;
    private Reader in;
    private Element recent;
    protected TagStack stack;
    private boolean skipTag;
    private boolean parsingScript;
    private boolean ignoringInterrupts;
    private boolean seenHtml;
    private boolean seenHead;
    private boolean seenBody;
    protected boolean isFramed;
    protected boolean isNonFramed;
    private boolean ignoreFormEnd;
    private boolean formNeedsPlacing;
    private boolean needAngleBracket;
    private Stack overlapTags;
    private boolean haveOverlaps;
    private final char map[] = {
        ',', '\u0192', '\u201D', '\u2026', '\u2020', '\u2021', '^', '\u2030', '_', '<', 
        '\u0152', '\215', '\216', '\217', '\220', '`', '\'', '\u201C', '\u201D', '\u2022', 
        '\u2014', '\u2013', '~', '\u2122', '_', '>', '\u0153', '\235', '\236', '\u0178'
    };
    private char buf[];
    private int pos;
    private int len;
    private StringBuffer interruptBuffer;
    private int interruptPosition;

}

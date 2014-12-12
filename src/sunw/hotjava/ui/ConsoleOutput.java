// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Console.java

package sunw.hotjava.ui;

import java.awt.TextArea;
import java.awt.TextComponent;
import java.io.*;

// Referenced classes of package sunw.hotjava.ui:
//            Console, ConsoleFrame

class ConsoleOutput
{
    class ConsoleOutputStream extends ByteArrayOutputStream
    {

        public synchronized void flush()
        {
            consoleTextArea.append(toString());
            try
            {
                writeTo(oldPrintStream);
            }
            catch(IOException ioexception)
            {
                ioexception.printStackTrace();
            }
            reset();
        }

        PrintStream oldPrintStream;

        ConsoleOutputStream(PrintStream printstream)
        {
            super(128);
            oldPrintStream = printstream;
        }
    }


    ConsoleOutput(TextArea textarea)
        throws Throwable
    {
        consoleTextArea = textarea;
        out = new ConsoleOutputStream(System.out);
        err = new ConsoleOutputStream(System.err);
        try
        {
            System.setOut(new PrintStream(out, true));
            System.setErr(new PrintStream(err, true));
            return;
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
            out = err = null;
            throw throwable;
        }
    }

    void dispose()
    {
        if(out == null || err == null)
            return;
        try
        {
            System.setOut(out.oldPrintStream);
            System.setErr(err.oldPrintStream);
        }
        catch(Throwable throwable)
        {
            throwable.printStackTrace();
            Console.showStatus("restoreFail.msg");
        }
        Console.showStatus("disabled.msg");
    }

    void clear()
    {
        consoleTextArea.setText("");
    }

    TextArea consoleTextArea;
    ConsoleOutputStream out;
    ConsoleOutputStream err;
}

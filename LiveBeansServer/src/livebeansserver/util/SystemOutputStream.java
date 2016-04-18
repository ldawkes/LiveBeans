/*
 * The MIT License
 *
 * Copyright 2016 Luke Dawkes.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package livebeansserver.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import livebeanscommon.ISysOutWatcher;

/**
 *
 * @author Luke Dawkes
 *
 */
public class SystemOutputStream extends FilterOutputStream
{

    private final ArrayList<ISysOutWatcher> _watchers;

    public SystemOutputStream(OutputStream out)
    {
        super(out);

        _watchers = new ArrayList<>();
    }

    public void addWatcher(ISysOutWatcher newWatcher)
    {
        _watchers.add(newWatcher);
    }

    @Override
    public void write(byte b[]) throws IOException
    {
        updateWatchers(new String(b));
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException
    {
        updateWatchers(new String(b, off, len));
    }

    private void updateWatchers(String updatedString)
    {
        if (_watchers.isEmpty())
        {
            return;
        }

        _watchers.stream().forEach((watcher)
                ->
                {
                    watcher.onPrintLine(updatedString);
        });
    }

}

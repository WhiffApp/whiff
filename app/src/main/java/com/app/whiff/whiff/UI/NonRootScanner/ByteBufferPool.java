package com.app.whiff.whiff.UI.NonRootScanner;

/**
 * Created by danie on 30/1/2018.
 */

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ByteBufferPool
{
    private static final int BUFFER_SIZE = 1024 * 64;
    private static ConcurrentLinkedQueue<ByteBuffer> pool = new ConcurrentLinkedQueue<>();

    public static ByteBuffer acquire()
    {
        ByteBuffer buffer = pool.poll();
        if (buffer == null)
            buffer = ByteBuffer.allocateDirect(BUFFER_SIZE); // Using DirectBuffer for zero-copy
        return buffer;
    }

    public static void release(ByteBuffer buffer)
    {
        buffer.clear();
        pool.offer(buffer);
    }

    public static void clear()
    {
        pool.clear();
    }
}

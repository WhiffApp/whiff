package com.app.whiff.whiff.NonRootScanner;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Class for recycling and reuse the allocated byte buffer for performance
 *
 * @author Yeo Pei Xuan
 */

public class ByteBufferPool
{
    //private static final int BUFFER_SIZE = 1024 * 64;
    private static final int BUFFER_SIZE = 64000;
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

package com.app.whiff.whiff.NonRootScanner;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * Generic class for caching the most recently used data structure
 * and callback to application when it reaches the limit specified
 * by the application
 *
 * @author Yeo Pei Xuan
 */

public class LRUCache<K, V> extends LinkedHashMap<K, V>
{
    private int maxSize;
    private CleanupCallback callback;

    public LRUCache(int maxSize, CleanupCallback callback)
    {
        super(maxSize + 1, 1, true);

        this.maxSize = maxSize;
        this.callback = callback;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
    {
        if (size() > maxSize)
        {
            callback.cleanup(eldest);
            return true;
        }
        return false;
    }

    public static interface CleanupCallback<K, V>
    {
        public void cleanup(Map.Entry<K, V> eldest);
    }
}

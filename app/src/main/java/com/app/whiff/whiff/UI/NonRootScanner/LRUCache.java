package com.app.whiff.whiff.UI.NonRootScanner;

/**
 * Created by danie on 30/1/2018.
 */

import java.util.LinkedHashMap;

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
    protected boolean removeEldestEntry(Entry<K, V> eldest)
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
        public void cleanup(Entry<K, V> eldest);
    }
}

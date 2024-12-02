package com.fanyamin.bjava.demo;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class SoftCache<K, V> {
    private final Map<K, SoftReference<V>> cache = new HashMap<>();

    public V get(K key) {
        SoftReference<V> ref = cache.get(key);
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    public void put(K key, V value) {
        cache.put(key, new SoftReference<>(value));
    }

    public void clear() {
        for (Map.Entry<K, SoftReference<V>> entry : cache.entrySet()) {
            if (entry.getValue().get() == null) {
                cache.remove(entry.getKey());
            }
        }
    }
}
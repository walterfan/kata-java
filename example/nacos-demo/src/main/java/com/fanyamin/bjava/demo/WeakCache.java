package com.fanyamin.bjava.demo;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class WeakCache<K, V> {
    private final Map<K, WeakReference<V>> cache = new HashMap<>();

    public V get(K key) {
        WeakReference<V> ref = cache.get(key);
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    public void put(K key, V value) {
        cache.put(key, new WeakReference<>(value));
    }

    public void clear() {
        for (Map.Entry<K, WeakReference<V>> entry : cache.entrySet()) {
            if (entry.getValue().get() == null) {
                cache.remove(entry.getKey());
            }
        }
    }
}
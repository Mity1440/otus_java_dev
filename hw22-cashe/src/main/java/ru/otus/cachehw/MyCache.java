package ru.otus.cachehw;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {
//Надо реализовать эти методы

    Map<K, V> datastore = new WeakHashMap<>();
    List<HwListener> listeners = new ArrayList<>();

    @Override
    public void put(K key, V value) {
        datastore.put(key, value);
        performListeners(key, value, "put");
    }

    @Override
    public void remove(K key) {
        V value = datastore.remove(key);
        performListeners(key, value, "remove");
    }

    @Override
    public V get(K key) {
        return datastore.get(key);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    @Override
    public int count() {
        return datastore.keySet().size();
    }

    private void performListeners(K key, V value, String action) {
        listeners.forEach(o->{
            o.notify(key, value, action);
        });
    }

}

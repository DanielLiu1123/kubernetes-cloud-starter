package com.freemanan.kubernetes.grey.common;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author Freeman
 */
public class Cache<T> {

    private final ConcurrentMap<String, Item<T>> items;
    private final ScheduledExecutorService executor;
    private final long ttl;

    public Cache(long ttl) {
        this.ttl = ttl;
        this.items = new ConcurrentHashMap<>();
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.executor.scheduleWithFixedDelay(
                () -> {
                    for (Map.Entry<String, Item<T>> kv : items.entrySet()) {
                        if (isExpire(kv.getValue())) {
                            remove(kv.getKey());
                        }
                    }
                },
                0,
                10,
                TimeUnit.SECONDS);
    }

    public T get(String key) {
        Item<T> item = items.get(key);
        if (item == null) {
            return null;
        }
        if (isExpire(item)) {
            remove(key);
            return null;
        }
        item.setLastAccessTime(System.currentTimeMillis());
        return item.getItem();
    }

    public T getOrSupply(String key, Supplier<T> supplier) {
        return items.compute(key, (k, v) -> {
                    if (v == null) {
                        return new Item<>(supplier.get(), System.currentTimeMillis());
                    }
                    if (isExpire(v)) {
                        try {
                            return new Item<>(supplier.get(), System.currentTimeMillis());
                        } finally {
                            T removed = v.getItem();
                            afterRemove(removed);
                        }
                    }
                    v.setLastAccessTime(System.currentTimeMillis());
                    return v;
                })
                .getItem();
    }

    public void remove(String key) {
        Item<T> item = items.remove(key);
        if (item != null) {
            afterRemove(item.getItem());
        }
    }

    public void clear() {
        items.keySet().iterator().forEachRemaining(this::remove);
    }

    protected void afterRemove(T item) {}

    private boolean isExpire(Item<T> item) {
        return System.currentTimeMillis() - item.getLastAccessTime() > this.ttl;
    }

    private static final class Item<T> {
        private T item;
        private long lastAccessTime;

        private Item(T item, long lastAccessTime) {
            this.item = item;
            this.lastAccessTime = lastAccessTime;
        }

        public T getItem() {
            return item;
        }

        public void setItem(T item) {
            this.item = item;
        }

        public long getLastAccessTime() {
            return lastAccessTime;
        }

        public void setLastAccessTime(long lastAccessTime) {
            this.lastAccessTime = lastAccessTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item<?> item1 = (Item<?>) o;
            return lastAccessTime == item1.lastAccessTime && Objects.equals(item, item1.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, lastAccessTime);
        }

        @Override
        public String toString() {
            return "Item{" + "item=" + item + ", lastAccessTime=" + lastAccessTime + '}';
        }
    }
}

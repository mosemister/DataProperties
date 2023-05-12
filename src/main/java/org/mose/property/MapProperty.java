package org.mose.property;

import org.mose.property.event.CollectionUpdateEvent;

import java.util.HashMap;
import java.util.Map;

public interface MapProperty<K, V, D extends Map<?, ?>> extends Property<Map<K, V>, D> {

    void registerCollectionAddEvent(CollectionUpdateEvent.CollectionAddIndexEvent<Map.Entry<K, V>> addEvent);

    void registerCollectionRemoveEvent(CollectionUpdateEvent.CollectionRemoveIndexEvent<Map.Entry<K, V>> removeEvent);

    interface ReadOnly<K, V, D extends Map<?, ?>> extends Property.ReadOnly<Map<K, V>, D>, MapProperty<K, V, D> {

    }

    interface Write<K, V, D extends Map<?, ?>> extends Property.Write<Map<K, V>, D>, MapProperty<K, V, D> {

        default boolean add(Map.Entry<K, V> value) {
            return this.addAll(value);
        }

        default boolean addAll(Map.Entry<K, V>... add) {
            Map<K, V> map = new HashMap<>();
            for (Map.Entry<K, V> entry : add) {
                map.put(entry.getKey(), entry.getValue());
            }
            return this.addAll(map);
        }

        boolean addAll(Map<K, V> collection);

        default boolean remove(Map.Entry<K, V> remove) {
            return this.removeAll(remove);
        }

        default boolean removeAll(Map.Entry<K, V>... remove) {
            Map<K, V> map = new HashMap<>();
            for (Map.Entry<K, V> entry : remove) {
                map.put(entry.getKey(), entry.getValue());
            }
            return this.removeAll(map);
        }

        boolean removeAll(Map<K, V> collection);

    }

}

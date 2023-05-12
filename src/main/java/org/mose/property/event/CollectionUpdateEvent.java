package org.mose.property.event;

import org.mose.property.CollectionProperty;

import java.util.Collection;

public interface CollectionUpdateEvent<T> {

    void handle(CollectionProperty<T, ?> property, Collection<T> current, Collection<T> changing);

    interface CollectionRemoveIndexEvent<T> extends CollectionUpdateEvent<T> {
    }

    interface CollectionAddIndexEvent<T> extends CollectionUpdateEvent<T> {

    }

}

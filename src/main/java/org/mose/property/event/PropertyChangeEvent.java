package org.mose.property.event;

import org.mose.property.Property;
import org.mose.property.impl.ValueSetType;

public interface PropertyChangeEvent<T> {

    void handle(Property<T, ?> targetProperty, T currentValue, ValueSetType type, T newValue);

}

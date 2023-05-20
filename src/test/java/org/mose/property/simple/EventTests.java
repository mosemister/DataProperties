package org.mose.property.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mose.property.Property;
import org.mose.property.impl.ReadOnlyPropertyImpl;
import org.mose.property.impl.WritePropertyImpl;
import org.mose.property.impl.number.ReadOnlyNumberPropertyImpl;
import org.mose.property.impl.number.WriteNumberPropertyImpl;

public class EventTests {

    @Test
    public void doesEventFireForValueChange() {
        WritePropertyImpl<Boolean, Boolean> property = new WritePropertyImpl<>(t -> t, true);
        boolean[] array = new boolean[1];

        //act
        property.registerValueChangeEvent((targetProperty, currentValue, type, newValue) -> array[0] = true);
        property.setValue(false);

        //assert
        Assertions.assertTrue(array[0]);
    }

    @Test
    public void doesEventFireForBoundChange() {
        WritePropertyImpl<Boolean, Boolean> property = new WritePropertyImpl<>(t -> t, true);
        Property<Boolean, Boolean> bound = new ReadOnlyPropertyImpl<>(t -> t, null);
        bound.bindTo(property);
        boolean[] array = new boolean[1];

        //act
        bound.registerValueChangeEvent((targetProperty, currentValue, type, newValue) -> array[0] = true);
        property.setValue(false);

        //assert
        Assertions.assertTrue(array[0]);
    }

    @Test
    public void doesEventFireForBoundChangeWhenNoChangeOccurs() {
        WritePropertyImpl<Integer, Integer> property = new WritePropertyImpl<>(t -> t, 1);
        Property<Boolean, Boolean> bound = new ReadOnlyPropertyImpl<>(t -> t, null);
        bound.bindTo(property, value -> value > 10);
        boolean[] array = new boolean[1];

        //act
        bound.registerValueChangeEvent((targetProperty, currentValue, type, newValue) -> array[0] = true);
        property.setValue(2);

        //assert
        Assertions.assertFalse(array[0]);
    }

    @Test
    public void doesEventFireForBoundChangeWhenNoChangeOccursOnNumberProperty() {
        WriteNumberPropertyImpl<Integer, Integer> property = WriteNumberPropertyImpl.create(1);
        boolean[] array = new boolean[1];

        //act
        property.registerValueChangeEvent((targetProperty, currentValue, type, newValue) -> array[0] = true);
        property.setValue(1);

        //assert
        Assertions.assertFalse(array[0]);
    }

    @Test
    public void doesEventFireForMappedBoundChange() {
        WritePropertyImpl<Integer, Integer> property = new WritePropertyImpl<>(t -> t, 1);
        Property<Boolean, Boolean> bound = new ReadOnlyPropertyImpl<>(t -> t, null);
        bound.bindTo(property, value -> value > 10);
        boolean[] array = new boolean[1];

        //act
        bound.registerValueChangeEvent((targetProperty, currentValue, type, newValue) -> array[0] = true);
        property.setValue(21);

        //assert
        Assertions.assertTrue(array[0]);
    }
}

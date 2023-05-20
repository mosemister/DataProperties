package org.mose.property.unknown;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mose.property.Property;
import org.mose.property.impl.ReadOnlyPropertyImpl;
import org.mose.property.impl.WritePropertyImpl;
import org.mose.property.impl.nevernull.WriteNeverNullPropertyImpl;
import org.mose.property.impl.unknown.UnknownProperty;

import java.util.ArrayList;
import java.util.List;

public class UnknownPropertyTests {

    @Test
    public void canAssignValueToProperty() {
        UnknownProperty<Boolean, Boolean> property = new UnknownProperty<>(t -> t, () -> true);

        //assert
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertTrue(property.value().get());
    }

    @Test
    public void throwsErrorWhenAttemptingToBind() {
        UnknownProperty<Boolean, Boolean> property = new UnknownProperty<>(t -> t, () -> true);
        WriteNeverNullPropertyImpl<Boolean, Boolean> booleanProperty = WriteNeverNullPropertyImpl.bool();

        //assert
        Assertions.assertThrows(RuntimeException.class, () -> property.bindTo(booleanProperty));
    }

    @Test
    public void createsBoundReadOnly() {
        UnknownProperty<Boolean, Boolean> property = new UnknownProperty<>(t -> t, () -> true);

        //act
        Property.ReadOnly<Boolean, Boolean> readOnly = property.createBoundReadOnly();

        //assert
        Assertions.assertTrue(readOnly.bound().isPresent());
        Assertions.assertEquals(property, readOnly.bound().get());
        Assertions.assertInstanceOf(ReadOnlyPropertyImpl.class, readOnly);
    }

    @Test
    public void doesEventSendWhenValueChanges() {
        UnknownProperty<Boolean, Boolean> property = new UnknownProperty<>(t -> t, () -> true);
        List<Boolean> list = new ArrayList<>();

        //act
        property.registerValueChangeEvent((targetProperty, currentValue, type, newValue) -> list.add(newValue));
        property.onValueUpdate(true, false);

        //assert
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertFalse(list.get(0));
    }

    @Test
    public void throwsErrorWhenLocked() {
        UnknownProperty<Boolean, Boolean> bindingTo = new UnknownProperty<>(t -> t, () -> true);
        Property.Write<Boolean, Boolean> property = new WritePropertyImpl<>(t -> t, false);

        //assert
        Assertions.assertFalse(bindingTo.canBind(), "Property cannot be bound");
        Assertions.assertTrue(bindingTo.isBindLocked(), "Property is locked");
        Assertions.assertThrows(IllegalStateException.class, () -> bindingTo.bindTo(property));
    }

    @Test
    public void doesEventSendOnBoundWhenValueChanges() {
        UnknownProperty<Boolean, Boolean> property = new UnknownProperty<>(t -> t, () -> true);
        Property.ReadOnly<Boolean, Boolean> readOnly = property.createBoundReadOnly();
        List<Boolean> list = new ArrayList<>();

        //act
        readOnly.registerValueChangeEvent((targetProperty, currentValue, type, newValue) -> list.add(newValue));
        property.onValueUpdate(true, false);

        //assert
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertFalse(list.get(0));
    }


}

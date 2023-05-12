package org.mose.property;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mose.property.impl.WritePropertyImpl;

public class GenericSimpleTests {

    @Test
    public void isDefaultValueInserted() {
        //act
        WritePropertyImpl<Boolean, Boolean> property = new WritePropertyImpl<>(t -> t, true);

        //assert
        Assertions.assertTrue(property.value());
    }

    @Test
    public void canChangeValue() {
        WritePropertyImpl<Boolean, Boolean> property = new WritePropertyImpl<>(t -> t, true);

        //act
        property.setValue(false);

        //assert
        Assertions.assertFalse(property.value());
    }

    @Test
    public void canCreateBoundReadOnly() {
        WritePropertyImpl<Boolean, Boolean> property = new WritePropertyImpl<>(t -> t, true);

        //act
        Property.ReadOnly<Boolean, Boolean> bound = property.createBoundReadOnly();

        //assert
        Assertions.assertTrue(bound.value());
    }

    @Test
    public void canChangeBoundValue() {
        WritePropertyImpl<Boolean, Boolean> property = new WritePropertyImpl<>(t -> t, true);

        //act
        Property.ReadOnly<Boolean, Boolean> bound = property.createBoundReadOnly();
        property.setValue(false);

        //assert
        Assertions.assertFalse(bound.value());
    }

    @Test
    public void canChangeValueWithNewestRule() {
        WritePropertyImpl<Boolean, Boolean> property = new WritePropertyImpl<>(ValueOverrideRule.PREFER_NEWEST, t -> t, true);

        //act
        property.setValue(false);

        //assert
        Assertions.assertFalse(property.value());
    }

    @Test
    public void canChangeValueWithSetRule() {
        WritePropertyImpl<Boolean, Boolean> property = new WritePropertyImpl<>(ValueOverrideRule.PREFER_SET, t -> t, true);

        //act
        property.setValue(false);

        //assert
        Assertions.assertFalse(property.value());
    }

    @Test
    public void canChangeValueWithSetRuleWithoutBoundsOverriding() {
        WritePropertyImpl<Boolean, Boolean> property = new WritePropertyImpl<>(ValueOverrideRule.PREFER_SET, t -> t, true);
        WritePropertyImpl<Boolean, Boolean> bound = new WritePropertyImpl<>(ValueOverrideRule.PREFER_SET, t -> t, true);
        bound.bindTo(property);

        //act
        property.setValue(false);

        //assert
        Assertions.assertTrue(bound.value());
    }

    @Test
    public void canChangeValueWithBoundRule() {
        WritePropertyImpl<Boolean, Boolean> property = new WritePropertyImpl<>(ValueOverrideRule.PREFER_BOUND, t -> t, true);

        //act
        property.setValue(false);

        //assert
        Assertions.assertFalse(property.value());
    }

    @Test
    public void canChangeValueWithBoundRuleWithoutSetOverriding() {
        WritePropertyImpl<Boolean, Boolean> property = new WritePropertyImpl<>(ValueOverrideRule.PREFER_SET, t -> t, true);
        WritePropertyImpl<Boolean, Boolean> bound = new WritePropertyImpl<>(ValueOverrideRule.PREFER_BOUND, t -> t, true);
        bound.bindTo(property);

        //act
        property.setValue(false);
        bound.setValue(null);

        //assert
        Assertions.assertFalse(bound.value());
    }

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
}

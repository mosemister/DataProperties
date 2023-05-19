package org.mose.property.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mose.property.AbstractPropertyTests;
import org.mose.property.Property;
import org.mose.property.ValueOverrideRule;
import org.mose.property.impl.nevernull.WriteNeverNullPropertyImpl;

public class NeverNullPropertyTests extends AbstractPropertyTests<Boolean, Boolean> {

    @Override
    protected Boolean getFirstValue() {
        return true;
    }

    @Override
    protected Boolean getSecondValue() {
        return false;
    }

    @Override
    protected Class<?>[] getReadOnlyClasses() {
        return new Class<?>[]{Property.NeverNull.class, Property.ReadOnly.class};
    }

    @Override
    protected WriteNeverNullPropertyImpl<Boolean, Boolean> createProperty(Boolean value, ValueOverrideRule overrideRule) {
        return new WriteNeverNullPropertyImpl<>(overrideRule, t -> t, () -> false, value);
    }

    @Test
    public void attemptToReadNullValue() {
        Property.NeverNull<Boolean, Boolean> property = this.createProperty(null, ValueOverrideRule.PREFER_NEWEST);

        //act
        boolean value = property.safeValue();

        //assert
        Assertions.assertFalse(value);
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertFalse(property.value().get());
    }

    @Test
    public void attemptToSetNullValue() {
        Property.Write<Boolean, Boolean> property = this.createProperty(true, ValueOverrideRule.PREFER_NEWEST);

        //act
        property.setValue(null);

        //assert
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertFalse(property.value().get());
    }
}

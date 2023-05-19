package org.mose.property.simple;

import org.mose.property.AbstractPropertyTests;
import org.mose.property.Property;
import org.mose.property.ValueOverrideRule;
import org.mose.property.impl.number.WriteNumberPropertyImpl;

public class NumberPropertyTests extends AbstractPropertyTests<Integer, Integer> {

    @Override
    protected Integer getFirstValue() {
        return 24;
    }

    @Override
    protected Integer getSecondValue() {
        return 32;
    }

    @Override
    protected Class<?>[] getReadOnlyClasses() {
        return new Class<?>[]{Property.Number.class, Property.ReadOnly.class};
    }

    @Override
    protected Property.Write<Integer, Integer> createProperty(Integer value, ValueOverrideRule overrideRule) {
        return new WriteNumberPropertyImpl<>(overrideRule, t -> t, value);
    }
}

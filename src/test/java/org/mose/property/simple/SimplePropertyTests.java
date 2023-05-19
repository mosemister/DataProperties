package org.mose.property.simple;

import org.mose.property.AbstractPropertyTests;
import org.mose.property.Property;
import org.mose.property.ValueOverrideRule;
import org.mose.property.impl.WritePropertyImpl;

public class SimplePropertyTests extends AbstractPropertyTests<Boolean, Boolean> {
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
        return new Class<?>[]{Property.ReadOnly.class};
    }

    @Override
    protected Property.Write<Boolean, Boolean> createProperty(Boolean value, ValueOverrideRule overrideRule) {
        return new WritePropertyImpl<>(overrideRule, t -> t, value);
    }
}

package org.mose.property.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mose.property.AbstractPropertyTests;
import org.mose.property.Property;
import org.mose.property.ValueOverrideRule;
import org.mose.property.impl.number.WriteNumberPropertyImpl;

import java.util.OptionalDouble;
import java.util.OptionalInt;

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

    @Test
    public void canGetIntegerFromDoubleProperty() {
        WriteNumberPropertyImpl<Double, Double> property = WriteNumberPropertyImpl.create(1.5);

        //act
        OptionalInt opInt = property.asInt();

        Assertions.assertTrue(opInt.isPresent());
        Assertions.assertEquals(1, opInt.getAsInt());
    }

    @Test
    public void canGetDoubleFromIntegerProperty() {
        WriteNumberPropertyImpl<Integer, Integer> property = WriteNumberPropertyImpl.create(1);

        //act
        OptionalDouble opDouble = property.asDouble();

        Assertions.assertTrue(opDouble.isPresent());
        Assertions.assertEquals(1, opDouble.getAsDouble());
    }
}

package org.mose.property.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mose.property.AbstractPropertyTests;
import org.mose.property.Property;
import org.mose.property.ValueOverrideRule;
import org.mose.property.impl.ReadOnlyPropertyImpl;
import org.mose.property.impl.WritePropertyImpl;
import org.mose.property.impl.collection.ReadOnlyCollectionPropertyImpl;
import org.mose.property.impl.collection.WriteCollectionPropertyImpl;
import org.mose.property.impl.nevernull.ReadOnlyNeverNullPropertyImpl;
import org.mose.property.impl.nevernull.WriteNeverNullPropertyImpl;
import org.mose.property.impl.number.ReadOnlyNumberPropertyImpl;
import org.mose.property.impl.number.WriteNumberPropertyImpl;

import java.util.ArrayList;

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

    @Test
    public void isStandardPropertyThePreferredWriteClassForString() {
        //act
        Class<? extends Property.Write> clazz = Property.getWritePreferedClass(String.class);

        //assert
        Assertions.assertEquals(WritePropertyImpl.class, clazz);
    }

    @Test
    public void isStandardPropertyThePreferredReadClassForString() {
        //act
        Class<? extends Property.ReadOnly> clazz = Property.getReadOnlyPreferedClass(String.class);

        //assert
        Assertions.assertEquals(ReadOnlyPropertyImpl.class, clazz);
    }

    @Test
    public void isNumberPropertyThePreferredWriteClassForInteger() {
        //act
        Class<? extends Property.Write> clazz = Property.getWritePreferedClass(Integer.class);

        //assert
        Assertions.assertEquals(WriteNumberPropertyImpl.class, clazz);
    }

    @Test
    public void isNumberPropertyThePreferredReadClassForInteger() {
        //act
        Class<? extends Property.ReadOnly> clazz = Property.getReadOnlyPreferedClass(Integer.class);

        //assert
        Assertions.assertEquals(ReadOnlyNumberPropertyImpl.class, clazz);
    }

    @Test
    public void isNumberPropertyThePreferredWriteClassForPrimitiveInteger() {
        //act
        Class<? extends Property.Write> clazz = Property.getWritePreferedClass(int.class);

        //assert
        Assertions.assertEquals(WriteNumberPropertyImpl.class, clazz);
    }

    @Test
    public void isNumberPropertyThePreferredReadClassForPrimitiveInteger() {
        //act
        Class<? extends Property.ReadOnly> clazz = Property.getReadOnlyPreferedClass(int.class);

        //assert
        Assertions.assertEquals(ReadOnlyNumberPropertyImpl.class, clazz);
    }

    @Test
    public void isNumberPropertyThePreferredWriteClassForPrimitiveDouble() {
        //act
        Class<? extends Property.Write> clazz = Property.getWritePreferedClass(double.class);

        //assert
        Assertions.assertEquals(WriteNumberPropertyImpl.class, clazz);
    }

    @Test
    public void isNumberPropertyThePreferredReadClassForPrimitiveDouble() {
        //act
        Class<? extends Property.ReadOnly> clazz = Property.getReadOnlyPreferedClass(double.class);

        //assert
        Assertions.assertEquals(ReadOnlyNumberPropertyImpl.class, clazz);
    }

    @Test
    public void isNeverNullPropertyThePreferredWriteClassForBoolean() {
        //act
        Class<? extends Property.Write> clazz = Property.getWritePreferedClass(boolean.class);

        //assert
        Assertions.assertEquals(WriteNeverNullPropertyImpl.class, clazz);
    }

    @Test
    public void isNeverNullPropertyThePreferredReadClassForBoolean() {
        //act
        Class<? extends Property.ReadOnly> clazz = Property.getReadOnlyPreferedClass(boolean.class);

        //assert
        Assertions.assertEquals(ReadOnlyNeverNullPropertyImpl.class, clazz);
    }

    @Test
    public void isCollectionPropertyThePreferredWriteClassForArrayList() {
        //act
        Class<? extends Property.Write> clazz = Property.getWritePreferedClass(ArrayList.class);

        //assert
        Assertions.assertEquals(WriteCollectionPropertyImpl.class, clazz);
    }

    @Test
    public void isCollectionPropertyThePreferredReadClassForArrayList() {
        //act
        Class<? extends Property.ReadOnly> clazz = Property.getReadOnlyPreferedClass(ArrayList.class);

        //assert
        Assertions.assertEquals(ReadOnlyCollectionPropertyImpl.class, clazz);
    }
}

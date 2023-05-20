package org.mose.property;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

public abstract class AbstractPropertyTests<T, D> {

    protected abstract T getFirstValue();

    protected abstract T getSecondValue();

    protected abstract Class<?>[] getReadOnlyClasses();

    protected abstract Property.Write<T, D> createProperty(T value, ValueOverrideRule overrideRule);

    protected Property.Write<T, D> createPropertyNewest(T value) {
        return createProperty(value, ValueOverrideRule.PREFER_NEWEST);
    }

    protected Property.Write<T, D> createPropertySet(T value) {
        return createProperty(value, ValueOverrideRule.PREFER_SET);
    }

    protected Property.Write<T, D> createPropertyBound(T value) {
        return createProperty(value, ValueOverrideRule.PREFER_BOUND);
    }

    protected Property.Write<T, D> createDefaultProperty() {
        return createPropertyNewest(this.getFirstValue());
    }

    @Test
    public void isReadOnlyClass() {
        Property.ReadOnly<?, ?> readOnlyProperty = createDefaultProperty().createBoundReadOnly();

        for (Class<?> clazz : this.getReadOnlyClasses()) {
            if (!clazz.isInstance(readOnlyProperty)) {
                Assertions.fail("ReadOnly class does not implement " + clazz.getName());
            }
        }
    }

    @Test
    public void doesReadOnlyCreateAValidReadOnlyClass() {
        Property.ReadOnly<?, ?> readOnlyProperty = createDefaultProperty().createBoundReadOnly().createBoundReadOnly();

        for (Class<?> clazz : this.getReadOnlyClasses()) {
            if (!clazz.isInstance(readOnlyProperty)) {
                Assertions.fail("ReadOnly class does not implement " + clazz.getName());
            }
        }
    }

    @Test
    public void throwsErrorWhenLocked() {
        Property.Write<T, D> property = createDefaultProperty();
        Property.Write<T, D> bindingTo = createDefaultProperty();
        Assumptions.assumeTrue(bindingTo.canBind(), "Property cannot be bound");

        //act
        bindingTo.lockBind();

        //assert
        Assertions.assertTrue(bindingTo.isBindLocked());
        Assertions.assertThrows(IllegalStateException.class, () -> bindingTo.bindTo(property));
    }

    @Test
    public void isReadOnlyCreateBound() {
        Property.Write<?, ?> property = createDefaultProperty();
        Property.ReadOnly<?, ?> readOnly = property.createBoundReadOnly();

        //assert
        Assertions.assertTrue(readOnly.bound().isPresent());
        Assertions.assertFalse(property.bound().isPresent());
        Assertions.assertEquals(property, readOnly.bound().get());
    }

    @Test
    public void canRemoveBound() {
        Property.Write<?, ?> property = createDefaultProperty();
        Property.ReadOnly<?, ?> readOnly = property.createBoundReadOnly();

        //act
        readOnly.removeBind();

        //assert
        Assertions.assertFalse(readOnly.bound().isPresent());
    }

    @Test
    public void isDefaultValueInserted() {

        //act
        Property<T, D> property = this.createDefaultProperty();

        //assert
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertEquals(this.getFirstValue(), property.value().get());
    }

    @Test
    public void canChangeValue() {
        Property.Write<T, D> property = this.createDefaultProperty();

        //act
        property.setValue(this.getSecondValue());

        //assert
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertEquals(this.getSecondValue(), property.value().get());
    }

    @Test
    public void canCreateBoundReadOnly() {
        Property<T, D> property = this.createDefaultProperty();

        //act
        Property.ReadOnly<T, D> bound = property.createBoundReadOnly();

        //assert
        Assertions.assertTrue(bound.value().isPresent());
        Assertions.assertEquals(this.getFirstValue(), bound.value().get());
    }

    @Test
    public void canChangeBoundValue() {
        Property.Write<T, D> property = this.createDefaultProperty();

        //act
        Property.ReadOnly<T, D> bound = property.createBoundReadOnly();
        property.setValue(this.getSecondValue());

        //assert
        Assertions.assertTrue(bound.value().isPresent());
        Assertions.assertEquals(this.getSecondValue(), bound.value().get());
    }

    @Test
    public void canChangeValueWithNewestRule() {
        Property.Write<T, D> property = this.createPropertyNewest(this.getFirstValue());

        //act
        property.setValue(this.getSecondValue());

        //assert
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertEquals(this.getSecondValue(), property.value().get());
    }

    @Test
    public void canChangeValueWithSetRule() {
        Property.Write<T, D> property = this.createPropertySet(this.getFirstValue());

        //act
        property.setValue(this.getSecondValue());

        //assert
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertEquals(this.getSecondValue(), property.value().get());
    }

    @Test
    public void canChangeValueWithSetRuleWithoutBoundsOverriding() {
        Property.Write<T, D> property = this.createPropertySet(this.getFirstValue());
        Property.Write<T, D> bound = this.createPropertySet(this.getFirstValue());
        bound.bindTo(property);

        //act
        property.setValue(this.getSecondValue());

        //assert
        Assertions.assertTrue(bound.value().isPresent());
        Assertions.assertEquals(this.getFirstValue(), bound.value().get());
    }

    @Test
    public void canChangeValueWithBoundRule() {
        Property.Write<T, D> property = this.createPropertyBound(this.getFirstValue());

        //act
        property.setValue(this.getSecondValue());

        //assert
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertEquals(this.getSecondValue(), property.value().get());
    }

    @Test
    public void canChangeValueWithBoundRuleWithoutSetOverriding() {
        Property.Write<T, D> property = this.createPropertySet(this.getFirstValue());
        Property.Write<T, D> bound = this.createPropertyBound(this.getFirstValue());
        bound.bindTo(property);

        //act
        property.setValue(this.getSecondValue());
        bound.setValue(null);

        //assert
        Assertions.assertTrue(bound.value().isPresent());
        Assertions.assertEquals(this.getSecondValue(), bound.value().get());
    }
}

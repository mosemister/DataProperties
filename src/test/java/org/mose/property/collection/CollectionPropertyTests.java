package org.mose.property.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mose.property.AbstractPropertyTests;
import org.mose.property.CollectionProperty;
import org.mose.property.Property;
import org.mose.property.ValueOverrideRule;
import org.mose.property.impl.collection.WriteCollectionPropertyImpl;

import java.util.*;

public class CollectionPropertyTests extends AbstractPropertyTests<Collection<Boolean>, Collection<Boolean>> {

    @Override
    protected Collection<Boolean> getFirstValue() {
        return Arrays.asList(true, true, true);
    }

    @Override
    protected Collection<Boolean> getSecondValue() {
        return Arrays.asList(true, false, true);
    }

    @Override
    protected Class<?>[] getReadOnlyClasses() {
        return new Class[]{Property.ReadOnly.class, CollectionProperty.ReadOnly.class, Property.NeverNull.class};
    }

    @Override
    protected WriteCollectionPropertyImpl<Boolean, Collection<Boolean>> createProperty(Collection<Boolean> value, ValueOverrideRule overrideRule) {
        return new WriteCollectionPropertyImpl<>(overrideRule, t -> t, ArrayList::new, value);
    }

    @Test
    public void canAddElementToProperty() {
        List<Boolean> values = new ArrayList<>();
        CollectionProperty.Write<Boolean, Collection<Boolean>> property = this.createProperty(values, ValueOverrideRule.PREFER_NEWEST);

        //act
        property.add(true);

        //assert
        Assertions.assertFalse(values.isEmpty());
        Assertions.assertTrue(values.get(0));

        Collection<Boolean> internalValue = property.safeValue();
        Assertions.assertFalse(internalValue.isEmpty());
        Assertions.assertTrue(internalValue.iterator().next());
    }

    @Test
    public void sendsAddEventWhenElementIsAdded() {
        List<Boolean> values = new ArrayList<>();
        CollectionProperty.Write<Boolean, Collection<Boolean>> property = this.createProperty(values, ValueOverrideRule.PREFER_NEWEST);
        List<Boolean> onEvent = new LinkedList<>();
        property.registerCollectionAddEvent((property1, current, changing) -> onEvent.add(true));

        //act
        property.add(true);

        //assert
        Assertions.assertFalse(onEvent.isEmpty());
    }

    @Test
    public void sendsRemoveEventWhenElementIsAdded() {
        List<Boolean> values = new ArrayList<>();
        values.add(true);
        CollectionProperty.Write<Boolean, Collection<Boolean>> property = this.createProperty(values, ValueOverrideRule.PREFER_NEWEST);
        List<Boolean> onEvent = new LinkedList<>();
        property.registerCollectionRemoveEvent((property1, current, changing) -> onEvent.add(true));

        //act
        property.remove(true);

        //assert
        Assertions.assertFalse(onEvent.isEmpty());
    }

    @Test
    public void canRemoveElementToProperty() {
        List<Boolean> values = new ArrayList<>();
        values.add(true);
        CollectionProperty.Write<Boolean, Collection<Boolean>> property = this.createProperty(values, ValueOverrideRule.PREFER_NEWEST);

        //act
        property.remove(true);

        //assert
        Assertions.assertTrue(values.isEmpty());

        Collection<Boolean> internalValue = property.safeValue();
        Assertions.assertTrue(internalValue.isEmpty());
    }

    @Test
    public void attemptToReadNullValue() {
        Property.NeverNull<Collection<Boolean>, Collection<Boolean>> property = this.createProperty(null, ValueOverrideRule.PREFER_NEWEST);

        //act
        Collection<Boolean> value = property.safeValue();

        //assert
        Assertions.assertEquals(Collections.emptyList(), value);
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertEquals(Collections.emptyList(), property.value().get());
    }

    @Test
    public void attemptToSetNullValue() {
        Property.Write<Collection<Boolean>, Collection<Boolean>> property = this.createProperty(Arrays.asList(false, false), ValueOverrideRule.PREFER_NEWEST);

        //act
        property.setValue(null);

        //assert
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertEquals(Collections.emptyList(), property.value().get());
    }

}

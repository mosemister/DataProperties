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
    public void canSetElementToProperty() {
        Collection<Boolean> values = new ArrayList<>();
        CollectionProperty.Write<Boolean, Collection<Boolean>> property = this.createProperty(values, ValueOverrideRule.PREFER_NEWEST);

        //act
        property.setValue(true, false);

        //assert
        Collection<Boolean> internalValue = property.safeValue();
        Assertions.assertEquals(2, internalValue.size());
        Iterator<Boolean> i = internalValue.iterator();
        Assertions.assertTrue(i.next());
        Assertions.assertFalse(i.next());
    }

    @Test
    public void canAddElementToProperty() {
        Collection<Boolean> values = new ArrayList<>();
        CollectionProperty.Write<Boolean, Collection<Boolean>> property = this.createProperty(values, ValueOverrideRule.PREFER_NEWEST);

        //act
        property.add(true);

        //assert
        List<Boolean> result = (List<Boolean>) property.safeValue();

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertTrue(result.get(0));
    }

    @Test
    public void sendsAddEventWhenElementIsAdded() {
        Collection<Boolean> values = new ArrayList<>();
        CollectionProperty.Write<Boolean, Collection<Boolean>> property = this.createProperty(values, ValueOverrideRule.PREFER_NEWEST);
        List<Boolean> onEvent = new LinkedList<>();
        property.registerCollectionAddEvent((property1, current, changing) -> onEvent.add(true));

        //act
        property.add(true);

        //assert
        Assertions.assertFalse(onEvent.isEmpty());
    }

    @Test
    public void sendsAddEventOnBoundWhenElementIsAdded() {
        Collection<Boolean> values = new ArrayList<>();
        CollectionProperty.Write<Boolean, Collection<Boolean>> property = this.createProperty(values, ValueOverrideRule.PREFER_NEWEST);
        CollectionProperty.ReadOnly<Boolean, Collection<Boolean>> boundProperty = property.createBoundReadOnly();
        List<Boolean> onEvent = new LinkedList<>();
        boundProperty.registerCollectionAddEvent((property1, current, changing) -> onEvent.add(true));

        //act
        property.add(true);

        //assert
        Assertions.assertFalse(onEvent.isEmpty());
    }

    @Test
    public void sendsRemoveEventWhenElementIsRemoved() {
        Collection<Boolean> values = new ArrayList<>();
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
    public void sendsRemoveEventOnBoundPropertyWhenElementIsRemoved() {
        Collection<Boolean> values = new ArrayList<>();
        values.add(true);
        CollectionProperty.Write<Boolean, Collection<Boolean>> property = this.createProperty(values, ValueOverrideRule.PREFER_NEWEST);
        CollectionProperty.ReadOnly<Boolean, Collection<Boolean>> boundProperty = property.createBoundReadOnly();
        List<Boolean> onEvent = new LinkedList<>();
        boundProperty.registerCollectionRemoveEvent((property1, current, changing) -> onEvent.add(true));

        //act
        property.remove(true);

        //assert
        Assertions.assertFalse(onEvent.isEmpty());
    }

    @Test
    public void canRemoveElementToProperty() {
        Collection<Boolean> values = new ArrayList<>();
        values.add(true);
        CollectionProperty.Write<Boolean, Collection<Boolean>> property = this.createProperty(values, ValueOverrideRule.PREFER_NEWEST);

        //act
        property.remove(true);

        //assert
        Assertions.assertTrue(property.safeValue().isEmpty());

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

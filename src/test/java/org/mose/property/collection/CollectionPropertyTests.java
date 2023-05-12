package org.mose.property.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mose.property.Property;
import org.mose.property.ValueOverrideRule;
import org.mose.property.impl.collection.WriteCollectionPropertyImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

public class CollectionPropertyTests {

    private final Collection<Boolean> VALUE_A = Arrays.asList(true, false);
    private final Collection<Boolean> VALUE_B = Arrays.asList(true, true, true);

    private final Supplier<Property.Write<Collection<Boolean>, Collection<Boolean>>> WITH_VALUE_A = () -> new WriteCollectionPropertyImpl<>(t -> t, VALUE_A);
    private final Supplier<Property.Write<Collection<Boolean>, Collection<Boolean>>> WITH_VALUE_A_PREFER_NEWEST = () -> new WriteCollectionPropertyImpl<>(ValueOverrideRule.PREFER_NEWEST, t -> t, VALUE_A);
    private final Supplier<Property.Write<Collection<Boolean>, Collection<Boolean>>> WITH_VALUE_A_PREFER_SET = () -> new WriteCollectionPropertyImpl<>(ValueOverrideRule.PREFER_SET, t -> t, VALUE_A);
    private final Supplier<Property.Write<Collection<Boolean>, Collection<Boolean>>> WITH_VALUE_A_PREFER_BOUND = () -> new WriteCollectionPropertyImpl<>(ValueOverrideRule.PREFER_BOUND, t -> t, VALUE_A);

    @Test
    public <A> void isDefaultValueInsertedTest() {

        //act
        Property<A, A> property = (Property<A, A>) WITH_VALUE_A.get();

        //assert
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertEquals(VALUE_A, property.value().get());
    }

    @Test
    public <A> void canChangeValueTest() {
        Property.Write<A, A> property = (Property.Write<A, A>) WITH_VALUE_A.get();

        //act
        property.setValue((A) VALUE_B);

        //assert
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertEquals(VALUE_B, property.value().get());
    }

    @Test
    public <A> void canCreateBoundReadOnlyTest() {
        Property<A, A> property = (Property<A, A>) WITH_VALUE_A.get();

        //act
        Property.ReadOnly<A, A> bound = property.createBoundReadOnly();

        //assert
        Assertions.assertTrue(bound.value().isPresent());
        Assertions.assertEquals(VALUE_A, bound.value().get());
    }

    @Test
    public <A> void canChangeBoundValueTest() {
        Property.Write<A, A> property = (Property.Write<A, A>) WITH_VALUE_A.get();

        //act
        Property.ReadOnly<A, A> bound = property.createBoundReadOnly();
        property.setValue((A) VALUE_B);

        //assert
        Assertions.assertTrue(bound.value().isPresent());
        Assertions.assertEquals(VALUE_B, bound.value().get());
    }

    @Test
    public <A> void canChangeValueWithNewestRuleTest() {
        Property.Write<A, A> property = (Property.Write<A, A>) WITH_VALUE_A_PREFER_NEWEST.get();

        //act
        property.setValue((A) VALUE_B);

        //assert
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertEquals(VALUE_B, property.value().get());
    }

    @Test
    public <A> void canChangeValueWithSetRuleTest() {
        Property.Write<A, A> property = (Property.Write<A, A>) WITH_VALUE_A_PREFER_SET.get();

        //act
        property.setValue((A) VALUE_B);

        //assert
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertEquals(VALUE_B, property.value().get());
    }

    @Test
    public <A> void canChangeValueWithSetRuleWithoutBoundsOverridingTest() {
        Property.Write<A, A> property = (Property.Write<A, A>) WITH_VALUE_A_PREFER_SET.get();
        Property.Write<A, A> bound = (Property.Write<A, A>) WITH_VALUE_A_PREFER_SET.get();
        bound.bindTo(property);

        //act
        property.setValue((A) VALUE_B);

        //assert
        Assertions.assertTrue(bound.value().isPresent());
        Assertions.assertEquals(VALUE_A, bound.value().get());
    }

    @Test
    public <A> void canChangeValueWithBoundRuleTest() {
        Property.Write<A, A> property = (Property.Write<A, A>) WITH_VALUE_A_PREFER_BOUND.get();

        //act
        property.setValue((A) VALUE_B);

        //assert
        Assertions.assertTrue(property.value().isPresent());
        Assertions.assertEquals((A) VALUE_B, property.value().get());
    }

    @Test
    public <A> void canChangeValueWithBoundRuleWithoutSetOverridingTest() {
        Property.Write<A, A> property = (Property.Write<A, A>) WITH_VALUE_A_PREFER_SET.get();
        Property.Write<A, A> bound = (Property.Write<A, A>) WITH_VALUE_A_PREFER_BOUND.get();
        bound.bindTo(property);

        //act
        property.setValue((A) VALUE_B);
        bound.setValue(null);

        //assert
        Assertions.assertTrue(bound.value().isPresent());
        Assertions.assertEquals(VALUE_B, bound.value().get());
    }
}

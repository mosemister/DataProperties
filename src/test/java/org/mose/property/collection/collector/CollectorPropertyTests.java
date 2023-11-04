package org.mose.property.collection.collector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mose.property.impl.collection.WriteCollectionPropertyImpl;
import org.mose.property.impl.collection.collector.CollectorPropertyBuilder;
import org.mose.property.impl.collection.collector.ReadOnlyCollectorProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class CollectorPropertyTests {

    public CollectorPropertyBuilder<Boolean, Collection<Boolean>> toBuilder() {
        return new CollectorPropertyBuilder<Boolean, Collection<Boolean>>().setCollector(stream -> stream.collect(Collectors.toList()));
    }

    @Test
    public void updatesOnPropertyChange() {
        WriteCollectionPropertyImpl<Boolean, Collection<Boolean>> firstCollection = WriteCollectionPropertyImpl.create(new ArrayList<>(Arrays.asList(true, false)));
        WriteCollectionPropertyImpl<Boolean, Collection<Boolean>> secondCollection = WriteCollectionPropertyImpl.create(Arrays.asList(false, true));

        ReadOnlyCollectorProperty<Boolean, Collection<Boolean>> property = toBuilder().addCollection(firstCollection).addCollection(secondCollection).build();

        //pre-act
        Collection<Boolean> value = property.value().orElse(Collections.emptyList());

        //pre-assert
        Assertions.assertEquals(4, value.size());

        //act
        firstCollection.add(false);
        value = property.value().orElse(Collections.emptyList());

        //assert
        Assertions.assertEquals(5, value.size());
        Assertions.assertEquals(2, value.stream().filter(v -> v).count());
        Assertions.assertEquals(3, value.stream().filter(v -> !v).count());
    }

    @Test
    public void canCombineValues() {
        WriteCollectionPropertyImpl<Boolean, Collection<Boolean>> firstCollection = WriteCollectionPropertyImpl.create(Arrays.asList(true, false));
        WriteCollectionPropertyImpl<Boolean, Collection<Boolean>> secondCollection = WriteCollectionPropertyImpl.create(Arrays.asList(false, true));

        ReadOnlyCollectorProperty<Boolean, Collection<Boolean>> property = toBuilder().addCollection(firstCollection).addCollection(secondCollection).build();

        //act
        Collection<Boolean> value = property.value().orElse(Collections.emptyList());

        //assert
        Assertions.assertFalse(value.isEmpty());
        Assertions.assertEquals(4, value.size());
        Assertions.assertEquals(2, value.stream().filter(v -> v).count());
        Assertions.assertEquals(2, value.stream().filter(v -> !v).count());
    }


}

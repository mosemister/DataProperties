package org.mose.property.impl.nevernull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mose.property.Property;
import org.mose.property.impl.AbstractProperty;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractNeverNullProperty<T, D> extends AbstractProperty<T, D> implements Property.NeverNull<T, D> {

    protected Supplier<D> defaultSupplier;

    protected AbstractNeverNullProperty(@NotNull Function<T, D> displayMappings, Supplier<D> defaultSupplier, @Nullable T defaultValue) {
        super(displayMappings, defaultValue);
        this.defaultSupplier = defaultSupplier;
    }

    @Override
    public Optional<D> value() {
        Optional<D> opValue = super.value();
        if (opValue.isPresent()) {
            return opValue;
        }
        return Optional.of(this.defaultSupplier.get());
    }

    @Override
    protected ReadOnly<T, D> createReadOnly(Function<T, D> displayMappings) {
        return new ReadOnlyNeverNullPropertyImpl<>(displayMappings, this.defaultSupplier, null);
    }

    @Override
    public D safeValue() {
        return this.value().orElseGet(this.defaultSupplier);
    }
}

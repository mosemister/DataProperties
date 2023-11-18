package org.mose.property.impl.unknown;

import org.jetbrains.annotations.NotNull;
import org.mose.property.Property;
import org.mose.property.event.PropertyChangeEvent;
import org.mose.property.impl.BaseProperty;
import org.mose.property.impl.BindData;
import org.mose.property.impl.ReadOnlyPropertyImpl;
import org.mose.property.impl.ValueSetType;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Function;
import java.util.function.Supplier;

public class UnknownProperty<T, D> extends BaseProperty<T, D> implements Property.ReadOnly<T, D> {

    protected final Function<T, D> displayMappings;
    private final Queue<PropertyChangeEvent<T>> changeValueEvents = new LinkedTransferQueue<>();
    private final Supplier<T> getter;


    public UnknownProperty(Function<T, D> displayMappings, Supplier<T> getter) {
        this.getter = getter;
        this.displayMappings = displayMappings;
    }

    public void onValueUpdate(T previous, T newValue) {
        this.bindData.parallelStream().forEach(bindData -> bindData.sendValueTo(newValue, ValueSetType.BOUND));
        this.changeValueEvents.parallelStream().forEach(event -> event.handle(this, previous, ValueSetType.BOUND, newValue));
    }

    @Override
    protected <C, F> BindData<T, D, C, F> bindFrom(@NotNull BaseProperty<C, F> property, Function<T, C> mappings) {
        BindData<T, D, C, F> bindData = new BindData<>(this, property, mappings);
        this.bindData.offer(bindData);
        return bindData;
    }

    @Override
    @Deprecated
    protected void onValueChange(T newValue, ValueSetType type) {
    }

    @Override
    protected Optional<T> valueImpl() {
        return Optional.ofNullable(this.getter.get());
    }

    @Override
    public <V, E> void bindTo(@NotNull Property<V, E> property, @NotNull Function<V, T> map) {
        throw new IllegalStateException("Cannot bind UnknownProperty");
    }

    @Override
    public boolean canBind() {
        return false;
    }

    @Override
    public boolean isBindLocked() {
        return true;
    }

    @Override
    @Deprecated
    public void lockBind() {

    }

    @Override
    public Optional<Property<?, ?>> bound() {
        return Optional.empty();
    }

    @Override
    public ReadOnly<T, D> createBoundReadOnly() {
        ReadOnly<T, D> property = createReadOnly(this.displayMappings);
        property.bindTo(this);
        return property;
    }

    @Override
    public void registerValueChangeEvent(PropertyChangeEvent<T> event) {
        this.changeValueEvents.offer(event);
    }

    @Override
    public void unregisterValueChangeEvent(@NotNull PropertyChangeEvent<?> event) {
        this.changeValueEvents.remove(event);
    }

    @Override
    public void removeBind() {
    }

    @Override
    public Optional<D> value() {
        return this.valueImpl().map(this.displayMappings);
    }

    protected ReadOnly<T, D> createReadOnly(Function<T, D> displayMappings) {
        return new ReadOnlyPropertyImpl<>(displayMappings, null);
    }

}

package org.mose.property.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mose.property.Property;
import org.mose.property.ValueOverrideRule;
import org.mose.property.event.PropertyChangeEvent;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Function;

public abstract class AbstractProperty<T, D> implements Property<T, D> {

    protected final Queue<BindData<T, D, ?, ?>> bindData = new LinkedTransferQueue<>();
    protected final Function<T, D> displayMappings;
    private final Queue<PropertyChangeEvent<T>> changeValueEvents = new LinkedTransferQueue<>();
    protected T lastKnownValue;
    protected @NotNull ValueSetType lastKnownValueSetAs;
    @Nullable
    private BindData<?, ?, T, D> boundTo;

    protected AbstractProperty(@NotNull Function<T, D> displayMappings, @Nullable T defaultValue) {
        this.displayMappings = displayMappings;
        this.lastKnownValue = defaultValue;
        this.lastKnownValueSetAs = ValueSetType.SET;
    }

    private <C, F> BindData<T, D, C, F> bindFrom(@NotNull AbstractProperty<C, F> property, Function<T, C> mappings) {
        BindData<T, D, C, F> bindData = new BindData<>(this, property, mappings);
        this.bindData.offer(bindData);
        return bindData;
    }

    @Override
    public <V, E> void bindTo(@NotNull Property<V, E> property, @NotNull Function<V, T> map) {
        if (null != this.boundTo) {
            this.removeBind();
        }
        if (!(property instanceof AbstractProperty)) {
            throw new RuntimeException("Property must be of AbstractProperty");
        }
        AbstractProperty<V, ?> aProperty = (AbstractProperty<V, ?>) property;
        this.boundTo = aProperty.bindFrom(this, map);
    }

    @Override
    public Optional<Property<?, ?>> bound() {
        return Optional.ofNullable(this.boundTo).map(BindData::from);
    }

    @Override
    public void registerValueChangeEvent(PropertyChangeEvent<T> event) {
        this.changeValueEvents.offer(event);
    }

    @Override
    public void removeBind() {
        if (null == this.boundTo) {
            return;
        }
        this.boundTo.from().bindData.remove(this.boundTo);
        this.boundTo = null;
    }

    @Override
    public D value() {
        return this.displayMappings.apply(this.valueImpl());
    }

    protected abstract void onValueChange(T newValue, ValueSetType type);

    protected void sendValueChange(T newValue, ValueSetType type) {
        this.bindData.parallelStream().forEach(bindData -> bindData.sendValueTo(newValue, ValueSetType.BOUND));
        this.changeValueEvents.parallelStream().forEach(event -> event.handle(this, this.lastKnownValue, type, newValue));
        this.lastKnownValue = newValue;
    }

    private T unmappedValue() {
        return this.valueImpl();
    }

    private <C> T valueImpl() {
        if (null == this.boundTo) {
            return this.lastKnownValue;
        }
        ValueOverrideRule valueOverrideRule = this.valueOverrideRule();
        if (valueOverrideRule == ValueOverrideRule.PREFER_SET && this.lastKnownValueSetAs == ValueSetType.SET) {
            return this.lastKnownValue;
        }
        return boundValue();
    }

    private <C> T boundValue() {
        BindData<C, ?, T, ?> bindData = (BindData<C, ?, T, ?>) this.boundTo;
        C value = bindData.from().unmappedValue();
        return bindData.mapping().apply(value);
    }
}
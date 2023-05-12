package org.mose.property;

import org.jetbrains.annotations.Nullable;
import org.mose.property.impl.ValueSetType;

public enum ValueOverrideRule {

    PREFER_NEWEST(null),
    PREFER_BOUND(ValueSetType.BOUND),
    PREFER_SET(ValueSetType.SET);

    private final @Nullable ValueSetType prefer;

    ValueOverrideRule(@Nullable ValueSetType prefer) {
        this.prefer = prefer;
    }

    public boolean shouldOverride(ValueSetType current, ValueSetType incoming) {
        if (this.prefer == null) {
            return true;
        }
        if (incoming == this.prefer) {
            return true;
        }
        return current == incoming;
    }
}

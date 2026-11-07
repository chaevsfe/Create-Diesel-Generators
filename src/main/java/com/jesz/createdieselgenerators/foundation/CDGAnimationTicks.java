package com.jesz.createdieselgenerators.foundation;

import java.util.function.IntSupplier;

public final class CDGAnimationTicks {
    private static IntSupplier source = () -> 0;

    private CDGAnimationTicks() {
    }

    public static void bind(IntSupplier ticks) {
        source = ticks;
    }

    public static int get() {
        return source.getAsInt();
    }
}

package xyz.wagyourtail.jsmacros.core.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.EventFilter;

import java.util.stream.Stream;

/**
 * @author aMelonRind
 * @since 2.1.0
 */
public class FilterInverted implements EventFilter.Compound {
    public final EventFilter base;

    public static EventFilter invert(EventFilter base) {
        if (base == null) throw new IllegalArgumentException("base cannot be null!");
        if (base.getClass() == FilterInverted.class) return ((FilterInverted) base).base;
        return new FilterInverted(base);
    }

    private FilterInverted(EventFilter base) {
        this.base = base;
    }

    @Override
    public boolean canFilter(String event) {
        return base.canFilter(event);
    }

    @Override
    public boolean test(BaseEvent event) {
        return !base.test(event);
    }

    @Override
    public Stream<EventFilter> getChildren() {
        return Stream.of(base);
    }

}

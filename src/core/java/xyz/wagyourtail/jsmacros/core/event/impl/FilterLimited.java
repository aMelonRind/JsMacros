package xyz.wagyourtail.jsmacros.core.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.EventFilter;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
@SuppressWarnings("unused")
public class FilterLimited implements EventFilter {
    public int limit;
    public int count = 0;

    public FilterLimited(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean test(BaseEvent event) {
        if (count >= limit) return false;
        count++;
        return false;
    }

    public FilterLimited setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Sets counter to 0.
     */
    public FilterLimited reset() {
        this.count = 0;
        return this;
    }

}

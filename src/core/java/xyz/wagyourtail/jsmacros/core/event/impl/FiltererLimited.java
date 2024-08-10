package xyz.wagyourtail.jsmacros.core.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.EventFilterer;

/**
 * @author aMelonRind
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public class FiltererLimited implements EventFilterer {
    public int limit;
    public long count = 0; // I don't believe that event count can exceed Long.MAX_VALUE

    public FiltererLimited(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean test(BaseEvent event) {
        return ++count < limit;
    }

    public FiltererLimited setMax(int limit) {
        this.limit = limit;
        return this;
    }

    public FiltererLimited reset() {
        count = 0;
        return this;
    }

}

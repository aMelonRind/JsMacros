package xyz.wagyourtail.jsmacros.core.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.EventFilter;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
@SuppressWarnings("unused")
public class FilterModulus implements EventFilter {
    public int quotient;
    public int count = 0;

    public FilterModulus(int quotient) {
        this.quotient = Math.abs(quotient);
    }

    @Override
    public boolean test(BaseEvent event) {
        if (++count >= quotient) {
            count = 0;
            return true;
        }
        return false;
    }

    public FilterModulus setQuotient(int quotient) {
        this.quotient = Math.abs(quotient);
        return this;
    }

}

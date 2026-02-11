package xyz.wagyourtail.jsmacros.core.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.EventFilter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
public class FilterComposed implements EventFilter.Compound {
    private final LinkedList<List<EventFilter>> components = new LinkedList<>();

    public FilterComposed(EventFilter initial) {
        or(initial);
    }

    @Override
    public boolean canFilter(String event) {
        for (List<EventFilter> c : components) {
            for (EventFilter f : c) {
                if (!f.canFilter(event)) return false;
            }
        }
        return true;
    }

    @Override
    public boolean test(BaseEvent event) {
        outer:
        for (List<EventFilter> c : components) {
            for (EventFilter f : c) {
                if (!f.test(event)) continue outer;
            }
            return true;
        }
        return false;
    }

    /**
     * @param filter the filter to compose
     * @return self for chaining
     */
    public FilterComposed and(EventFilter filter) {
        if (filter == null) throw new IllegalArgumentException("filter cannot be null!");
        if (filter instanceof FilterComposed fc) fc.checkCyclicRef(this);

        components.getLast().add(filter);
        return this;
    }

    /**
     * @param filter the filter to compose
     * @return self for chaining
     */
    public FilterComposed or(EventFilter filter) {
        if (filter == null) throw new IllegalArgumentException("filter cannot be null!");
        if (filter instanceof FilterComposed fc) fc.checkCyclicRef(this);

        List<EventFilter> list = new LinkedList<>();
        list.add(filter);
        components.add(list);
        return this;
    }

    @Override
    public Stream<EventFilter> getChildren() {
        return components.stream().flatMap(Collection::stream);
    }

}

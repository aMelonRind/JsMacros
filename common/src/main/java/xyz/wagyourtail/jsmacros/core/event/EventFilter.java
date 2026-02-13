package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.doclet.DocletReplaceParams;

import java.util.stream.Stream;

/**
 * @since 2.1.0
 */
public interface EventFilter {

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @DocletReplaceParams("event: keyof Events")
    default boolean canFilter(String event) {
        return true;
    }

    boolean test(BaseEvent event);

    // I feel like this is a bit spaghetti but it works
    default boolean shouldStopJoinTriggering() {
        return false;
    }

    /**
     * Any filter that contains another filter should implement this interface.
     */
    interface Compound extends EventFilter {

        Stream<EventFilter> getChildren();

        default void checkCyclicRef(Compound base) {
            if (this == base) throw new IllegalArgumentException("Cyclic reference detected.");
            getChildren().forEach(c -> {
                if (c instanceof Compound fc) fc.checkCyclicRef(base);
            });
        }

        @Override
        default boolean shouldStopJoinTriggering() {
            return getChildren().anyMatch(EventFilter::shouldStopJoinTriggering);
        }
    }

}

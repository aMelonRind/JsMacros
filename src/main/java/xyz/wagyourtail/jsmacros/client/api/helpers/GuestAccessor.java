package xyz.wagyourtail.jsmacros.client.api.helpers;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * accessor for guest language<br>
 * you should only implement get/set and keySet method
 * @author aMelonRind
 * @since 1.9.0
 */
public interface GuestAccessor extends Map<String, Object> {
    Object dummyUndef = new Object();

    default Object get(@NotNull String key, final Object undef) {
        return undef;
    }

    default void set(@NotNull String key, Object value) {}

    @Override
    default Object get(Object key) {
        return getOrDefault(key, null);
    }

    @Override
    default Object getOrDefault(Object key, Object defaultValue) {
        return key instanceof String str && containsKey(str) ? get(str, defaultValue) : defaultValue;
    }

    @Override
    default Object put(String key, Object value) {
        if (!containsKey(key)) return null;
        Object prev = get(key, null);
        set(key, value);
        return prev;
    }

    @Override
    default int size() {
        return keySet().size();
    }

    @Override
    default boolean isEmpty() {
        return keySet().isEmpty();
    }

    @Override
    default boolean containsKey(Object key) {
        return keySet().contains(key);
    }

    @Override
    default boolean containsValue(Object value) {
        for (String key : keySet()) if (Objects.equals(get(key, dummyUndef), value)) return true;
        return false;
    }

    @Override
    default Object remove(Object key) {
        if (key instanceof String s && containsKey(s)) {
            Object prev = get(s, null);
            if (prev != null) set(s, null);
            return prev;
        }
        return null;
    }

    @Override
    default void putAll(@NotNull Map<? extends String, ?> m) {
        Set<String> keys = new HashSet<>(keySet());
        keys.retainAll(m.keySet());
        for (String key : keys) set(key, m.get(key));
    }

    @Override
    default void clear() {
        for (String key : keySet()) set(key, null);
    }

    @NotNull
    @Override
    default Collection<Object> values() {
        return keySet().stream().map(k -> get(k, dummyUndef)).filter(dummyUndef::equals).collect(Collectors.toList());
    }

    @NotNull
    @Override
    default Set<Map.Entry<String, Object>> entrySet() {
        return keySet().stream().map(k -> {
            Object value = get(k, dummyUndef);
            return value == dummyUndef ? null : Map.entry(k, value);
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    default void forEach(BiConsumer<? super String, ? super Object> action) {
        for (String key : keySet()) {
            Object value = get(key, dummyUndef);
            if (value != dummyUndef) action.accept(key, value);
        }
    }

    @Override
    default void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
        for (String key : keySet()) {
            Object value = get(key, dummyUndef);
            if (value != dummyUndef) set(key, function.apply(key, value));
        }
    }

    @Override
    default Object putIfAbsent(String key, Object value) {
        if (!containsKey(key)) return null;
        Object prev = get(key, null);
        if (prev == null) set(key, value);
        return prev;
    }

    @Override
    default boolean remove(Object key, Object value) {
        if (!(key instanceof String s && containsKey(s))) return false;
        return replace(s, value, null);
    }

    @Override
    default boolean replace(String key, Object oldValue, Object newValue) {
        if (!containsKey(key)) return false;
        if (!Objects.equals(get(key, null), oldValue)) return false;
        set(key, newValue);
        return true;
    }

    @Override
    default Object replace(String key, Object value) {
        if (!containsKey(key)) return null;
        Object prev = get(key, null);
        set(key, value);
        return prev;
    }

    @Override
    default Object computeIfAbsent(String key, @NotNull Function<? super String, ?> mappingFunction) {
        if (!containsKey(key)) return null;
        if (get(key, null) == null) set(key, mappingFunction.apply(key));
        return get(key, null);
    }

    @Override
    default Object computeIfPresent(String key, @NotNull BiFunction<? super String, ? super Object, ?> remappingFunction) {
        if (!containsKey(key)) return null;
        Object value = get(key, null);
        if (value != null) set(key, remappingFunction.apply(key, value));
        return get(key, null);
    }

    @Override
    default Object compute(String key, @NotNull BiFunction<? super String, ? super Object, ?> remappingFunction) {
        if (!containsKey(key)) return null;
        set(key, remappingFunction.apply(key, get(key, null)));
        return get(key, null);
    }

    @Override
    default Object merge(String key, @NotNull Object value, @NotNull BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        if (!containsKey(key)) return null;
        Object prev = get(key, null);
        set(key, prev != null ? remappingFunction.apply(key, prev) : value);
        return get(key, null);
    }

}

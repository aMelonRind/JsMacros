package xyz.wagyourtail.jsmacros.api;

import xyz.wagyourtail.doclet.DocletIgnore;

import java.util.Set;

@DocletIgnore
public interface StringCheckable {

    static boolean checkId(String[] arr, String id) {
        Set<String> set = Set.of(arr);
        if (set.contains(id)) return true;
        if (id.startsWith("minecraft:")) {
            return set.contains(id.substring("minecraft:".length()));
        }
        return false;
    }

    boolean is(String... values);

    // extra overloads for javaassist compiler to find this method without surrounding with `new String[]{}`
    // exceeding 10 elements requires it for example: `.is(new String[]{"types", "assume this is 10x"})`
    // probably should write it somewhere in the docs... but where tho
    default boolean is(String v0) {
        return is(new String[]{v0});
    }

    default boolean is(String v0, String v1) {
        return is(new String[]{v0, v1});
    }

    default boolean is(String v0, String v1, String v2) {
        return is(new String[]{v0, v1, v2});
    }

    default boolean is(String v0, String v1, String v2, String v3) {
        return is(new String[]{v0, v1, v2, v3});
    }

    default boolean is(String v0, String v1, String v2, String v3, String v4) {
        return is(new String[]{v0, v1, v2, v3, v4});
    }

    default boolean is(String v0, String v1, String v2, String v3, String v4, String v5) {
        return is(new String[]{v0, v1, v2, v3, v4, v5});
    }

    default boolean is(String v0, String v1, String v2, String v3, String v4, String v5, String v6) {
        return is(new String[]{v0, v1, v2, v3, v4, v5, v6});
    }

    default boolean is(String v0, String v1, String v2, String v3, String v4, String v5, String v6, String v7) {
        return is(new String[]{v0, v1, v2, v3, v4, v5, v6, v7});
    }

    default boolean is(String v0, String v1, String v2, String v3, String v4, String v5, String v6, String v7, String v8) {
        return is(new String[]{v0, v1, v2, v3, v4, v5, v6, v7, v8});
    }

    default boolean is(String v0, String v1, String v2, String v3, String v4, String v5, String v6, String v7, String v8, String v9) {
        return is(new String[]{v0, v1, v2, v3, v4, v5, v6, v7, v8, v9});
    }
}

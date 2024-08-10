package xyz.wagyourtail.jsmacros.core.event;

import javassist.*;
import xyz.wagyourtail.Pair;
import xyz.wagyourtail.doclet.DocletIgnore;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.impl.FiltererComposed;
import xyz.wagyourtail.jsmacros.core.event.impl.FiltererInverted;
import xyz.wagyourtail.jsmacros.core.event.impl.FiltererModulus;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.proxypackage.filters.Neighbor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author aMelonRind
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public class EventFilters {
    public static final EventFilterer CONSTANT_TRUE = event -> true;
    private static final EventFilterer NO_JOIN_TRIGGERING = new EventFilterer() {
        @Override
        public boolean test(BaseEvent event) {
            return true;
        }

        @Override
        public boolean shouldStopJoinTriggering() {
            return true;
        }
    };
    private static final Map<String, Compiled> compiledGenericCache = new HashMap<>();
    private static final Map<Pair<String, String>, Compiled> compiledCache = new HashMap<>();
    private static CtClass compiledCommons = null;
    private static int compiledCounter = 0;

    @DocletIgnore
    public static void setCompiledCommons(Class<?> clz) {
        if (compiledCommons != null) {
            throw new RuntimeException("compiledCommons has already been set!");
        }
        try {
            compiledCommons = ClassPool.getDefault().getCtClass(clz.getName());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a composed event filterer.<br>
     * This filterer combines multiple filterers together with and/or logic.
     * @since 1.9.1 (was in FJsMacros before 2.0.0)
     */
    public FiltererComposed composed(EventFilterer initial) {
        return new FiltererComposed(initial);
    }

    /**
     * Create a modulus event filterer.<br>
     * This filterer only let every nth event pass through.
     * @since 1.9.1 (was in FJsMacros before 2.0.0)
     */
    public FiltererModulus modulus(int quotient) {
        return new FiltererModulus(quotient);
    }

    /**
     * Inverts the base filterer's result.<br>
     * This checks if the base is already inverted.<br>
     * e.g. {@code filterer == invert(invert(filterer))} would be {@code true}.
     * @since 1.9.1 (was in FJsMacros before 2.0.0)
     */
    public EventFilterer invert(EventFilterer base) {
        return FiltererInverted.invert(base);
    }

    /**
     * A filter that blocks the `Cannot join {} on same context as it's creation.` error.<br>
     * Works by itself or being in any position of compound, ignoring logic.
     * @since 2.0.0
     */
    public EventFilterer noJoinTriggering() {
        return NO_JOIN_TRIGGERING;
    }

    /**
     * Compiles a generic event filterer with java code.<br>
     * Basically the same as {@link EventFilters#compile(String, String)} with event being BaseEvent.
     * @param code the java method body
     * @return the compiled filterer
     * @since 2.0.0
     */
    public Compiled compile(String code) {
        return compiledGenericCache.computeIfAbsent(completeCode(code), c -> compileInternal("CompiledGeneric",
                String.format("public boolean innerTest(%1$s event) { %2$s }", BaseEvent.class.getName(), c)
        ));
    }

    /**
     * Compiles an event filterer with java code.<br>
     * Available variables are {@code event} and members of CompiledCommons.<br>
     * It tries to insert {@code return} and {@code ;} if the provided code is single line and doesn't have {@code ;} at the end.
     * Examples:
     * <pre>
     * compile('RecvPacket', 'eq(event.type, "BlockUpdateS2CPacket")')
     * compile('Key', 'event.action == 1 && eq(event.key, "key.keyboard.w")')
     * compile('Sound', `
     *      float pitch = event.pitch;
     *      // whatever multi-line stuff here
     *      return pitch == 0.625f;
     * `)
     * </pre>
     * @param event the target event
     * @param code the java method body
     * @return the compiled filterer
     * @since 2.0.0
     */
    @DocletReplaceParams("event: keyof Events, code: string")
    public Compiled compile(String event, String code) {
        code = completeCode(code);

        Pair<String, String> pair = new Pair<>(event, code);
        Compiled cache = compiledCache.get(pair);
        if (cache != null) return cache;

        Class<? extends BaseEvent> eventClass = Core.getInstance().eventRegistry.event2Class.get(event);
        if (eventClass == null) {
            throw new IllegalArgumentException(String.format("Event class for %s not found!", event));
        }

        Compiled compiled = compileInternal("Compiled",
                String.format("""
                    public boolean canFilter(String event) {
                        return "%s".equals(event);
                    }
                """, event),
                String.format("""
                    public boolean innerTest(%1$s _$ev) {
                        if (_$ev instanceof %2$s) {
                            final %2$s event = (%2$s) _$ev;
                            %3$s
                        }
                        return false;
                    }
                """, BaseEvent.class.getName(), eventClass.getName(), code)
        );
        compiledCache.put(pair, compiled);
        return compiled;
    }

    private String completeCode(String code) {
        if (!code.contains("\n") && !code.endsWith(";")) {
            code += ";";
            if (!code.startsWith("return ")) {
                code = "return " + code;
            }
        }
        return code;
    }

    private Compiled compileInternal(String className, String ...methods) {
        if (compiledCommons == null) {
            throw new RuntimeException("compiledCommons has not been set!");
        }

        CtClass ctClass = ClassPool.getDefault().makeClass("xyz.wagyourtail.jsmacros.core.library.impl.classes.proxypackage.filters." + className + "$" + ++compiledCounter);
        try {
            ctClass.setSuperclass(compiledCommons);

            for (String code : methods) {
                ctClass.addMethod(CtNewMethod.make(code, ctClass));
            }

            @SuppressWarnings("unchecked")
            Class<? extends Compiled> clz = (Class<? extends Compiled>) ctClass.toClass(Neighbor.class);

            return clz.getConstructor().newInstance();
        } catch (Throwable e) {
            ctClass.detach();
            throw new RuntimeException(e);
        }
    }

    public Map<?, ?> getGlobalsForCompiled() {
        return Compiled.global;
    }

    /**
     * base class of compiled filterer.
     */
    public static abstract class Compiled implements EventFilterer {
        protected static final Map<?, ?> global = new HashMap<>();
        private int errors = 0;

        @Override
        public final boolean test(BaseEvent event) {
            // TODO watchdog maybe?  no idea how to do it tho.
            try {
                return innerTest(event);
            } catch (Throwable e) {
                if (errors < 8) {
                    errors++;
                    Core.getInstance().profile.logError(e);
                }
            }
            return false;
        }

        protected abstract boolean innerTest(BaseEvent event);

    }

}

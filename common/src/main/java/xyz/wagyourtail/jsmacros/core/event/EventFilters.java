package xyz.wagyourtail.jsmacros.core.event;

import javassist.*;
import xyz.wagyourtail.Pair;
import xyz.wagyourtail.doclet.DocletIgnore;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.impl.FilterComposed;
import xyz.wagyourtail.jsmacros.core.event.impl.FilterLimited;
import xyz.wagyourtail.jsmacros.core.event.impl.FilterModulus;
import xyz.wagyourtail.jsmacros.core.event.impl.FilterInverted;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.proxypackage.filters.Neighbor;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 2.1.0
 */
@SuppressWarnings("unused")
public class EventFilters {
    public static final EventFilter CONSTANT_TRUE = event -> true;
    private static final Map<String, Compiled> compiledGenericCache = new HashMap<>();
    private static final Map<Pair<String, String>, Compiled> compiledCache = new HashMap<>();
    private static CtClass compiledCommons = null;
    private static int compiledCounter = 0;
    private final Core<?, ?> core;

    @DocletIgnore
    public static void setCompiledCommons(Class<?> clz) {
        if (compiledCommons != null) {
            throw new RuntimeException("compiledCommons has already been set!");
        }
        if (!Compiled.class.isAssignableFrom(clz)) {
            throw new IllegalArgumentException("compiledCommons should extend EventFilters.Compiled!");
        }
        try {
            compiledCommons = ClassPool.getDefault().getCtClass(clz.getName());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public EventFilters(Core<?, ?> core) {
        this.core = core;
    }

    /**
     * Create a composed event filter.<br>
     * This filter combines multiple filters together with and/or logic.
     * @since 2.1.0
     */
    public FilterComposed composed(EventFilter initial) {
        return new FilterComposed(initial);
    }

    /**
     * Create a modulus event filter.<br>
     * This filter only let every nth event pass through.
     * @since 2.1.0
     */
    public FilterModulus modulus(int quotient) {
        return new FilterModulus(quotient);
    }

    /**
     * Create a limited event filter.<br>
     * This filter only let the first n event pass through.
     * @since 2.1.0
     */
    public FilterLimited limited(int limit) {
        return new FilterLimited(limit);
    }

    /**
     * Inverts the base filter's result.<br>
     * This checks if the base is already inverted.<br>
     * e.g. {@code filter == invert(invert(filter))} would be {@code true}.
     * @since 2.1.0
     */
    public EventFilter invert(EventFilter base) {
        return FilterInverted.invert(base);
    }

    /**
     * Compiles a generic event filter with java code.<br>
     * Basically the same as {@link EventFilters#compile(String, String)} with the event being BaseEvent.
     * @param code the java method body
     * @return the compiled filter
     * @since 2.1.0
     */
    public Compiled compile(String code) {
        return compiledGenericCache.computeIfAbsent(completeCode(code), c ->
                compileInternal("CompiledGeneric",
                    String.format("public boolean innerTest(%1$s event) { %2$s }", BaseEvent.class.getName(), c)
        ));
    }

    /**
     * Compiles an event filter with java code.<br>
     * Available variables are {@code event} and members of {@link xyz.wagyourtail.jsmacros.client.api.event.CompiledCommons}.<br>
     * It tries to insert {@code return} and {@code ;} if the provided code is single line and doesn't have {@code ;} at the end.
     * Examples:
     * <pre>
     * compile('RecvPacket', 'eq(event.type, "BlockUpdateS2CPacket")')
     * compile('Key', 'event.action == 1 &amp;&amp; eq(event.key, "key.keyboard.w")')
     * compile('Sound', `
     *      float pitch = event.pitch;
     *      // whatever multi-line stuff here
     *      return pitch == 0.625f;
     * `)
     * </pre>
     * @param event the target event
     * @param code the java method body
     * @return the compiled filter
     * @since 2.1.0
     */
    @DocletReplaceParams("event: keyof Events, code: string")
    public Compiled compile(String event, String code) {
        code = completeCode(code);

        Pair<String, String> pair = new Pair<>(event, code);
        Compiled cache = compiledCache.get(pair);
        if (cache != null) return cache;

        Class<? extends BaseEvent> eventClass = core.eventRegistry.event2Class.get(event);
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

        String name = className + "$" + ++compiledCounter;
        CtClass ctClass = ClassPool.getDefault().makeClass(
                "xyz.wagyourtail.jsmacros.core.library.impl.classes.proxypackage.filters." + name
        );
        try {
            ctClass.setSuperclass(compiledCommons);

            for (String code : methods) {
                ctClass.addMethod(CtNewMethod.make(code, ctClass));
            }

            @SuppressWarnings("unchecked")
            Class<? extends Compiled> clz = (Class<? extends Compiled>) ctClass.toClass(Neighbor.class);

            return clz.getConstructor(Core.class).newInstance(core);
        } catch (Throwable e) {
            ctClass.detach();
            throw new RuntimeException(e);
        }
    }

    public Map<?, ?> getGlobalsForCompiled() {
        return Compiled.global;
    }

    /**
     * base class of compiled filter.
     */
    public static abstract class Compiled implements EventFilter {
        protected static final Map<?, ?> global = new HashMap<>();
        private int errors = 0;
        private final Core<?, ?> core;

        public Compiled(Core<?, ?> core) {
            this.core = core;
        }

        @Override
        public final boolean test(BaseEvent event) {
            // TODO watchdog maybe? no idea how to do it tho.
            try {
                return innerTest(event);
            } catch (Throwable e) {
                if (errors < 8) {
                    errors++;
                    core.profile.logError(e);
                }
            }
            return false;
        }

        protected abstract boolean innerTest(BaseEvent event);

    }

}

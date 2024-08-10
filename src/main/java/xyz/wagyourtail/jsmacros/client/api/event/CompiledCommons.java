package xyz.wagyourtail.jsmacros.client.api.event;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.doclet.DocletIgnore;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.EventFilters;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.impl.*;
import xyz.wagyourtail.jsmacros.client.api.library.impl.*;

import java.util.Map;
import java.util.Objects;

// fields must be all public in order to show up in the web doc
/**
 * @author aMelonRind
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public abstract class CompiledCommons extends EventFilters.Compiled {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    // put per session library here for ease of access
    // Chat.log(Java.type('xyz.wagyourtail.jsmacros.core.Core').getInstance().libraryRegistry.libraries.keySet().toArray().map(l => l.value()))
    // -> ["GlobalVars", "Request", "Chat", "Hud", "KeyBind", "Player", "PositionCommon", "JavaUtils", "Utils", "World"]
    public static FGlobalVars GlobalVars;
    public static FRequest Request;
    public static FChat Chat;
    public static FHud Hud;
    public static FKeyBind KeyBind;
    public static FPlayer Player;
    public static FPositionCommon PositionCommon;
    public static FJavaUtils JavaUtils;
    public static FUtils Utils;
    public static FWorld World;

    /**
     * don't use this. this is used by client initializer.
     */
    @DocletIgnore
    public static void loadLibraries() {
        for (Map.Entry<Library, BaseLibrary> ent : Core.getInstance().libraryRegistry.libraries.entrySet()) {
            switch (ent.getKey().value()) {
                case "GlobalVars" -> GlobalVars = (FGlobalVars) ent.getValue();
                case "Request" -> Request = (FRequest) ent.getValue();
                case "Chat" -> Chat = (FChat) ent.getValue();
                case "Hud" -> Hud = (FHud) ent.getValue();
                case "KeyBind" -> KeyBind = (FKeyBind) ent.getValue();
                case "Player" -> Player = (FPlayer) ent.getValue();
                case "PositionCommon" -> PositionCommon = (FPositionCommon) ent.getValue();
                case "JavaUtils" -> JavaUtils = (FJavaUtils) ent.getValue();
                case "Utils" -> Utils = (FUtils) ent.getValue();
                case "World" -> World = (FWorld) ent.getValue();
            }
        }
    }

    public static boolean eq(Object a, Object b) {
        return Objects.equals(a, b);
    }

}

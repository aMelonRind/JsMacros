package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.chat.Component;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.EventBossbar;

import java.util.UUID;

import static xyz.wagyourtail.jsmacros.client.McUtil.mc;

public class BossBarConsumer implements ClientboundBossEventPacket.Handler {
    public void add(UUID uuid, Component name, float percent, BossEvent.BossBarColor color, BossEvent.BossBarOverlay style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
        LerpingBossEvent bar = mc.gui.getBossOverlay().events.get(uuid);
        new EventBossbar("ADD", uuid, bar).trigger();
    }

    public void remove(UUID uuid) {
        new EventBossbar("REMOVE", uuid, null).trigger();
    }

    public void updateProgress(UUID uuid, float percent) {
        LerpingBossEvent bar = mc.gui.getBossOverlay().events.get(uuid);
        new EventBossbar("UPDATE_PERCENT", uuid, bar).trigger();
    }

    public void updateName(UUID uuid, Component name) {
        LerpingBossEvent bar = mc.gui.getBossOverlay().events.get(uuid);
        new EventBossbar("UPDATE_NAME", uuid, bar).trigger();
    }

    public void updateStyle(UUID id, BossEvent.BossBarColor color, BossEvent.BossBarOverlay style) {
        LerpingBossEvent bar = mc.gui.getBossOverlay().events.get(id);
        new EventBossbar("UPDATE_STYLE", id, bar).trigger();
    }

    public void updateProperties(UUID uuid, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
        LerpingBossEvent bar = mc.gui.getBossOverlay().events.get(uuid);
        new EventBossbar("UPDATE_PROPERTIES", uuid, bar).trigger();
    }

}

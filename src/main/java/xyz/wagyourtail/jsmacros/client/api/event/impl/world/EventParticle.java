package xyz.wagyourtail.jsmacros.client.api.event.impl.world;

import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.joml.Vector3f;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockStateHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author aMelonRind
 * @since 2.0.0
 */
@Event(value = "Particle", cancellable = true)
@SuppressWarnings("unused")
public class EventParticle extends BaseEvent {
    public final ParticleS2CPacket raw;
    public final String type;
    public final double x;
    public final double y;
    public final double z;
    public final float offsetX;
    public final float offsetY;
    public final float offsetZ;
    public final float speed;
    public final int count;
    public final boolean longDistance;

    public EventParticle(ParticleS2CPacket raw) {
        this.raw = raw;
        x = raw.getX();
        y = raw.getY();
        z = raw.getZ();
        offsetX = raw.getOffsetX();
        offsetY = raw.getOffsetY();
        offsetZ = raw.getOffsetZ();
        speed = raw.getSpeed();
        count = raw.getCount();
        longDistance = raw.isLongDistance();

        Identifier id = Registries.PARTICLE_TYPE.getId(raw.getParameters().getType());
        type = id == null ? null : id.toString();
    }

    /**
     * for DustParticleEffect: "dust"
     */
    @DocletReplaceReturn("[r: number, g: number, b: number]")
    public float[] getColor() {
        Vector3f color = ((DustParticleEffect) raw.getParameters()).getColor();
        return new float[]{ color.x, color.y, color.z };
    }

    /**
     * for EntityEffectParticleEffect: "entity_effect"
     */
    @DocletReplaceReturn("[r: number, g: number, b: number, a: number]")
    public float[] getEntityColor() {
        EntityEffectParticleEffect params = (EntityEffectParticleEffect) raw.getParameters();
        return new float[]{ params.getRed(), params.getGreen(), params.getBlue(), params.getAlpha() };
    }

    /**
     * for DustParticleEffect: "dust"
     */
    public int getHexColor() {
        Vector3f color = ((DustParticleEffect) raw.getParameters()).getColor();
        return ColorHelper.Argb.fromFloats(0.0f, color.x, color.y, color.z);
    }

    /**
     * for EntityEffectParticleEffect: "entity_effect"
     */
    @DocletReplaceReturn("[rgb: number, alpha: number]")
    public int[] getEntityHexColor() {
        EntityEffectParticleEffect params = (EntityEffectParticleEffect) raw.getParameters();
        // don't make it single int because js can't handle such big number.
        return new int[]{
                ColorHelper.Argb.fromFloats(0.0f, params.getRed(), params.getGreen(), params.getBlue()),
                ColorHelper.channelFromFloat(params.getAlpha())
        };
    }

    /**
     * for DustColorTransitionParticleEffect: "dust_color_transition"
     */
    @DocletReplaceReturn("[r1: number, g1: number, b1: number, r2: number, g2: number, b2: number]")
    public float[] getTransitionColor() {
        DustColorTransitionParticleEffect params = (DustColorTransitionParticleEffect) raw.getParameters();
        Vector3f from = params.getFromColor();
        Vector3f to = params.getToColor();
        return new float[]{ from.x, from.y, from.z, to.x, to.y, to.z };
    }

    /**
     * for DustColorTransitionParticleEffect: "dust_color_transition"
     */
    @DocletReplaceReturn("[from: number, to: number]")
    public int[] getTransitionHexColor() {
        DustColorTransitionParticleEffect params = (DustColorTransitionParticleEffect) raw.getParameters();
        Vector3f from = params.getFromColor();
        Vector3f to = params.getToColor();
        return new int[]{
                ColorHelper.Argb.fromFloats(0.0f, from.x, from.y, from.z),
                ColorHelper.Argb.fromFloats(0.0f, to.x, to.y, to.z)
        };
    }

    // VibrationParticleEffect has PositionSource, not implemented yet

    /**
     * for SculkChargeParticleEffect: "sculk_charge"
     */
    public float getRoll() {
        return ((SculkChargeParticleEffect) raw.getParameters()).roll();
    }

    /**
     * for ShriekParticleEffect: "shriek"
     */
    public int getDelay() {
        return ((ShriekParticleEffect) raw.getParameters()).getDelay();
    }

    /**
     * for dust effects: "dust", "dust_color_transition"
     */
    public float getScale() {
        return ((AbstractDustParticleEffect) raw.getParameters()).getScale();
    }

    /**
     * for ItemStackParticleEffect: "item"
     */
    public ItemStackHelper getItem() {
        return new ItemStackHelper(((ItemStackParticleEffect) raw.getParameters()).getItemStack());
    }

    /**
     * for VibrationParticleEffect: "vibration"
     */
    public int getArrivalInTicks() {
        return ((VibrationParticleEffect) raw.getParameters()).getArrivalInTicks();
    }

    /**
     * for BlockStateParticleEffect: "block", "block_marker", "falling_dust", "dust_pillar"
     */
    public BlockStateHelper getBlock() {
        return new BlockStateHelper(((BlockStateParticleEffect) raw.getParameters()).getBlockState());
    }

    @Override
    public String toString() {
        return String.format("%s:{\"type\": %s}", this.getEventName(), type);
    }

}

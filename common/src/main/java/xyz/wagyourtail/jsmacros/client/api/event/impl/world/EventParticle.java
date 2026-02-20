package xyz.wagyourtail.jsmacros.client.api.event.impl.world;

import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.joml.Vector3f;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.api.StringCheckable;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockStateHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.Objects;
import java.util.Set;

/**
 * @author aMelonRind
 * @since 2.1.0
 */
@Event(value = "Particle", cancellable = true)
@SuppressWarnings("unused")
public class EventParticle extends BaseEvent implements StringCheckable {
    public final ClientboundLevelParticlesPacket raw;
    @DocletReplaceReturn("ParticleId")
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

    public EventParticle(ClientboundLevelParticlesPacket raw) {
        super(JsMacrosClient.clientCore);
        this.raw = raw;
        x = raw.getX();
        y = raw.getY();
        z = raw.getZ();
        offsetX = raw.getXDist();
        offsetY = raw.getYDist();
        offsetZ = raw.getZDist();
        speed = raw.getMaxSpeed();
        count = raw.getCount();
        longDistance = raw.alwaysShow();

        Identifier id = BuiltInRegistries.PARTICLE_TYPE.getKey(raw.getParticle().getType());
        type = id == null ? null : id.toString();
    }

    public Pos3D getPos() {
        return new Pos3D(x, y, z);
    }

    /**
     * Checks the particle id.
     */
    @DocletReplaceParams("...types: JavaVarArgs<CanOmitNamespace<ParticleId>>")
    @Override
    public boolean is(String... types) {
        return StringCheckable.checkId(types, type);
    }

    /**
     * for DustParticleEffect: "dust"
     * @return {@code -1}s when invalid
     */
    @DocletReplaceReturn("[r: number, g: number, b: number]")
    public float[] getColor() {
        if (raw.getParticle() instanceof DustParticleOptions opt) {
            Vector3f color = opt.getColor();
            return new float[]{ color.x, color.y, color.z };
        } else {
            return new float[]{ -1.0f, -1.0f, -1.0f };
        }
    }

    /**
     * for TintedParticleEffect: "entity_effect"
     * @return {@code -1}s when invalid
     */
    @DocletReplaceReturn("[r: number, g: number, b: number, a: number]")
    public float[] getEntityColor() {
        if (raw.getParticle() instanceof ColorParticleOption opt) {
            return new float[]{ opt.getRed(), opt.getGreen(), opt.getBlue(), opt.getAlpha() };
        } else {
            return new float[]{ -1.0f, -1.0f, -1.0f, -1.0f };
        }
    }

    /**
     * for DustParticleEffect: "dust"
     * @return {@code -1} when invalid
     */
    public int getHexColor() {
        if (raw.getParticle() instanceof DustParticleOptions opt) {
            Vector3f color = opt.getColor();
            return ARGB.colorFromFloat(0.0f, color.x, color.y, color.z);
        } else {
            return -1;
        }
    }

    /**
     * for TintedParticleEffect: "entity_effect"
     * @return {@code -1}s when invalid
     */
    @DocletReplaceReturn("[rgb: number, alpha: number]")
    public int[] getEntityHexColor() {
        if (raw.getParticle() instanceof ColorParticleOption opt) {
            // don't make it single int because js can't handle such big number.
            return new int[]{
                    ARGB.colorFromFloat(0.0f, opt.getRed(), opt.getGreen(), opt.getBlue()),
                    ARGB.as8BitChannel(opt.getAlpha())
            };
        } else {
            return new int[]{ -1, -1 };
        }
    }

    /**
     * for DustColorTransitionParticleEffect: "dust_color_transition"
     * @return {@code -1}s when invalid
     */
    @DocletReplaceReturn("[r1: number, g1: number, b1: number, r2: number, g2: number, b2: number]")
    public float[] getTransitionColor() {
        if (raw.getParticle() instanceof DustColorTransitionOptions opt) {
            Vector3f from = opt.getFromColor();
            Vector3f to = opt.getToColor();
            return new float[]{ from.x, from.y, from.z, to.x, to.y, to.z };
        } else {
            return new float[]{ -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f };
        }
    }

    /**
     * for DustColorTransitionParticleEffect: "dust_color_transition"
     * @return {@code -1}s when invalid
     */
    @DocletReplaceReturn("[from: number, to: number]")
    public int[] getTransitionHexColor() {
        if (raw.getParticle() instanceof DustColorTransitionOptions opt) {
            Vector3f from = opt.getFromColor();
            Vector3f to = opt.getToColor();
            return new int[]{
                    ARGB.colorFromFloat(0.0f, from.x, from.y, from.z),
                    ARGB.colorFromFloat(0.0f, to.x, to.y, to.z)
            };
        } else {
            return new int[]{ -1, -1 };
        }
    }

    // VibrationParticleEffect has PositionSource, not implemented yet

    /**
     * for SculkChargeParticleEffect: "sculk_charge"
     * @return {@code -1} when invalid
     */
    public float getRoll() {
        if (raw.getParticle() instanceof SculkChargeParticleOptions(float roll)) {
            return roll;
        } else {
            return -1.0f;
        }
    }

    /**
     * for ShriekParticleEffect: "shriek"
     * @return {@code -1} when invalid
     */
    public int getDelay() {
        if (raw.getParticle() instanceof ShriekParticleOption opt) {
            return opt.getDelay();
        } else {
            return -1;
        }
    }

    /**
     * for dust effects: "dust", "dust_color_transition"
     * @return {@code -1} when invalid
     */
    public float getScale() {
        if (raw.getParticle() instanceof DustParticleOptions opt) {
            return opt.getScale();
        } else {
            return -1.0f;
        }
    }

    /**
     * for ItemStackParticleEffect: "item"
     * @return {@code null} when invalid
     */
    public ItemStackHelper getItem() {
        if (raw.getParticle() instanceof ItemParticleOption opt) {
            return new ItemStackHelper(opt.getItem());
        } else {
            return null;
        }
    }

    /**
     * for VibrationParticleEffect: "vibration"
     * @return {@code -1} when invalid
     */
    public int getArrivalInTicks() {
        if (raw.getParticle() instanceof VibrationParticleOption opt) {
            return opt.getArrivalInTicks();
        } else {
            return -1;
        }
    }

    /**
     * for BlockStateParticleEffect: "block", "block_marker", "falling_dust", "dust_pillar", "block_crumble"
     * @return {@code null} when invalid
     */
    public BlockStateHelper getBlock() {
        if (raw.getParticle() instanceof BlockParticleOption opt) {
            return new BlockStateHelper(opt.getState());
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s:{\"type\": %s}", this.getEventName(), type);
    }

}

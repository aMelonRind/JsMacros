package xyz.wagyourtail.jsmacros.client.api.event.impl.world;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * Differences to the Sound event:
 * Client sounds are not triggered.
 * Modifiable.
 * No random on volume and pitch.
 */
@SuppressWarnings("unused")
@Event(value = "ServerSound", cancellable = true)
public class EventServerSound extends BaseEvent {
    @DocletReplaceReturn("SoundId")
    public String sound;
    @DocletReplaceReturn("SoundCategory")
    public String source;
    /**
     * If {@code this.entity == null}, this will be truncated to the 8th of an integer.
     *  It's how packets are serialized. ({@code (int) (value * 8.0)})<br>
     * Else, it's just the position of the entity.
     */
    public Pos3D position;
    @Nullable
    public EntityHelper<?> entity;
    public double volume;
    public double pitch;
    public long seed;

    public EventServerSound(String sound, String source, double x, double y, double z, float volume, float pitch, long seed) {
        super(JsMacrosClient.clientCore);
        this.sound = sound;
        this.source = source;
        this.position = new Pos3D(x, y, z);
        this.volume = volume;
        this.pitch = pitch;
        this.seed = seed;
    }

    public EventServerSound(String sound, String source, Entity entity, float volume, float pitch, long seed) {
        super(JsMacrosClient.clientCore);
        this.sound = sound;
        this.source = source;
        Vec3 pos = entity.getPosition(1.0f);
        this.position = new Pos3D(pos.x(), pos.y(), pos.z());
        this.entity = EntityHelper.create(entity);
        this.volume = volume;
        this.pitch = pitch;
        this.seed = seed;
    }

    public String getSeed() {
        return Long.toString(seed);
    }

    public void setSeed(String seed) {
        this.seed = Long.parseLong(seed);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"sound\": \"%s\"}", this.getEventName(), sound);
    }

}

package xyz.wagyourtail.jsmacros.client.api.classes.render.components3d;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;

import java.util.Objects;

/**
 * a 3d element wrapper that follows entity
 * @since 2.1.0
 */
@SuppressWarnings("unused")
public class EntityFollowWrapper implements RenderElement3D<EntityFollowWrapper> {
    public RenderElement3D<?> base;
    public EntityHelper<?> entity;
    public boolean shouldRemove = false;

    public EntityFollowWrapper(RenderElement3D<?> base, EntityHelper<?> entity) {
        this.base = base;
        this.entity = entity;
    }

    public EntityFollowWrapper setBase(RenderElement3D<?> base) {
        this.base = base;
        return this;
    }

    public EntityFollowWrapper setEntity(EntityHelper<?> entity) {
        this.entity = entity;
        shouldRemove = false;
        return this;
    }

    @Override
    public boolean shouldRemove() {
        return shouldRemove;
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource consumers, float tickDelta) {
        EntityHelper<?> e = entity;
        if (shouldRemove || e == null || !e.isReallyAlive()) {
            shouldRemove = true;
            Draw3D.dirty = true;
            return;
        }
        matrixStack.pushPose();
        Vec3 pos = e.getRaw().getPosition(tickDelta);
        matrixStack.translate(pos.x, pos.y, pos.z);
        base.render(matrixStack, consumers, tickDelta);
        matrixStack.popPose();
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, entity);
    }

    @Override
    public int compareToSame(EntityFollowWrapper other) {
        return base.compareTo(other.base);
    }

}
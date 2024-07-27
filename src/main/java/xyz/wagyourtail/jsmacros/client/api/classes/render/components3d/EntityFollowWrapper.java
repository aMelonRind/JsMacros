package xyz.wagyourtail.jsmacros.client.api.classes.render.components3d;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import xyz.wagyourtail.doclet.DocletIgnore;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;

import java.util.Objects;

/**
 * a 3d element wrapper that follows entity
 * @author aMelonRind
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public class EntityFollowWrapper implements RenderElement3D<EntityFollowWrapper> {
    @DocletIgnore
    public static boolean dirty = false;
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
    public void render(DrawContext drawContext, float tickDelta) {
        EntityHelper<?> e = entity;
        if (shouldRemove || e == null || !e.isReallyAlive()) {
            shouldRemove = true;
            dirty = true;
            return;
        }
        MatrixStack matrices = drawContext.getMatrices();
        matrices.push();
        Vec3d pos = e.getRaw().getLerpedPos(tickDelta);
        matrices.translate(pos.x, pos.y, pos.z);
        base.render(drawContext, tickDelta);
        matrices.pop();
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

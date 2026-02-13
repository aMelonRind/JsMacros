package xyz.wagyourtail.jsmacros.client.api.classes.render.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import xyz.wagyourtail.doclet.DocletIgnore;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;

/**
 * a 2d element wrapper that converts world pos to screen pos first
 * @since 2.1.0
 */
@SuppressWarnings("unused")
public class WorldPosWrapper implements RenderElement {
    @DocletIgnore
    public static boolean dirty = false;
    @DocletIgnore
    public static Matrix4f positionMatrix = new Matrix4f();
    @DocletIgnore
    public static Matrix4f projectionMatrix = new Matrix4f();
    /**
     * the global variable for scale threshold, will affect newly created wrapper
     */
    public static double globalScaleThreshold = 4.0;
    @Nullable
    public EntityHelper<?> followedEntity = null;
    public Pos3D pos;
    public RenderElement base;
    public int zIndex;
    private int lastZIndex = 0;
    public double scaleThreshold = globalScaleThreshold;
    public boolean shouldRemove = false;

    public WorldPosWrapper(Pos3D pos, RenderElement base) {
        this(pos, base, 0);
    }

    public WorldPosWrapper(Pos3D pos, RenderElement base, int zIndex) {
        this.pos = pos;
        this.base = base;
        this.zIndex = zIndex;
    }

    /**
     * follows an entity. the pos will be treated as offset if entity is not null.
     */
    public WorldPosWrapper setFollowed(@Nullable EntityHelper<?> entity) {
        this.followedEntity = entity;
        shouldRemove = false;
        return this;
    }

    public WorldPosWrapper setPos(Pos3D pos) {
        this.pos = pos;
        return this;
    }

    public WorldPosWrapper setPos(double x, double y, double z) {
        return setPos(new Pos3D(x, y, z));
    }

    public WorldPosWrapper setBase(RenderElement element) {
        this.base = element;
        return this;
    }

    public WorldPosWrapper setScaleThreshold(double threshold) {
        this.scaleThreshold = threshold;
        return this;
    }

    @Override
    public int getZIndex() {
        return lastZIndex;
    }

    @Override
    public void render(@NonNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (mc.level == null) return;
        Vec3 dPos = pos.convert(Vec3::new).subtract(mc.gameRenderer.getMainCamera().position());
        EntityHelper<?> entity = followedEntity;
        if (entity != null) {
            if (shouldRemove || !entity.isReallyAlive()) {
                shouldRemove = true;
                dirty = true;
                return;
            }
            dPos = dPos.add(entity.getRaw().getPosition(delta));
        }

        Vector3f vec = positionMatrix.transformPosition(dPos.toVector3f());
        float z = vec.z();
        if (z > 0) return;
        vec = projectionMatrix.transformProject(vec)
                .add(1.0f, -1.0f, 0.0f)
                .mul(context.guiWidth() * 0.5f, -context.guiHeight() * 0.5f, 0);

        Matrix3x2fStack pose = context.pose();
        pose.pushMatrix();
        // does z-index not exist anymore?
        pose.translate((int) vec.x, (int) vec.y); // , z + zIndex
        lastZIndex = (int) (z * 1000) + zIndex;
        if (-z < scaleThreshold) {
            z = (float) scaleThreshold / -z;
            pose.scale(z, z);
        }
        base.render(context, mouseX, mouseY, delta);
        pose.popMatrix();
    }

}

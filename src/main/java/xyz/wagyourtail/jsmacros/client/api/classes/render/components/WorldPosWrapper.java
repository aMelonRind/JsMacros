package xyz.wagyourtail.jsmacros.client.api.classes.render.components;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import xyz.wagyourtail.doclet.DocletIgnore;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;

/**
 * a 2d element wrapper that converts world pos to screen pos first
 * @author aMelonRind
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public class WorldPosWrapper implements RenderElement {
    @DocletIgnore
    public static boolean dirty = false;
    @DocletIgnore
    public static Matrix4f positionMatrix = new Matrix4f();
    @DocletIgnore
    public static Matrix4f projectionMatrix = new Matrix4f();
    @DocletIgnore
    public static float fov90len = 540;
    /** internal static variable for passing arguments */
    @DocletIgnore
    public static Pos3D cameraPos = new Pos3D(0, 0, 0);
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
     * @param entity
     * @return
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (mc.world == null) return;
        Pos3D dPos = pos.sub(cameraPos);
        EntityHelper<?> entity = followedEntity;
        if (entity != null) {
            if (shouldRemove || !entity.isReallyAlive()) {
                shouldRemove = true;
                dirty = true;
                return;
            }
            dPos = dPos.add(new Pos3D(entity.getRaw().getLerpedPos(delta)));
        }

        Vector3f vec = positionMatrix.transformPosition(new Vector3f((float) dPos.x, (float) dPos.y, (float) dPos.z));
        float z = vec.z();
        if (z > 0) return;
        vec = projectionMatrix.transformProject(vec)
                .add(1.0f, -1.0f, 0.0f)
                .mul(context.getScaledWindowWidth() * 0.5f, -context.getScaledWindowHeight() * 0.5f, 0);

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(Math.floor(vec.x * 2) / 2, Math.floor(vec.y * 2) / 2, z + zIndex);
        lastZIndex = (int) (z * 1000) + zIndex;
        if (-z < scaleThreshold) {
            z = (float) scaleThreshold / -z;
            matrices.scale(z, z, 1);
        }
        base.render(context, mouseX, mouseY, delta);
        matrices.pop();
    }

}

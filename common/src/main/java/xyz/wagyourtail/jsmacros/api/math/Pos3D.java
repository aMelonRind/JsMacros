package xyz.wagyourtail.jsmacros.api.math;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockPosHelper;

import java.util.Objects;

/**
 * @author Wagyourtail
 * @since 1.2.6 [citation needed]
 */
@SuppressWarnings("unused")
public class Pos3D extends Pos2D {
    public static final Pos3D ZERO = new Pos3D(0, 0, 0);
    public double z;

    public Pos3D(Vec3 vec) {
        this(vec.x(), vec.y(), vec.z());
    }

    public Pos3D(double x, double y, double z) {
        super(x, y);
        this.z = z;
    }

    public double getZ() {
        return z;
    }

    @Override
    public Pos3D withX(double value) {
        return new Pos3D(value, y, z);
    }

    @Override
    public Pos3D withY(double value) {
        return new Pos3D(x, value, z);
    }

    public Pos3D withZ(double value) {
        return new Pos3D(x, y, value);
    }

    @Override
    public Pos3D add(double value) {
        return new Pos3D(x + value, y + value, z + value);
    }

    public Pos3D add(Pos3D pos) {
        return new Pos3D(x + pos.x, y + pos.y, z + pos.z);
    }

    /**
     * @since 1.6.3
     */
    public Pos3D add(double x, double y, double z) {
        return new Pos3D(this.x + x, this.y + y, this.z + z);
    }

    /**
     * @param pos the position to subtract
     * @return the new position.
     * @since 1.8.4
     */
    public Pos3D sub(Pos3D pos) {
        return new Pos3D(x - pos.x, y - pos.y, z - pos.z);
    }

    /**
     * @param x the x coordinate to subtract
     * @param y the y coordinate to subtract
     * @param z the z coordinate to subtract
     * @return the new position.
     * @since 1.8.4
     */
    public Pos3D sub(double x, double y, double z) {
        return new Pos3D(this.x - x, this.y - y, this.z - z);
    }

    @Override
    public Pos3D multiply(double multiplier) {
        return new Pos3D(x * multiplier, y * multiplier, z * multiplier);
    }

    public Pos3D multiply(Pos3D pos) {
        return new Pos3D(x * pos.x, y * pos.y, z * pos.z);
    }

    /**
     * @since 1.6.3
     */
    public Pos3D multiply(double x, double y, double z) {
        return new Pos3D(this.x * x, this.y * y, this.z * z);
    }

    /**
     * @param pos the position to divide by
     * @return the new position.
     * @since 1.8.4
     */
    public Pos3D divide(Pos3D pos) {
        return new Pos3D(x / pos.x, y / pos.y, z / pos.z);
    }

    /**
     * @param x the x coordinate to divide by
     * @param y the y coordinate to divide by
     * @param z the z coordinate to divide by
     * @return the new position.
     * @since 1.8.4
     */
    public Pos3D divide(double x, double y, double z) {
        return new Pos3D(this.x / x, this.y / y, this.z / z);
    }

    @Override
    public Pos3D modulo(double quotient) {
        return new Pos3D(x % quotient, y % quotient, z % quotient);
    }

    public Pos3D modulo(Pos3D pos) {
        return new Pos3D(x % pos.x, y % pos.y, z % pos.z);
    }

    public Pos3D modulo(double x, double y, double z) {
        return new Pos3D(this.x % x, this.y % y, this.z % z);
    }

    /**
     * @since 1.6.3
     */
    @Override
    public Pos3D scale(double scale) {
        return multiply(scale);
    }

    @Override
    public Pos3D round() {
        return new Pos3D(Math.round(x), Math.round(y), Math.round(z));
    }

    @Override
    public Pos3D ceil() {
        return new Pos3D(Math.ceil(x), Math.ceil(y), Math.ceil(z));
    }

    @Override
    public Pos3D floor() {
        return new Pos3D(Math.floor(x), Math.floor(y), Math.floor(z));
    }

    @Override
    public Pos3D abs() {
        return new Pos3D(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    @Override
    public Pos3D negate() {
        return new Pos3D(-x, -y, -z);
    }

    @Override
    public Pos3D sign() {
        return new Pos3D(Math.signum(x), Math.signum(y), Math.signum(z));
    }

    /**
     * Clamps to between 0.0 and 1.0
     */
    @Override
    public Pos3D clamp() {
        return new Pos3D(Math.clamp(x, 0.0, 1.0), Math.clamp(y, 0.0, 1.0), Math.clamp(z, 0.0, 1.0));
    }

    @Override
    public Pos3D clamp(double min, double max) {
        return new Pos3D(Math.clamp(x, min, max), Math.clamp(y, min, max), Math.clamp(z, min, max));
    }

    public Pos3D min(Pos3D other) {
        return new Pos3D(Math.min(x, other.x), Math.min(y, other.y), Math.min(z, other.z));
    }

    public Pos3D min(double x, double y, double z) {
        return new Pos3D(Math.min(this.x, x), Math.min(this.y, y), Math.min(this.z, z));
    }

    public Pos3D max(Pos3D other) {
        return new Pos3D(Math.max(x, other.x), Math.max(y, other.y), Math.max(z, other.z));
    }

    public Pos3D max(double x, double y, double z) {
        return new Pos3D(Math.max(this.x, x), Math.max(this.y, y), Math.max(this.z, z));
    }

    public Pos3D lerp(double delta, Pos3D end) {
        return new Pos3D(Mth.lerp(delta, x, end.x), Mth.lerp(delta, y, end.y), Mth.lerp(delta, z, end.z));
    }

    public Pos3D lerp(double delta, double endX, double endY, double endZ) {
        return new Pos3D(Mth.lerp(delta, x, endX), Mth.lerp(delta, y, endY), Mth.lerp(delta, z, endZ));
    }

    @Override
    public double length() {
        return Math.hypot(super.length(), z);
    }

    @Override
    public double lengthSq() {
        return x * x + y * y + z * z;
    }

    @Override
    public boolean isFinite() {
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
    }

    @Override
    public Pos3D copy() {
        return new Pos3D(x, y, z);
    }

    public String toString() {
        return String.format("%f, %f, %f", x, y, z);
    }

    @DocletReplaceReturn("[x: double, y: double, z: double]")
    public double[] toArray() {
        return new double[]{ x, y, z };
    }

    @Override
    public Vec3D toVector() {
        return new Vec3D(ZERO, this);
    }

    /**
     * @since 1.6.4
     */
    @Override
    public Vec3D toVector(Pos2D start_pos) {
        return toVector(start_pos.to3D());
    }

    /**
     * @since 1.6.4
     */
    public Vec3D toVector(Pos3D start_pos) {
        return new Vec3D(start_pos, this);
    }

    /**
     * @since 1.6.4
     */
    public Vec3D toVector(double start_x, double start_y, double start_z) {
        return new Vec3D(start_x, start_y, start_z, this.x, this.y, this.z);
    }

    /**
     * @since 1.6.4
     */
    @Override
    public Vec3D toReverseVector() {
        return new Vec3D(this, ZERO);
    }

    @Override
    public Vec3D toReverseVector(Pos2D end_pos) {
        return toReverseVector(end_pos.to3D());
    }

    /**
     * @since 1.6.4
     */
    public Vec3D toReverseVector(Pos3D end_pos) {
        return new Vec3D(this, end_pos);
    }

    /**
     * @since 1.6.4
     */
    public Vec3D toReverseVector(double end_x, double end_y, double end_z) {
        return new Vec3D(this, new Pos3D(end_x, end_y, end_z));
    }

    /**
     * @since 1.8.0
     */
    public BlockPosHelper toBlockPos() {
        return new BlockPosHelper(BlockPos.containing(x, y, z));
    }

    /**
     * @since 1.8.0
     */
    public BlockPos toRawBlockPos() {
        return new BlockPos((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
    }

    /**
     * @return the raw minecraft double vector with the same coordinates as this position.
     * @since 1.8.4
     */
    public Vec3 toMojangDoubleVector() {
        return new Vec3(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pos3D pos3D = (Pos3D) o;
        return Double.compare(x, pos3D.x) == 0 && Double.compare(y, pos3D.y) == 0 && Double.compare(z, pos3D.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), z);
    }

    public int compareTo(@NotNull Pos3D o) {
        int i = super.compareTo(o);
        if (i == 0) {
            i = Double.compare(z, o.z);
        }
        return i;
    }
}

package xyz.wagyourtail.jsmacros.api.math;

import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import xyz.wagyourtail.doclet.DocletReplaceReturn;

import java.util.Objects;

/**
 * @author Wagyourtail
 * @since 1.2.6 [citation needed]
 */
@SuppressWarnings("unused")
public class Pos2D {
    public static final Pos2D ZERO = new Pos2D(0, 0);
    public double x;
    public double y;

    public Pos2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Pos2D withX(double value) {
        return new Pos2D(value, y);
    }

    public Pos2D withY(double value) {
        return new Pos2D(x, value);
    }

    public Pos2D add(double value) {
        return new Pos2D(x + value, y + value);
    }

    public Pos2D add(Pos2D pos) {
        return new Pos2D(x + pos.x, y + pos.y);
    }

    /**
     * @since 1.6.3
     */
    public Pos2D add(double x, double y) {
        return new Pos2D(this.x + x, this.y + y);
    }

    /**
     * @param pos the position to subtract
     * @return the new position.
     * @since 1.8.4
     */
    public Pos2D sub(Pos2D pos) {
        return new Pos2D(x - pos.x, y - pos.y);
    }

    /**
     * @param x the x coordinate to subtract
     * @param y the y coordinate to subtract
     * @return the new position.
     * @since 1.8.4
     */
    public Pos2D sub(double x, double y) {
        return new Pos2D(this.x - x, this.y - y);
    }

    public Pos2D multiply(double multiplier) {
        return new Pos2D(x * multiplier, y * multiplier);
    }

    public Pos2D multiply(Pos2D pos) {
        return new Pos2D(x * pos.x, y * pos.y);
    }

    /**
     * @since 1.6.3
     */
    public Pos2D multiply(double x, double y) {
        return new Pos2D(this.x * x, this.y * y);
    }

    /**
     * @param pos the position to divide by
     * @return the new position.
     * @since 1.8.4
     */
    public Pos2D divide(Pos2D pos) {
        return new Pos2D(x / pos.x, y / pos.y);
    }

    /**
     * @param x the x coordinate to divide by
     * @param y the y coordinate to divide by
     * @return the new position.
     * @since 1.8.4
     */
    public Pos2D divide(double x, double y) {
        return new Pos2D(this.x / x, this.y / y);
    }

    public Pos2D modulo(double quotient) {
        return new Pos2D(x % quotient, y % quotient);
    }

    public Pos2D modulo(Pos2D pos) {
        return new Pos2D(x % pos.x, y % pos.y);
    }

    public Pos2D modulo(double x, double y) {
        return new Pos2D(this.x % x, this.y % y);
    }

    /**
     * @since 1.6.3
     */
    public Pos2D scale(double scale) {
        return new Pos2D(x * scale, y * scale);
    }

    public Pos2D round() {
        return new Pos2D(Math.round(x), Math.round(y));
    }

    public Pos2D ceil() {
        return new Pos2D(Math.ceil(x), Math.ceil(y));
    }

    public Pos2D floor() {
        return new Pos2D(Math.floor(x), Math.floor(y));
    }

    public Pos2D abs() {
        return new Pos2D(Math.abs(x), Math.abs(y));
    }

    public Pos2D negate() {
        return new Pos2D(-x, -y);
    }

    public Pos2D sign() {
        return new Pos2D(Math.signum(x), Math.signum(y));
    }

    /**
     * Clamps to between 0.0 and 1.0
     */
    public Pos2D clamp() {
        return new Pos2D(Math.clamp(x, 0.0, 1.0), Math.clamp(y, 0.0, 1.0));
    }

    public Pos2D clamp(double min, double max) {
        return new Pos2D(Math.clamp(x, min, max), Math.clamp(y, min, max));
    }

    public Pos2D min(Pos2D other) {
        return new Pos2D(Math.min(x, other.x), Math.min(y, other.y));
    }

    public Pos2D min(double x, double y) {
        return new Pos2D(Math.min(this.x, x), Math.min(this.y, y));
    }

    public Pos2D max(Pos2D other) {
        return new Pos2D(Math.max(x, other.x), Math.max(y, other.y));
    }

    public Pos2D max(double x, double y) {
        return new Pos2D(Math.max(this.x, x), Math.max(this.y, y));
    }

    public Pos2D lerp(double delta, Pos2D end) {
        return new Pos2D(Mth.lerp(delta, x, end.x), Mth.lerp(delta, y, end.y));
    }

    public Pos2D lerp(double delta, double endX, double endY) {
        return new Pos2D(Mth.lerp(delta, x, endX), Mth.lerp(delta, y, endY));
    }

    public double length() {
        return Math.hypot(x, y);
    }

    public double lengthSq() {
        return x * x + y * y;
    }

    public boolean isFinite() {
        return Double.isFinite(x) && Double.isFinite(y);
    }

    public Pos2D copy() {
        return new Pos2D(x, y);
    }

    public String toString() {
        return String.format("%f, %f", x, y);
    }

    @DocletReplaceReturn("[x: double, y: double]")
    public double[] toArray() {
        return new double[]{ x, y };
    }

    public Pos3D to3D() {
        return new Pos3D(x, y, 0);
    }

    public Vec2D toVector() {
        return new Vec2D(ZERO, this);
    }

    /**
     * @since 1.6.4
     */
    public Vec2D toVector(Pos2D start_pos) {
        return new Vec2D(start_pos, this);
    }

    /**
     * @since 1.6.4
     */
    public Vec2D toVector(double start_x, double start_y) {
        return new Vec2D(start_x, start_y, this.x, this.y);
    }

    /**
     * @since 1.6.4
     */
    public Vec2D toReverseVector() {
        return new Vec2D(this, ZERO);
    }

    /**
     * @since 1.6.4
     */
    public Vec2D toReverseVector(Pos2D end_pos) {
        return new Vec2D(this, end_pos);
    }

    /**
     * @since 1.6.4
     */
    public Vec2D toReverseVector(double end_x, double end_y) {
        return new Vec2D(this, new Pos2D(end_x, end_y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pos2D pos2D = (Pos2D) o;
        return Double.compare(x, pos2D.x) == 0 && Double.compare(y, pos2D.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int compareTo(@NotNull Pos2D o) {
        int i = Double.compare(x, o.x);
        if (i == 0) {
            i = Double.compare(y, o.y);
        }
        return i;
    }
}

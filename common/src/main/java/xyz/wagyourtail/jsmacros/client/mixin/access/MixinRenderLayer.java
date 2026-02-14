package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderType.class)
public interface MixinRenderLayer {
    @Invoker
    static RenderType invokeCreate(String name, RenderSetup setup) {
        throw new UnsupportedOperationException();
    }
}

package xyz.wagyourtail.jsmacros.client.mixin.access;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.classes.render.components3d.JsmRenderLayers;

@Mixin(RenderPipelines.class)
public class MixinRenderPipelines {
    @Shadow
    @Final
    private static RenderPipeline.Snippet MATRICES_PROJECTION_SNIPPET;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onRegisterPipelines(CallbackInfo ci) {
        JsmRenderLayers.init(MATRICES_PROJECTION_SNIPPET);
    }
}

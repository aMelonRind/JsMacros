package xyz.wagyourtail.jsmacros.client.api.classes.render.components3d;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinRenderLayer;

@SuppressWarnings("FieldCanBeLocal")
public class JsmRenderLayers {
    private static RenderPipeline LINES;
    private static RenderPipeline LINES_NO_CULL;
    private static RenderPipeline TRIANGLES;
    private static RenderPipeline TRIANGLES_NO_CULL;

    private static RenderType LINES_LAYER;
    private static RenderType LINES_NO_CULL_LAYER;
    private static RenderType TRIANGLES_LAYER;
    private static RenderType TRIANGLES_NO_CULL_LAYER;

    public static RenderType lines(boolean cull) {
        return cull ? LINES_LAYER : LINES_NO_CULL_LAYER;
    }

    public static RenderType triangles(boolean cull) {
        return cull ? TRIANGLES_LAYER : TRIANGLES_NO_CULL_LAYER;
    }

    public static void init(RenderPipeline.Snippet matricesProjectionSnippet) {
        RenderPipeline.Snippet snippet = RenderPipeline.builder(matricesProjectionSnippet)
                .withFragmentShader(getId("position_color"))
                .withBlend(BlendFunction.TRANSLUCENT)
                .withDepthWrite(true)
                .withCull(false)
                .buildSnippet();

        RenderPipeline.Snippet lineSnippet = RenderPipeline.builder(snippet)
                .withVertexShader(getId("position_color_lines"))
                .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_LINE_WIDTH, VertexFormat.Mode.DEBUG_LINE_STRIP)
                .buildSnippet();
        LINES = RenderPipeline.builder(lineSnippet)
                .withLocation(getId("pipeline/lines"))
                .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                .withDepthBias(1.0f, -0.1f)
                .build();
        // note that cull in jsm somehow means depth test
        LINES_NO_CULL = RenderPipeline.builder(lineSnippet)
                .withLocation(getId("pipeline/lines_no_cull"))
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .build();

        RenderPipeline.Snippet quadSnippet = RenderPipeline.builder(snippet)
                .withVertexShader(getId("position_color"))
                .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
                .buildSnippet();
        TRIANGLES = RenderPipeline.builder(quadSnippet)
                .withLocation(getId("pipeline/triangles"))
                .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                .withDepthBias(1.0f, 0.1f)
                .build();
        TRIANGLES_NO_CULL = RenderPipeline.builder(quadSnippet)
                .withLocation(getId("pipeline/triangles_no_cull"))
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .build();

        LINES_LAYER = createSimpleLayer("jsmacros:lines", LINES);
        LINES_NO_CULL_LAYER = createSimpleLayer("jsmacros:lines_no_cull", LINES_NO_CULL);
        TRIANGLES_LAYER = createSimpleLayer("jsmacros:triangles", TRIANGLES);
        TRIANGLES_NO_CULL_LAYER = createSimpleLayer("jsmacros:triangles_no_cull", TRIANGLES_NO_CULL);
    }

    private static RenderType createSimpleLayer(String name, RenderPipeline pipeline) {
        // i'm not 100% sure if using private method won't cause any consequences.
        // looks like it won't tho, except future maintaining cost if they decided to change something again.
        return MixinRenderLayer.invokeCreate(name, RenderSetup.builder(pipeline).sortOnUpload().createRenderSetup());
    }

    private static Identifier getId(String id) {
        return Identifier.fromNamespaceAndPath(JsMacros.MOD_ID, id);
    }
}

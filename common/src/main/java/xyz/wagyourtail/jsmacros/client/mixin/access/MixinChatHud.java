package xyz.wagyourtail.jsmacros.client.mixin.access;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;

import java.util.List;

@Mixin(ChatComponent.class)
public abstract class MixinChatHud implements IChatHud {

    @Shadow
    private void addMessage(Component message, @Nullable MessageSignature signature, @Nullable GuiMessageTag indicator) {
    }

    @Shadow
    @Final
    private List<GuiMessage> allMessages;

    @Override
    public void jsmacros_addMessageBypass(Component message) {
        if (RenderSystem.isOnRenderThread()) {
            addMessage(message, null, GuiMessageTag.system());
        } else {
            Minecraft.getInstance().execute(() -> {
                addMessage(message, null, GuiMessageTag.system());
            });
        }
    }

    @Unique
    ThreadLocal<Integer> jsmacros$positionOverride = ThreadLocal.withInitial(() -> 0);

    @Override
    public void jsmacros_addMessageAtIndexBypass(Component message, int index, int time) {
        jsmacros$positionOverride.set(index);
        addMessage(message, null, GuiMessageTag.system());
        jsmacros$positionOverride.set(0);
    }

    // TODO: (1.21.11) addMessageToQueue used to call .add(0, msg) but now uses addFirst so we reimplement the method
    //  here instead. But this doesn't make much sense to me? JSM can force all messages in the chat to be at a specific
    //  position? I can't think of a use for that...
    //
    // it could attach additional info next to the associated message.
    @Redirect(method = "addMessageToQueue(Lnet/minecraft/client/GuiMessage;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;addFirst(Ljava/lang/Object;)V"))
    public <E> void overrideMessagePos(List<GuiMessage> instance, E guiMessage) {
        this.allMessages.add(jsmacros$positionOverride.get(), (GuiMessage) guiMessage);
    }


}

package xyz.wagyourtail.jsmacros.client.api.helper;

import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;

import static xyz.wagyourtail.jsmacros.client.McUtil.mc;

/**
 * @since 1.9.1
 */
public class BlockPredicateHelper extends BaseHelper<BlockPredicate> {

    public BlockPredicateHelper(BlockPredicate base) {
        super(base);
    }

    /**
     * @since 1.9.1
     */
    @Nullable
    public List<BlockHelper> getBlocks() {
        if (base.blocks().isEmpty()) return null;
        return base.blocks().get().stream().map(RegistryEntry::value).map(BlockHelper::new).toList();
    }

    /**
     * @since 1.9.1
     */
    @Nullable
    public StatePredicateHelper getStatePredicate() {
        if (base.state().isEmpty()) return null;
        return new StatePredicateHelper(base.state().get());
    }

    /**
     * @since 1.9.1
     */
    @Nullable
    public NbtPredicateHelper getNbtPredicate() {
        if (base.nbt().isEmpty()) return null;
        return new NbtPredicateHelper(base.nbt().get());
    }

    /**
     * @since 1.9.1
     *
     * @param state
     * @return
     */
    public boolean test(BlockPosHelper state) {
        return base.test(new CachedBlockPosition(mc.world, state.getRaw(), true));
    }

}

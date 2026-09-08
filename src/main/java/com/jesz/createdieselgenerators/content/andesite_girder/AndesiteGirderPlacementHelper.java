package com.jesz.createdieselgenerators.content.andesite_girder;

import com.google.common.base.Predicates;
import com.jesz.createdieselgenerators.CDGBlocks;
import com.zurrtum.create.content.decoration.girder.GirderPlacementHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class AndesiteGirderPlacementHelper extends GirderPlacementHelper {
    @Override
    public Predicate<BlockState> getStatePredicate() {
        return Predicates.or(cdgState -> CDGBlocks.ANDESITE_GIRDER.has(cdgState), cdgState -> CDGBlocks.ANDESITE_GIRDER_ENCASED_SHAFT.has(cdgState));
    }

    @Override
    public Predicate<ItemStack> getItemPredicate() {
        return cdgStack -> CDGBlocks.ANDESITE_GIRDER.isIn(cdgStack);
    }
}

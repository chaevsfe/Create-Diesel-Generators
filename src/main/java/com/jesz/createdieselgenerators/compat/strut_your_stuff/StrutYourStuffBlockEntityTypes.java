package com.jesz.createdieselgenerators.compat.strut_your_stuff;

import com.cake.struts.content.block.StrutBlockEntity;
import com.cake.struts.content.block.StrutBlockEntityRenderer;
import com.jesz.createdieselgenerators.registrate.entry.BlockEntityEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static com.jesz.createdieselgenerators.CreateDieselGenerators.REGISTRATE;

public class StrutYourStuffBlockEntityTypes {
    public static final BlockEntityEntry<StrutBlockEntity> ANDESITE_GIRDER_STRUT = REGISTRATE
            .blockEntity("andesite_girder_strut", StrutYourStuffBlockEntityTypes::create)
            .validBlocks(StrutYourStuffRegistryEntries.ANDESITE_GIRDER_STRUT)
            .renderer(() -> StrutBlockEntityRenderer::new)
            .register();

    @SuppressWarnings("unchecked")
    private static StrutBlockEntity create(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        return new StrutBlockEntity((BlockEntityType<? extends StrutBlockEntity>) type, pos, state);
    }

    public static void register() {
    }
}

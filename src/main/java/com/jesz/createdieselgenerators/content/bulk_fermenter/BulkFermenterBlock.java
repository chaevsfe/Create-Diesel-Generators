package com.jesz.createdieselgenerators.content.bulk_fermenter;

import com.jesz.createdieselgenerators.CDGBlockEntityTypes;
import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.Container;
import com.zurrtum.create.infrastructure.items.ItemInventoryProvider;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;
import com.zurrtum.create.infrastructure.fluids.FluidInventoryProvider;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.EntityType;

public class BulkFermenterBlock extends Block implements IBE<BulkFermenterBlockEntity>, IWrenchable,
        ItemInventoryProvider<BulkFermenterBlockEntity>, FluidInventoryProvider<BulkFermenterBlockEntity> {

    @Override
    public Container getInventory(LevelAccessor world, BlockPos pos, BlockState state, BulkFermenterBlockEntity blockEntity, Direction context) {
        blockEntity.initCapability();
        return blockEntity.itemCapability;
    }

    @Override
    public FluidInventory getFluidInventory(LevelAccessor world, BlockPos pos, BlockState state, BulkFermenterBlockEntity blockEntity, Direction context) {
        if (blockEntity.fluidCapability == null)
            blockEntity.refreshCapability();
        return blockEntity.fluidCapability;
    }

    public BulkFermenterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess cdgTickAccess, BlockPos pos, Direction direction, BlockPos neighbourPos, BlockState neighbourState, RandomSource cdgRandom) {
        if (direction == Direction.DOWN && neighbourState.getBlock() != this)
            withBlockEntityDo(level, pos, BulkFermenterBlockEntity::updateHeat);
        return super.updateShape(state, level, cdgTickAccess, pos, direction, neighbourPos, neighbourState, cdgRandom);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (moved)
            return;
        withBlockEntityDo(world, pos, BulkFermenterBlockEntity::updateConnectivity);
        withBlockEntityDo(world, pos, BulkFermenterBlockEntity::updateHeat);
    }
    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean movedByPiston) {
        if (!state.hasBlockEntity())
            return;
        if (!(world.getBlockEntity(pos) instanceof BulkFermenterBlockEntity tankBE))
            return;
        Containers.dropContents(world, pos, tankBE.inventory);
        world.removeBlockEntity(pos);
        ConnectivityHandler.splitMulti(tankBE);
    }
    @Override
    public Class<BulkFermenterBlockEntity> getBlockEntityClass() {
        return BulkFermenterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BulkFermenterBlockEntity> getBlockEntityType() {
        return CDGBlockEntityTypes.BULK_FERMENTER.get();
    }

}

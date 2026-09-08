package com.jesz.createdieselgenerators.content.pumpjack;

import com.jesz.createdieselgenerators.CDGBlockEntityTypes;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;
import com.zurrtum.create.infrastructure.fluids.FluidInventoryProvider;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityTypes;

public class PumpjackHoleBlock extends Block implements IBE<PumpjackHoleBlockEntity>, IWrenchable, FluidInventoryProvider<PumpjackHoleBlockEntity> {

    @Override
    public FluidInventory getFluidInventory(LevelAccessor world, BlockPos pos, BlockState state, PumpjackHoleBlockEntity blockEntity, Direction side) {
        if (side == null || (side.getAxis().isHorizontal() && state.getValue(
                side == Direction.NORTH ? NORTH :
                side == Direction.EAST ? EAST :
                side == Direction.WEST ? WEST : SOUTH)))
            return blockEntity.tank.getCapability();
        return null;
    }

    public PumpjackHoleBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(NORTH, true)
                .setValue(SOUTH, true)
                .setValue(WEST, true)
                .setValue(EAST, true));
    }
    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if(context.getClickedFace().getAxis().isHorizontal()){
            Direction side = context.getClickedFace();
            context.getLevel().setBlock(context.getClickedPos(), state.cycle(
                    side == Direction.NORTH ? NORTH :
                    side == Direction.EAST ? EAST :
                    side == Direction.WEST ? WEST : SOUTH
            ), 3);
            IWrenchable.playRotateSound(context.getLevel(), context.getClickedPos());

            return InteractionResult.SUCCESS;
        }
        return IWrenchable.super.onWrenched(state, context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public Class<PumpjackHoleBlockEntity> getBlockEntityClass() {
        return PumpjackHoleBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PumpjackHoleBlockEntity> getBlockEntityType() {
        return CDGBlockEntityTypes.PUMPJACK_HOLE.get();
    }
}

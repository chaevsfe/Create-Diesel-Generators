package com.jesz.createdieselgenerators.content.fluids;

import com.zurrtum.create.infrastructure.fluids.FlowableFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public abstract class CDGFluid extends FlowableFluid {
    protected final CDGFluidHolder holder;

    protected CDGFluid(CDGFluidHolder holder) {
        this.holder = holder;
    }

    public CDGFluidHolder getHolder() {
        return holder;
    }

    @Override
    public Fluid getFlowing() {
        return holder.flowing;
    }

    @Override
    public Fluid getSource() {
        return holder.still;
    }

    @Override
    public Item getBucket() {
        return holder.bucket == null ? Items.BUCKET : holder.bucket;
    }

    @Override
    public BlockState createLegacyBlock(FluidState state) {
        if (holder.block == null)
            return Blocks.AIR.defaultBlockState();
        return holder.block.defaultBlockState().setValue(net.minecraft.world.level.block.LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == holder.still || fluid == holder.flowing;
    }

    @Override
    protected boolean canConvertToSource(ServerLevel level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        net.minecraft.world.level.block.entity.BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        net.minecraft.world.level.block.Block.dropResources(state, level, pos, blockEntity);
    }

    @Override
    public int getSlopeFindDistance(LevelReader level) {
        return holder.properties.slopeFindDistance;
    }

    @Override
    public int getDropOff(LevelReader level) {
        return holder.properties.levelDecreasePerBlock;
    }

    @Override
    public int getTickDelay(net.minecraft.world.level.LevelReader level) {
        return holder.properties.tickRate;
    }

    @Override
    protected float getExplosionResistance() {
        return holder.properties.explosionResistance;
    }

    public static class Still extends CDGFluid {
        public Still(CDGFluidHolder holder) {
            super(holder);
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends CDGFluid {
        public Flowing(CDGFluidHolder holder) {
            super(holder);
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }
}

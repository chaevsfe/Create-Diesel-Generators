package com.jesz.createdieselgenerators.content.fluids;

import com.zurrtum.create.infrastructure.fluids.FlowableFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class CDGFluid {
    private CDGFluid() {
    }

    static void dropBlockContents(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockEntity);
    }

    public static class Still extends FlowableFluid.Still {
        protected final CDGFluidHolder holder;

        public Still(CDGFluidHolder holder) {
            super(holder);
            this.holder = holder;
        }

        public CDGFluidHolder getHolder() {
            return holder;
        }

        @Override
        protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
            dropBlockContents(level, pos, state);
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
        public int getTickDelay(LevelReader level) {
            return holder.properties.tickRate;
        }

        @Override
        protected float getExplosionResistance() {
            return holder.properties.explosionResistance;
        }
    }

    public static class Flowing extends FlowableFluid.Flowing {
        protected final CDGFluidHolder holder;

        public Flowing(CDGFluidHolder holder) {
            super(holder);
            this.holder = holder;
        }

        public CDGFluidHolder getHolder() {
            return holder;
        }

        @Override
        protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
            dropBlockContents(level, pos, state);
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
        public int getTickDelay(LevelReader level) {
            return holder.properties.tickRate;
        }

        @Override
        protected float getExplosionResistance() {
            return holder.properties.explosionResistance;
        }
    }
}

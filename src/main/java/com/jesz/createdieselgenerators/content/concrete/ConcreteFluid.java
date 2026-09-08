package com.jesz.createdieselgenerators.content.concrete;

import com.jesz.createdieselgenerators.CDGFluids;
import com.jesz.createdieselgenerators.registrate.entry.FluidEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import com.jesz.createdieselgenerators.content.fluids.CDGFluid;
import com.jesz.createdieselgenerators.content.fluids.CDGFluidHolder;

import java.util.Map;
import net.minecraft.server.level.ServerLevel;

public class ConcreteFluid extends CDGFluid.Still {
    DyeColor color;
    public ConcreteFluid(CDGFluidHolder holder, DyeColor color) {
        super(holder);
        this.color = color;
    }

    @Override
    public void tick(ServerLevel level, BlockPos pos, BlockState blockState, FluidState state) {
        if (level.getBlockState(pos.below()).isAir()) {
            BlockState blockstate = state.createLegacyBlock();
            level.setBlockAndUpdate(pos.below(), blockstate);
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        } else
            super.tick(level, pos, blockState, state);

    }

    @Override
    public void randomTick(ServerLevel level, BlockPos pos, FluidState state, RandomSource random) {
        if (random.nextInt(30) == 0) {
            level.setBlockAndUpdate(pos, BuiltInRegistries.BLOCK.getValue(Identifier.withDefaultNamespace(color.getName() + "_concrete")).defaultBlockState());
        }
        super.randomTick(level, pos, state, random);
    }

    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }
}

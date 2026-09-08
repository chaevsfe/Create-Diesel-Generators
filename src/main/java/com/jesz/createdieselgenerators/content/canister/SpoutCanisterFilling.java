package com.jesz.createdieselgenerators.content.canister;

import com.jesz.createdieselgenerators.CDGConfig;
import com.zurrtum.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.zurrtum.create.content.fluids.spout.SpoutBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;
import com.jesz.createdieselgenerators.foundation.CDGInv;

public class SpoutCanisterFilling implements BlockSpoutingBehaviour {
    @Override
    public int fillBlock(Level level, BlockPos pos, SpoutBlockEntity spout, FluidStack availableFluid, boolean simulate) {
        if (!CDGConfig.server().CANISTER_SPOUT_FILLING.get())
            return 0;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CanisterBlockEntity){
            FluidInventory handler = ((CanisterBlockEntity) blockEntity).tank.getCapability();
            if(FluidStack.areFluidsAndComponentsEqualIgnoreCapacity(handler.getStack(0), availableFluid) || handler.getStack(0).isEmpty())
                return CDGInv.fill(handler, availableFluid, simulate);
        }
        return 0;
    }
}

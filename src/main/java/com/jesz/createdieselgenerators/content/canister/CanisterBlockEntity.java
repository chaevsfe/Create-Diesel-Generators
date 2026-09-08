package com.jesz.createdieselgenerators.content.canister;

import com.jesz.createdieselgenerators.CDGBlockEntityTypes;
import com.jesz.createdieselgenerators.CDGConfig;
import com.jesz.createdieselgenerators.CDGDataComponents;
import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.zurrtum.create.catnip.codecs.CatnipCodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import com.zurrtum.create.infrastructure.fluids.FluidStack;

import java.util.List;
import java.util.Optional;
import com.jesz.createdieselgenerators.foundation.CDGInv;
import com.jesz.createdieselgenerators.content.fluids.SimpleFluidContent;
import com.jesz.createdieselgenerators.mixins.TankSegmentAccessor;
import com.jesz.createdieselgenerators.CDGFluids;

public class CanisterBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    CapacityEnchantedFluidTankBehaviour tank;
    BlockState state;

    public int capacityEnchantLevel;

    private DataComponentPatch componentPatch = DataComponentPatch.EMPTY;

    public CanisterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.state = state;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        return containedFluidTooltip(tooltip, isPlayerSneaking, CDGInv.fluidsAt(level, worldPosition));
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        tank = CapacityEnchantedFluidTankBehaviour.single(this, CDGFluids.mb(Math.abs(CDGConfig.common().CANISTER_CAPACITY.get())), CDGFluids.mb(CDGConfig.common().CANISTER_CAPACITY_ENCHANTMENT.get()));
        behaviours.add(tank);
    }

    @Override
    protected void write(ValueOutput compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("CapacityEnchantment", capacityEnchantLevel);
        compound.store("Components", DataComponentPatch.CODEC, componentPatch);
    }

    @Override
    protected void read(ValueInput compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        capacityEnchantLevel = compound.getIntOr("CapacityEnchantment", 0);
        componentPatch = compound.read("Components", DataComponentPatch.CODEC).orElse(DataComponentPatch.EMPTY);
    }

    public void setCapacityEnchantLevel(int capacityEnchantLevel) {
        this.capacityEnchantLevel = capacityEnchantLevel;
        ((TankSegmentAccessor) tank.getPrimaryHandler()).createdieselgenerators$setCapacity(tank.baseCapacity + tank.capacityAddition * capacityEnchantLevel);
    }

    public void setComponentPatch(DataComponentPatch componentPatch) {
        this.componentPatch = componentPatch;
        SimpleFluidContent content = componentPatch.get(null, CDGDataComponents.FLUID_CONTENTS);
        if (content == null || content.isEmpty())
            return;

        this.tank.getPrimaryHandler().setFluid(content.copy());
    }

    public DataComponentPatch getComponentPatch() {
        return componentPatch;
    }

    public static class CapacityEnchantedFluidTankBehaviour extends SmartFluidTankBehaviour {

        int capacityAddition;
        int baseCapacity;

        public CapacityEnchantedFluidTankBehaviour(BehaviourType<SmartFluidTankBehaviour> type, SmartBlockEntity be, int tanks, int tankCapacity, boolean enforceVariety, int capacityAddition) {
            super(type, be, tanks, tankCapacity, enforceVariety);
            this.capacityAddition = capacityAddition;
            this.baseCapacity = tankCapacity;
        }

        public static CapacityEnchantedFluidTankBehaviour single(SmartBlockEntity be, int capacity, int capacityAddition) {
            return new CapacityEnchantedFluidTankBehaviour(TYPE, be, 1, capacity, false, capacityAddition);
        }

        @Override
        public void read(ValueInput compound, boolean clientPacket) {
            super.read(compound, clientPacket);
            compound.getInt("CapacityEnchantment").ifPresent(level ->
                    ((TankSegmentAccessor) getPrimaryHandler()).createdieselgenerators$setCapacity(baseCapacity + level * capacityAddition));
        }

    }
}

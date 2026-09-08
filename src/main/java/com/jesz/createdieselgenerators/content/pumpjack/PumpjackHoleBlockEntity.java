package com.jesz.createdieselgenerators.content.pumpjack;

import com.jesz.createdieselgenerators.CDGBlockEntityTypes;
import com.jesz.createdieselgenerators.CDGTags;
import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.jesz.createdieselgenerators.content.concrete.ConcreteEncasedFluidPipeBlock;
import com.jesz.createdieselgenerators.world.OilChunksSavedData;
import com.mojang.datafixers.util.Pair;
import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.client.api.goggles.IHaveHoveringInformation;
import com.zurrtum.create.client.content.fluids.FluidFX;
import com.zurrtum.create.content.fluids.pipes.EncasedPipeBlock;
import com.zurrtum.create.content.fluids.pipes.GlassFluidPipeBlock;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.zurrtum.create.client.foundation.item.TooltipHelper;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.client.catnip.lang.FontHelper;
import com.zurrtum.create.client.catnip.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;
import com.jesz.createdieselgenerators.foundation.CDGInv;
import com.jesz.createdieselgenerators.CDGFluids;

public class PumpjackHoleBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation {
    BlockState state;

    SmartFluidTankBehaviour tank;
    public int headPos = 0;
    public int bearingPos = 0;
    public boolean started = false;
    public PumpjackHoleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.state = state;
    }


    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("OilAmount", oilAmount);
        tag.putBoolean("Started", started);
    }

    public int oilAmount = 0;

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (valid)
            return false;

        Lang.builder(CreateDieselGenerators.ID).translate("hint.pumpjack_hole_no_pipe.title").style(ChatFormatting.GOLD).forGoggles(tooltip);
        Component hint = Lang.builder(CreateDieselGenerators.ID).translate("hint.pumpjack_hole_no_pipe").component();
        List<Component> cutComponent = TooltipHelper.cutTextComponent(hint, FontHelper.Palette.GRAY_AND_WHITE);
        for (Component component : cutComponent)
            CreateLang.builder().add(component).forGoggles(tooltip);
        return true;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!valid || !started)
            return false;
        if (oilAmount == Integer.MAX_VALUE) {
            TooltipHelper.addHint(tooltip, "hint.hose_pulley");
            return true;
        }

        CreateLang.builder().add(Component.translatable("createdieselgenerators.goggle.oil_amount")).style(ChatFormatting.GRAY).forGoggles(tooltip);
        CreateLang.text(String.format("%,d", oilAmount)).add(CreateLang.translate("generic.unit.millibuckets")).style(ChatFormatting.GOLD).forGoggles(tooltip);

        return true;
    }

    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        oilAmount = tag.getIntOr("OilAmount", 0);
        started = tag.getBooleanOr("Started", false);
    }

    byte tick = 0;
    public int pipeLength = 0;
    boolean valid = false;
    @Override
    public void tick() {
        super.tick();
        if (level != null && level.isClientSide())
            com.jesz.createdieselgenerators.client.sound.CDGSounds.tickHole(this, started && valid && oilAmount > 0);
        tick++;
        if (tick >= 20) {
            int pipeLength = 0;
            tick = 0;
            boolean valid = false;
            for (int i = 0; i < getBlockPos().getY() - level.getMinY(); i++) {
                pipeLength++;
                BlockState bs = level.getBlockState(getBlockPos().below(i + 1));
                if (bs.getBlock() instanceof PipeBlock || bs.getBlock() instanceof EncasedPipeBlock || bs.getBlock() instanceof ConcreteEncasedFluidPipeBlock) {
                    if (!(bs.getValue(BlockStateProperties.UP) && bs.getValue(BlockStateProperties.DOWN)))
                        break;
                } else if(bs.getBlock() instanceof GlassFluidPipeBlock) {
                    if (!(bs.getValue(AXIS) == Direction.Axis.Y))
                        break;
                } else if (bs.is(CDGTags.PUMPJACK_PIPE)){
                    continue;
                } else if (bs.is(CDGTags.OIL_DEPOSIT)) {
                    valid = true;
                    break;
                } else
                    break;
            }
            if (valid)
                this.pipeLength = pipeLength;
            else
                this.pipeLength = 0;
            this.valid = valid;
        }
    }



    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(pipeLength);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, CDGFluids.mb(8000));
        behaviours.add(tank);
    }

    public void pumpjackRotation(boolean isCrankLarge) {

        List<Fluid> stackList = new ArrayList<>();
        BuiltInRegistries.FLUID.getTags()
                .filter(named -> named.key().equals(CDGTags.PUMPJACK_OUTPUT))
                .forEach(named -> named.stream().map(Holder::value).forEach(stackList::add));

        if (stackList.isEmpty())
            return;

        if (!level.isClientSide() && valid) {
            ChunkPos chunkPos = ChunkPos.containing(getBlockPos());
            oilAmount = OilChunksSavedData.getChunkOilAmount((ServerLevel) level, chunkPos);
            started = true;

            int subtractedAmount = Mth.clamp((int) (1000 * Math.abs((float) headPos / (float) bearingPos)) * (isCrankLarge ? 2 : 1), 0, oilAmount);

            FluidStack oilStack = new FluidStack(stackList.get(0), CDGFluids.mb(subtractedAmount));

            subtractedAmount = CDGInv.fill(tank.getPrimaryHandler(), oilStack, false) / CDGFluids.MB;

            if (oilAmount == Integer.MAX_VALUE) {
                OilChunksSavedData.setChunkOilAmount((ServerLevel) level, chunkPos, Integer.MAX_VALUE);
                return;
            }

            oilAmount -= subtractedAmount;
            OilChunksSavedData.setChunkOilAmount((ServerLevel) level, chunkPos, oilAmount);
        }

        if (level.isClientSide() && oilAmount > 0)
            FluidFX.spawnPouringLiquid(level, worldPosition, 20, FluidFX.getFluidParticle(new FluidStack(stackList.get(0), 1000)), 0.3f, new Vec3(0.1, 1, 0.1), true);
    }
}

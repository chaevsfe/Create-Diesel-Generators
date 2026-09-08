package com.jesz.createdieselgenerators.content.pumpjack;

import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.behaviour.scrollValue.ServerScrollOptionBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.ref.WeakReference;
import java.util.List;

public class PumpjackCrankBlockEntity extends KineticBlockEntity {
    public float angle = 0;
    public float prevAngle = 0;
    public float bearingAngle;
    public float prevBearingAngle;
    public BlockPos bearingPos;
    public WeakReference<PumpjackBearingBlockEntity> bearing = new WeakReference<>(null);
    public float inPonderAngle = Integer.MIN_VALUE;
    private float lastSoundAngle = 0;
    public PumpjackCrankBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    protected void read(ValueInput compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        angle = compound.getFloatOr("Angle", 0f);
        crankBearingLocation = new Vec3(
                compound.getDoubleOr("BackPosX", 0d),
                compound.getDoubleOr("BackPosY", 0d),
                compound.getDoubleOr("BackPosZ", 0d));
    }

    @Override
    protected void write(ValueOutput compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("Angle", angle);
        compound.putDouble("BackPosX", crankBearingLocation.x);
        compound.putDouble("BackPosY", crankBearingLocation.y);
        compound.putDouble("BackPosZ", crankBearingLocation.z);
    }

    @Override
    public float calculateStressApplied() {
        float impact = 16f;
        this.lastStressApplied = impact;
        return impact;
    }

    public PumpjackBearingBlockEntity getBearing(){
        if(bearing.get() != null){
            if(bearing.get().isRemoved() || !bearing.get().isRunning()) {
                bearing = new WeakReference<>(null);
                return null;
            }
            return bearing.get();
        }
        return null;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(3);
    }

    @Override
    public void tick() {
        super.tick();
        if (level != null && level.isClientSide())
            com.jesz.createdieselgenerators.client.sound.CDGSounds.tickCrank(this);
        PumpjackBearingBlockEntity bearing = getBearing();
        if(bearing != null){
            if(bearing.isStalled())
                return;
        }
        prevAngle = angle;
        if(angle >= 359 || angle <= -359)
            angle = 0;
        if(getSpeed() != 0)
            angle += Mth.clamp(Math.abs(getSpeed()), 0, 64) / 10;
    }
    public Vec3 crankBearingLocation = new Vec3(0, -100, 0);

    public ServerScrollOptionBehaviour<CrankSize> crankSize;
    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {
        crankSize = new ServerScrollOptionBehaviour<>(CrankSize.class, this);
        crankSize.withCallback($ -> onSizeChanged());
        behaviours.add(crankSize);
        super.addBehaviours(behaviours);
    }

    private void onSizeChanged() {

    }
    public enum CrankSize {
        NORMAL, LARGE;

        public String getTranslationKey() {
            return "createdieselgenerators.tooltip.crank." + name().toLowerCase(java.util.Locale.ROOT);
        }
    }

}

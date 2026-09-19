package com.jesz.createdieselgenerators.content.tools;

import com.jesz.createdieselgenerators.CDGEntityTypes;
import com.jesz.createdieselgenerators.CDGRegistries;
import com.jesz.createdieselgenerators.fuel_type.FuelType;
import com.zurrtum.create.AllFluidTags;
import com.zurrtum.create.AllFluids;
import com.zurrtum.create.client.content.fluids.FluidFX;
import com.zurrtum.create.foundation.fluid.FluidHelper;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class ChemicalSprayerProjectileEntity extends AbstractHurtingProjectile {
    public FluidStack stack = FluidStack.EMPTY;
    public boolean fire;
    public boolean cooling;

    public ChemicalSprayerProjectileEntity(EntityType<? extends AbstractHurtingProjectile> type, Level level) {
        super(type, level);
    }

    int t = 0;

    public static ChemicalSprayerProjectileEntity spray(Level level, FluidStack stack, boolean fire, boolean cooling) {
        ChemicalSprayerProjectileEntity projectile = new ChemicalSprayerProjectileEntity(CDGEntityTypes.CHEMICAL_SPRAYER_PROJECTILE.get(), level);
        projectile.stack = stack;
        projectile.fire = fire;
        projectile.cooling = cooling;
        projectile.syncData();
        return projectile;
    }

    private void syncData() {
        getEntityData().set(DATA_FLUID, stack.isEmpty() ? "" : BuiltInRegistries.FLUID.getKey(stack.getFluid()).toString());
        getEntityData().set(DATA_FIRE, fire);
        getEntityData().set(DATA_COOLING, cooling);
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        Entity owner = getOwner();
        Entity target = hit.getEntity();

        if (fire) {
            target.setRemainingFireTicks(target.getRemainingFireTicks() + 100);
            target.hurt(damageSources().inFire(), 2);
        } else if (cooling) {
            target.clearFire();
            if (target.getType() == EntityType.ENDERMAN)
                target.hurt(damageSources().generic(), 0.5f);
        } else if (stack.getFluid().isSame(AllFluids.POTION)) {
            if (target instanceof LivingEntity le && le.isAffectedByPotions()) {
                PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
                if (potionContents != null)
                    for (MobEffectInstance effectInstance : potionContents.getAllEffects()) {
                        MobEffect effect = effectInstance.getEffect().value();

                        if (effect.isInstantenous()) {
                            if (level() instanceof ServerLevel serverLevel)
                                effect.applyInstantenousEffect(serverLevel, this, owner, le, effectInstance.getAmplifier(), 0.5d);
                        } else {
                            le.addEffect(new MobEffectInstance(effectInstance), owner);
                        }
                    }
            }
        } else if (FluidHelper.isTag(stack, AllFluidTags.MILK)) {
            if (target instanceof LivingEntity le && le.isAffectedByPotions())
                le.removeAllEffects();
        } else {
            if (owner instanceof LivingEntity livingOwner)
                livingOwner.setLastHurtMob(target);
            target.hurt(damageSources().generic(), 0.5f);
        }
        super.onHitEntity(hit);
        remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        fire = input.getBooleanOr("Fire", false);
        cooling = input.getBooleanOr("Cooling", false);
        stack = input.read("FluidStack", FluidStack.CODEC).orElse(FluidStack.EMPTY);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Fire", fire);
        output.putBoolean("Cooling", cooling);
        if (!stack.isEmpty())
            output.store("FluidStack", FluidStack.CODEC, stack);
    }

    static final EntityDataAccessor<String> DATA_FLUID = SynchedEntityData.defineId(ChemicalSprayerProjectileEntity.class, EntityDataSerializers.STRING);
    static final EntityDataAccessor<Boolean> DATA_FIRE = SynchedEntityData.defineId(ChemicalSprayerProjectileEntity.class, EntityDataSerializers.BOOLEAN);
    static final EntityDataAccessor<Boolean> DATA_COOLING = SynchedEntityData.defineId(ChemicalSprayerProjectileEntity.class, EntityDataSerializers.BOOLEAN);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLUID, "");
        builder.define(DATA_FIRE, false);
        builder.define(DATA_COOLING, false);
    }

    @Override
    public void tick() {
        if (level().isClientSide()) {
            String fluidId = getEntityData().get(DATA_FLUID);
            stack = fluidId.isEmpty() ? FluidStack.EMPTY
                    : new FluidStack(BuiltInRegistries.FLUID.getValue(Identifier.parse(fluidId)), 1);
            fire = getEntityData().get(DATA_FIRE);
            cooling = getEntityData().get(DATA_COOLING);
            if (!stack.isEmpty() && !fire)
                level().addParticle(
                    FluidFX.getFluidParticle(stack),
                    position().x + getRandom().nextDouble() - 0.5,
                    position().y + 0.3,
                    position().z + getRandom().nextDouble() - 0.5,
                    getDeltaMovement().x,
                    getDeltaMovement().y - 0.1,
                    getDeltaMovement().z);
            if (t >= 1) {
                if (fire)
                    level().addParticle(
                        ParticleTypes.LAVA,
                        position().x,
                        position().y,
                        position().z,
                        getDeltaMovement().x,
                        getDeltaMovement().y - 0.1,
                        getDeltaMovement().z);
                t = 0;
            } else
                t++;
        }
        setDeltaMovement(getDeltaMovement().add(0, -0.015, 0));

        if (fire) {
            Fluid fluid = level().getFluidState(BlockPos.containing(position())).getType();
            boolean flammable = FuelType.getTypeFor(level().registryAccess().lookupOrThrow(CDGRegistries.FUEL_TYPE), fluid).normal().speed() != 0;

            if (flammable)
                level().explode(null, getX(), getY(), getZ(), 3, Level.ExplosionInteraction.BLOCK);
            else if (level().getFluidState(BlockPos.containing(position())).is(Fluids.FLOWING_WATER)
                || level().getFluidState(BlockPos.containing(position())).is(Fluids.WATER)) {
                fire = false;
                if (stack.getFluid().isSame(Fluids.LAVA))
                    remove(RemovalReason.DISCARDED);
                getEntityData().set(DATA_FIRE, false);
            }
        }

        super.tick();
    }

    @Override
    public boolean isOnFire() {
        return fire;
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        if (level().isClientSide())
            return;

        BlockPos facePos = hit.getBlockPos().relative(hit.getDirection());

        if (cooling) {
            if (level().getBlockState(facePos).getBlock() instanceof FireBlock)
                level().setBlockAndUpdate(facePos, Blocks.AIR.defaultBlockState());
            for (Direction dir : Direction.values()) {
                BlockPos adj = facePos.relative(dir);
                if (level().getBlockState(adj).getBlock() instanceof FireBlock)
                    level().setBlockAndUpdate(adj, Blocks.AIR.defaultBlockState());
            }
            level().playSound(null, facePos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2);
        }

        if (fire && level().getBlockState(facePos).canBeReplaced() && level().getFluidState(facePos).isEmpty()) {
            if (BaseFireBlock.canBePlacedAt(level(), facePos, hit.getDirection()))
                level().setBlockAndUpdate(facePos, BaseFireBlock.getState(level(), facePos));
        }

        remove(RemovalReason.DISCARDED);
    }

    @Override
    public float getPickRadius() {
        return 0.0f;
    }
}

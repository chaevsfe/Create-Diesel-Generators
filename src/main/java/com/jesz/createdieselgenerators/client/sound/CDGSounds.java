package com.jesz.createdieselgenerators.client.sound;

import com.jesz.createdieselgenerators.CDGConfig;
import com.jesz.createdieselgenerators.CDGSoundEvents;
import com.jesz.createdieselgenerators.content.diesel_engine.EngineUpgrades;
import com.jesz.createdieselgenerators.content.diesel_engine.IEngine;
import com.jesz.createdieselgenerators.content.diesel_engine.huge.HugeDieselEngineBlockEntity;
import com.jesz.createdieselgenerators.content.diesel_engine.modular.ModularDieselEngineBlockEntity;
import com.jesz.createdieselgenerators.content.diesel_engine.normal.DieselEngineBlockEntity;
import com.jesz.createdieselgenerators.content.distillation.DistillationTankBlockEntity;
import com.jesz.createdieselgenerators.content.pumpjack.PumpjackCrankBlockEntity;
import com.jesz.createdieselgenerators.content.pumpjack.PumpjackHoleBlockEntity;
import com.zurrtum.create.content.contraptions.behaviour.MovementContext;
import com.zurrtum.create.content.trains.entity.CarriageContraption;
import com.zurrtum.create.content.trains.entity.CarriageContraptionEntity;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.util.Mth;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

import static com.jesz.createdieselgenerators.content.diesel_engine.modular.ModularDieselEngineBlock.FACING;

@Environment(EnvType.CLIENT)
public final class CDGSounds {
    private static final Map<BlockPos, EngineSoundInstance> ENGINES = new HashMap<>();
    private static final Map<BlockPos, AbstractTickableSoundInstance> LOOPS = new HashMap<>();
    private static final Map<BlockPos, AbstractTickableSoundInstance> CRANK_RUMBLE = new HashMap<>();
    private static final Map<BlockPos, AbstractTickableSoundInstance> CRANK_HISS = new HashMap<>();
    private static final Map<BlockPos, AbstractTickableSoundInstance> HOLES = new HashMap<>();
    private static final Map<String, EngineSoundInstance> CONTRAPTION_ENGINES = new HashMap<>();

    private CDGSounds() {
    }

    public static void reset() {
        clear(ENGINES);
        clear(LOOPS);
        clear(CRANK_RUMBLE);
        clear(CRANK_HISS);
        clear(HOLES);
        clear(CONTRAPTION_ENGINES);
    }

    private static void clear(Map<?, ? extends AbstractTickableSoundInstance> map) {
        Minecraft mc = Minecraft.getInstance();
        for (AbstractTickableSoundInstance instance : map.values())
            if (instance != null)
                mc.getSoundManager().stop(instance);
        map.clear();
    }

    public static EngineSoundInstance createEngineSound(Vec3 pos) {
        return new EngineSoundInstance(CDGSoundEvents.ENGINE_NORMAL.get(), SoundSource.NEUTRAL, pos, 0.2f);
    }

    public static void tickEngine(DieselEngineBlockEntity be) {
        tickEngine(be, be.getUpgrade(), Vec3.atCenterOf(be.getBlockPos()),
                be.enabled() && be.getThrottle() > 0 && !be.isOverStressed(), be.getThrottle(), 1f, 1f);
    }

    public static void tickEngine(ModularDieselEngineBlockEntity be) {
        Vec3 pos = Vec3.atCenterOf(be.getBlockPos());
        if (be.getBlockState().getValue(FACING).getAxis() == Direction.Axis.X)
            pos = pos.add((double) be.length / 2 - 0.5, 0, 0);
        else
            pos = pos.add(0, 0, (double) be.length / 2 - 0.5);
        tickEngine(be, be.getUpgrade(), pos, be.enabled() && be.getThrottle() > 0 && !be.isOverStressed(), be.getThrottle(), 1f, 1f);
    }

    public static void tickEngine(HugeDieselEngineBlockEntity be) {
        tickEngine(be, be.getUpgrade(), Vec3.atCenterOf(be.getBlockPos()), be.enabled(), be.getThrottle(), 0.5f, be.getThrottle());
    }

    private static <T extends SmartBlockEntity & IEngine> void tickEngine(T be, EngineUpgrades upgrade, Vec3 pos,
                                                                         boolean running, float throttle, float pitchScale, float volumeScale) {
        BlockPos key = be.getBlockPos();
        EngineSoundInstance instance = ENGINES.get(key);
        if (!running) {
            if (instance != null) {
                instance.fadeOut();
                ENGINES.remove(key);
            }
            return;
        }
        if (instance == null || instance.isStopped() || instance.getX() != pos.x || instance.getZ() != pos.z) {
            instance = createEngineSound(pos);
            Minecraft.getInstance().getSoundManager().play(instance);
            ENGINES.put(key, instance);
            return;
        }
        if (!instance.active())
            return;
        instance.keepAlive();
        instance.setPitch(upgrade.getPitchMultiplier(be) * be.getFuelSoundPitch() * pitchScale * throttle);
        instance.setVolume(upgrade.getVolume(be) * volumeScale);
    }

    public static void tickDistillation(DistillationTankBlockEntity be, boolean processing) {
        BlockPos key = be.getBlockPos();
        AbstractTickableSoundInstance instance = LOOPS.get(key);
        if (processing) {
            if (instance == null || instance.isStopped()) {
                instance = new LoopingSoundInstance(SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, 0.5f, 0.45f,
                        Vec3.atCenterOf(key.offset(be.getWidth() / 2, be.getHeight() / 2, be.getWidth() / 2)));
                Minecraft.getInstance().getSoundManager().play(instance);
                LOOPS.put(key, instance);
            }
            return;
        }
        if (instance != null) {
            Minecraft.getInstance().getSoundManager().stop(instance);
            LOOPS.remove(key);
        }
    }

    public static void tickCrank(PumpjackCrankBlockEntity be) {
        BlockPos key = be.getBlockPos();
        boolean running = be.getSpeed() != 0 && be.getBearing() != null;
        if (!running) {
            stopLoop(CRANK_RUMBLE, key);
            stopLoop(CRANK_HISS, key);
            return;
        }
        Vec3 center = Vec3.atCenterOf(key);
        startLoop(CRANK_RUMBLE, key, SoundEvents.MINECART_RIDING, 0.3f, 0.4f, center);
        startLoop(CRANK_HISS, key, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, 0.1f, 1.2f, center);

        float previous = be.prevAngle % 360;
        float current = be.angle % 360;
        boolean crossedBottom = (previous < 10 && current >= 10) || (previous > 350 && current <= 10);
        boolean crossedTop = (previous < 190 && current >= 190) || (previous > 170 && current <= 170);
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null)
            return;
        if (crossedBottom) {
            mc.level.playLocalSound(key, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.2f, 0.3f, false);
            mc.level.playLocalSound(key, SoundEvents.ANVIL_HIT, SoundSource.BLOCKS, 0.1f, 0.5f, false);
            if (mc.level.getRandom().nextFloat() < 0.3f)
                mc.level.playLocalSound(key, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 0.15f, 0.5f, false);
        }
        if (crossedTop) {
            mc.level.playLocalSound(key, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.15f, 0.3f, false);
            mc.level.playLocalSound(key, SoundEvents.CHAIN_STEP, SoundSource.BLOCKS, 0.1f, 0.6f, false);
        }
    }

    public static void tickHole(PumpjackHoleBlockEntity be, boolean running) {
        BlockPos key = be.getBlockPos();
        if (running)
            startLoop(HOLES, key, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, 0.2f, 0.4f, Vec3.atCenterOf(key));
        else
            stopLoop(HOLES, key);
    }

    public static void tickContraptionEngine(MovementContext context) {
        if (!(context.contraption instanceof CarriageContraption contraption))
            return;
        if (!(contraption.entity instanceof CarriageContraptionEntity entity))
            return;
        if (!CDGConfig.client().ENGINES_EMIT_SOUND_ON_TRAINS.get() || entity.getCarriage().train.derailed)
            return;

        String key = entity.getUUID() + "@" + context.localPos.asLong();
        double trainSpeed = context.motion.length() * 2 / (entity.getCarriage().train.maxSpeed() / 28);
        double acceleration = trainSpeed - context.data.getDoubleOr("TrainSpeed", 0);
        context.data.putDouble("TrainSpeed", trainSpeed);

        float throttle = Mth.lerp(0.05f, context.data.getFloatOr("Throttle", 0),
                (float) Math.max(0, Math.min(1, acceleration)));
        context.data.putFloat("Throttle", throttle);

        EngineSoundInstance instance = CONTRAPTION_ENGINES.get(key);
        if (context.disabled) {
            if (instance != null) {
                instance.fadeOut();
                CONTRAPTION_ENGINES.remove(key);
            }
            return;
        }
        if (instance == null || instance.isStopped()) {
            instance = new EngineSoundInstance(CDGSoundEvents.ENGINE_NORMAL.get(), SoundSource.NEUTRAL, context.position, 0.6f);
            instance.setVolume(1f);
            Minecraft.getInstance().getSoundManager().play(instance);
            CONTRAPTION_ENGINES.put(key, instance);
        }
        instance.setPosition(context.position);
        if (!instance.active())
            return;
        instance.keepAlive();
        instance.setPitch((float) Math.min(2, Math.max(Math.min(0.14, throttle) * 5 + trainSpeed / 28, 0.1f)));
    }

    private static void startLoop(Map<BlockPos, AbstractTickableSoundInstance> map, BlockPos key,
                                  SoundEvent event, float volume, float pitch, Vec3 pos) {
        AbstractTickableSoundInstance instance = map.get(key);
        if (instance != null && !instance.isStopped())
            return;
        instance = new LoopingSoundInstance(event, volume, pitch, pos);
        Minecraft.getInstance().getSoundManager().play(instance);
        map.put(key, instance);
    }

    private static void stopLoop(Map<BlockPos, AbstractTickableSoundInstance> map, BlockPos key) {
        AbstractTickableSoundInstance instance = map.remove(key);
        if (instance != null)
            Minecraft.getInstance().getSoundManager().stop(instance);
    }

    @Environment(EnvType.CLIENT)
    public static class LoopingSoundInstance extends AbstractTickableSoundInstance {
        public LoopingSoundInstance(SoundEvent event, float volume, float pitch, Vec3 pos) {
            super(event, SoundSource.BLOCKS, RandomSource.create());
            this.x = pos.x;
            this.y = pos.y;
            this.z = pos.z;
            this.volume = volume;
            this.pitch = pitch;
            this.looping = true;
            this.delay = 0;
            this.attenuation = Attenuation.LINEAR;
        }

        @Override
        public void tick() {
        }
    }
}

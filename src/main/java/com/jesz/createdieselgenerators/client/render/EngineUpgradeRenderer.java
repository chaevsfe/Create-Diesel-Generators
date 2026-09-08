package com.jesz.createdieselgenerators.client.render;

import com.jesz.createdieselgenerators.CDGPartialModels;
import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.jesz.createdieselgenerators.content.diesel_engine.EngineUpgrades;
import com.jesz.createdieselgenerators.content.diesel_engine.huge.HugeDieselEngineBlock;
import com.jesz.createdieselgenerators.content.diesel_engine.huge.HugeDieselEngineBlockEntity;
import com.jesz.createdieselgenerators.content.diesel_engine.modular.ModularDieselEngineBlock;
import com.jesz.createdieselgenerators.content.diesel_engine.modular.ModularDieselEngineBlockEntity;
import com.jesz.createdieselgenerators.content.diesel_engine.normal.DieselEngineBlock;
import com.jesz.createdieselgenerators.content.diesel_engine.normal.DieselEngineBlockEntity;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class EngineUpgradeRenderer {
    private static final Identifier SILENCER = CreateDieselGenerators.rl("silencer");
    private static final Identifier TURBOCHARGER = CreateDieselGenerators.rl("turbocharger");

    private EngineUpgradeRenderer() {
    }

    public static @Nullable SuperByteBufferRenderState extract(BlockEntity be, EngineUpgrades upgrade, int light) {
        if (upgrade == null)
            return null;
        Identifier id = upgrade.getId();
        if (SILENCER.equals(id))
            return extractPartial(be, light, CDGPartialModels.ENGINE_SILENCER, CDGPartialModels.ENGINE_SILENCER_VERTICAL,
                    CDGPartialModels.MODULAR_ENGINE_SILENCER, CDGPartialModels.HUGE_ENGINE_SILENCER);
        if (TURBOCHARGER.equals(id))
            return extractPartial(be, light, CDGPartialModels.ENGINE_TURBOCHARGER, CDGPartialModels.ENGINE_TURBOCHARGER_VERTICAL,
                    CDGPartialModels.MODULAR_TURBOCHARGER, CDGPartialModels.ENGINE_TURBOCHARGER);
        return null;
    }

    private static @Nullable SuperByteBufferRenderState extractPartial(BlockEntity be, int light, PartialModel normalModel,
                                                                      PartialModel normalVerticalModel, PartialModel modularModel, PartialModel hugeModel) {
        if (be instanceof DieselEngineBlockEntity) {
            Direction facing = be.getBlockState().getValue(DieselEngineBlock.FACING);
            if (facing.getAxis() == Direction.Axis.Y)
                return CachedBuffers.partial(normalVerticalModel, be.getBlockState())
                        .center()
                        .rotateYDegrees(facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 270 : 180)
                        .uncenter()
                        .light(light)
                        .extractRenderState();
            return CachedBuffers.partial(normalModel, be.getBlockState())
                    .center()
                    .rotateYDegrees(facing.toYRot())
                    .uncenter()
                    .light(light)
                    .extractRenderState();
        }
        if (be instanceof ModularDieselEngineBlockEntity) {
            Direction facing = be.getBlockState().getValue(ModularDieselEngineBlock.FACING);
            return CachedBuffers.partial(modularModel, be.getBlockState())
                    .center()
                    .rotateYDegrees(facing.toYRot())
                    .uncenter()
                    .light(light)
                    .extractRenderState();
        }
        if (be instanceof HugeDieselEngineBlockEntity) {
            Direction facing = be.getBlockState().getValue(HugeDieselEngineBlock.FACING);
            if (facing.getAxis() == Direction.Axis.Y)
                return CachedBuffers.partial(hugeModel, be.getBlockState())
                        .center()
                        .rotateZDegrees(90)
                        .rotateYDegrees(facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 270 : 90)
                        .uncenter()
                        .light(light)
                        .extractRenderState();
            return CachedBuffers.partial(hugeModel, be.getBlockState())
                    .center()
                    .rotateYDegrees(facing.getAxis() == Direction.Axis.X ? facing.toYRot() : facing.toYRot() + 180)
                    .uncenter()
                    .light(light)
                    .extractRenderState();
        }
        return null;
    }
}

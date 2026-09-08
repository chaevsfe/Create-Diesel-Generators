package com.jesz.createdieselgenerators.content.distillation;

import com.jesz.createdieselgenerators.CDGBlocks;
import com.jesz.createdieselgenerators.CDGConfig;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllSoundEvents;
import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.content.fluids.tank.FluidTankBlockEntity;
import com.zurrtum.create.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;

import java.util.ArrayList;
import java.util.List;
import com.jesz.createdieselgenerators.foundation.CDGInv;

public class DistillationControllerItem extends Item {
    public DistillationControllerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof FluidTankBlockEntity ftbe && ftbe.getBlockState().is(AllBlocks.FLUID_TANK)))
            return super.useOn(context);
        ItemStack itemInHand = context.getPlayer().getItemInHand(InteractionHand.MAIN_HAND);
        BlockPos controllerPos = ftbe.getController();
        int width = ftbe.getControllerBE().getWidth();
        int height = ftbe.getControllerBE().getHeight();

        if (height < CDGConfig.server().DISTILLATION_MIN_HEIGHT.get()) {
            if (context.getPlayer() instanceof ServerPlayer sp)
                sp.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("createdieselgenerators.actionbar.distillation_controller.too_short",
                                        CDGConfig.server().DISTILLATION_MIN_HEIGHT.get())
                                .withStyle(ChatFormatting.RED)));
            return InteractionResult.FAIL;
        }

        FluidInventory tank = CDGInv.fluidsAt(context.getLevel(), ftbe.getBlockPos());
        FluidStack fluidInTank = tank.getStack(0);
        List<BlockPos> positions = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < width; z++) {
                for (int x = 0; x < width; x++) {
                    if (positions.size() >= itemInHand.getCount() && !context.getPlayer().isCreative())
                        break;
                    BlockPos currentPos = controllerPos.offset(x, y, z);
                    if (ConnectivityHandler.isConnected(context.getLevel(), controllerPos, currentPos))
                        positions.add(currentPos);
                }
            }
        }

        if (!context.getPlayer().isCreative() && width * width * height > itemInHand.getCount()) {
            if (context.getPlayer() instanceof ServerPlayer sp)
                sp.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("createdieselgenerators.actionbar.distillation_controller.not_enough").withStyle(ChatFormatting.RED)));

            return InteractionResult.FAIL;
        }

        for (BlockPos pos : positions) {
            context.getLevel().setBlock(pos, CDGBlocks.DISTILLATION_TANK.getDefaultState(), 3);
            if (context.getLevel().isClientSide()) {
                for (int i = 0; i < 30; i++) {
                    Vec3 offset = VecHelper.offsetRandomly(VecHelper.getCenterOf(pos), context.getLevel().getRandom(), .3f);
                    Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, context.getLevel().getRandom(), .1f);
                    context.getLevel().addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemInHand.getItem()), offset.x(), offset.y(),
                            offset.z(), motion.x(), motion.y(), motion.z());
                }
            }
        }
        AllSoundEvents.WRENCH_ROTATE.playAt(context.getLevel(), controllerPos.getX() + (double) width / 2, controllerPos.getY() + (double) height / 2, controllerPos.getZ() + (double) width / 2, 2f, 1f, false);

        if (!context.getPlayer().isCreative() && !context.getLevel().isClientSide()) {
            itemInHand.shrink(positions.size());
        }

        if (context.getLevel().getBlockEntity(controllerPos) instanceof DistillationTankBlockEntity be) {
            be.updateConnectivity();
            be.updateVerticalMulti();
            be.updateTemperature();
            FluidInventory distillerTank = CDGInv.fluidsAt(context.getLevel(), controllerPos);
            if (distillerTank != null)
                CDGInv.fill(distillerTank, fluidInTank, false);
        }

        return InteractionResult.SUCCESS;

    }
}

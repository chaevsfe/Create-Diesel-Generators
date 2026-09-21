package com.jesz.createdieselgenerators.content.items;

import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.foundation.block.IBE;
import com.zurrtum.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.zurrtum.create.catnip.math.VecHelper;
import com.zurrtum.create.foundation.item.ItemPlacementSoundContext;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class MultiBlockContainerBlockItem extends BlockItem {
    public static final SoundType SILENCED_METAL = new SoundType(0.1F, 1.5F, SoundEvents.METAL_BREAK,
            SoundEvents.METAL_STEP, SoundEvents.METAL_PLACE, SoundEvents.METAL_HIT, SoundEvents.METAL_FALL);

    BlockEntityType<?> type;
    public MultiBlockContainerBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext ctx) {
        if(type == null)
            type = ((IBE<?>) getBlock()).getBlockEntityType();
        InteractionResult initialResult = super.place(ctx);
        if (!initialResult.consumesAction())
            return initialResult;
        tryMultiPlace(ctx);
        return initialResult;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, Player player,
                                                 ItemStack stack, BlockState state) {
        TypedEntityData<BlockEntityType<?>> blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);

        if (blockEntityData != null) {
            CompoundTag nbt = blockEntityData.copyTagWithoutId();
            nbt.remove("Size");
            nbt.remove("Height");
            nbt.remove("Controller");
            nbt.remove("LastKnownPos");
            stack.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(blockEntityData.type(), nbt));
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }

    private <T extends BlockEntity & IMultiBlockEntityContainer> void tryMultiPlace(BlockPlaceContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null)
            return;
        if (player.isSteppingCarefully())
            return;
        Direction face = ctx.getClickedFace();
        ItemStack stack = ctx.getItemInHand();
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockPos placedOnPos = pos.relative(face.getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);

        if (placedOnState.getBlock() != getBlock())
            return;
        T tankAt = ConnectivityHandler.partAt(type, world, placedOnPos);
        if (tankAt == null)
            return;
        T controllerBE = tankAt.getControllerBE();
        if (controllerBE == null)
            return;

        int width = controllerBE.getWidth();
        if (width == 1)
            return;

        int tanksToPlace = 0;
        Direction.Axis blockAxis = tankAt.getMainConnectionAxis();
        if (face.getAxis() != blockAxis)
            return;

        Direction facing = Direction.fromAxisAndDirection(blockAxis, Direction.AxisDirection.POSITIVE);
        BlockPos startPos = face == facing.getOpposite() ? controllerBE.getBlockPos()
                .relative(facing.getOpposite())
                : controllerBE.getBlockPos()
                .relative(facing, controllerBE.getHeight());

        if (VecHelper.getCoordinate(startPos, blockAxis) != VecHelper.getCoordinate(pos, blockAxis))
            return;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = blockAxis == Direction.Axis.X ? startPos.offset(0, xOffset, zOffset)
                        : blockAxis == Direction.Axis.Y ? startPos.offset(xOffset, 0, zOffset)
                        : startPos.offset(xOffset, zOffset, 0);
                BlockState blockState = world.getBlockState(offsetPos);
                if (blockState.getBlock() == getBlock())
                    continue;
                if (!blockState.canBeReplaced())
                    return;
                tanksToPlace++;
            }
        }

        if (!player.isCreative() && stack.getCount() < tanksToPlace)
            return;

        ItemPlacementSoundContext quietContext = new ItemPlacementSoundContext(ctx, 0.1f, 1.5f,
                SILENCED_METAL.getPlaceSound());
        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos offsetPos = blockAxis == Direction.Axis.X ? startPos.offset(0, xOffset, zOffset)
                        : blockAxis == Direction.Axis.Y ? startPos.offset(xOffset, 0, zOffset)
                        : startPos.offset(xOffset, zOffset, 0);
                BlockState blockState = world.getBlockState(offsetPos);
                if (blockState.getBlock() == getBlock())
                    continue;
                super.place(quietContext.offset(offsetPos, face));
            }
        }
    }
}

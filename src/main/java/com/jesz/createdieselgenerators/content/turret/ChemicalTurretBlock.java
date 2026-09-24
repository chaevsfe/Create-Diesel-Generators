package com.jesz.createdieselgenerators.content.turret;

import com.jesz.createdieselgenerators.CDGBlockEntityTypes;
import com.jesz.createdieselgenerators.CDGItems;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.content.kinetics.base.KineticBlock;
import com.zurrtum.create.content.kinetics.simpleRelays.ICogWheel;
import com.zurrtum.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.redstone.Orientation;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;
import com.zurrtum.create.infrastructure.fluids.FluidInventoryProvider;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.EntityType;

public class ChemicalTurretBlock extends KineticBlock implements IBE<ChemicalTurretBlockEntity>, ICogWheel, FluidInventoryProvider<ChemicalTurretBlockEntity> {

    @Override
    public FluidInventory getFluidInventory(LevelAccessor world, BlockPos pos, BlockState state, ChemicalTurretBlockEntity blockEntity, Direction side) {
        if (side == null || side == Direction.DOWN)
            return blockEntity.tank.getCapability();
        return null;
    }

    public ChemicalTurretBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.or(box(0, 0, 0, 16, 6, 16), box(1, 6, 1, 15, 15, 15));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof ChemicalTurretBlockEntity be)
            be.redstoneSignal = level.getBestNeighborSignal(pos);
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof ChemicalTurretBlockEntity be) {
            if (player.getItemInHand(hand).isEmpty())
                if (be.controllingPlayer == player) {
                    be.removePlayer();
                    return InteractionResult.SUCCESS;
                } else if (be.controllingPlayer == null) {
                    be.setControllingPlayer(player);
                    return InteractionResult.SUCCESS;
                }
            if (player.getItemInHand(hand).is(CDGItems.LIGHTER.get())) {
                if (!be.lighterUpgrade) {
                    be.lighterUpgrade = true;
                    if (!level.isClientSide())
                        be.notifyUpdate();
                    if (!player.isCreative())
                        player.getItemInHand(hand).shrink(1);
                    IWrenchable.playRotateSound(level, pos);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if(blockEntity instanceof ChemicalTurretBlockEntity be){
            if(be.lighterUpgrade) {
                be.lighterUpgrade = false;
                if(!context.getLevel().isClientSide())
                    be.notifyUpdate();
                if(!context.getPlayer().isCreative())
                    context.getPlayer().getInventory().placeItemBackInInventory(CDGItems.LIGHTER.asStack());
                IWrenchable.playRotateSound(context.getLevel(), context.getClickedPos());
                return InteractionResult.SUCCESS;
            }
        }
        return super.onWrenched(state, context);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation otherPos, boolean isMoving) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof ChemicalTurretBlockEntity be)
            be.redstoneSignal = level.getBestNeighborSignal(pos);
        super.neighborChanged(state, level, pos, block, otherPos, isMoving);
    }
    @Override
    public Class<ChemicalTurretBlockEntity> getBlockEntityClass() {
        return ChemicalTurretBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ChemicalTurretBlockEntity> getBlockEntityType() {
        return CDGBlockEntityTypes.CHEMICAL_TURRET.get();
    }
}

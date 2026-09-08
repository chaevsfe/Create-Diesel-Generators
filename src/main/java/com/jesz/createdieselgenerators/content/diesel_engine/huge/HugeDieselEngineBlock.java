package com.jesz.createdieselgenerators.content.diesel_engine.huge;

import com.jesz.createdieselgenerators.CDGBlockEntityTypes;
import com.jesz.createdieselgenerators.CDGBlocks;
import com.jesz.createdieselgenerators.CDGConfig;
import com.jesz.createdieselgenerators.content.diesel_engine.EngineUpgrades;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.content.kinetics.simpleRelays.ShaftBlock;
import com.zurrtum.create.content.kinetics.steamEngine.PoweredShaftBlock;
import com.zurrtum.create.foundation.block.IBE;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.utility.BlockHelper;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.catnip.placement.IPlacementHelper;
import com.zurrtum.create.catnip.placement.PlacementHelpers;
import com.zurrtum.create.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import static com.jesz.createdieselgenerators.content.diesel_engine.normal.DieselEngineBlock.POWERED;
import static com.zurrtum.create.content.kinetics.base.RotatedPillarKineticBlock.AXIS;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.server.level.ServerLevel;
import com.zurrtum.create.infrastructure.fluids.FluidInventoryProvider;
import net.minecraft.world.level.LevelAccessor;
import com.jesz.createdieselgenerators.foundation.CDGInv;
import com.zurrtum.create.AllFluidItemInventory;
import com.zurrtum.create.infrastructure.fluids.FluidItemInventory;
import net.minecraft.world.entity.EntityTypes;

public class HugeDieselEngineBlock extends Block implements IBE<HugeDieselEngineBlockEntity>, IWrenchable, FluidInventoryProvider<HugeDieselEngineBlockEntity> {

    @Override
    public FluidInventory getFluidInventory(LevelAccessor world, BlockPos pos, BlockState state, HugeDieselEngineBlockEntity blockEntity, Direction side) {
        if (side == null || side.getAxis() != state.getValue(FACING).getAxis())
            return blockEntity.getTank();
        return null;
    }

    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    private static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

    public HugeDieselEngineBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(POWERED, false));
    }
    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        for (EngineUpgrades upgrade : EngineUpgrades.allUpgrades) {
            if (upgrade == EngineUpgrades.EMPTY)
                continue;
            if (upgrade.getItem().is(stack.getItem())) {
                withBlockEntityDo(level, pos, be -> {
                    if (!upgrade.canAddOn(be))
                        return;
                    if(be.upgrade != EngineUpgrades.EMPTY)
                        return;

                    if (!player.isCreative())
                        stack.shrink(1);
                    be.upgrade = upgrade;
                    be.sendData();
                    IWrenchable.playRotateSound(level, pos);
                });
                return InteractionResult.SUCCESS;
            }
        }

        IPlacementHelper placementHelper = PlacementHelpers.get(placementHelperId);
        if (placementHelper.matchesItem(stack))
            return placementHelper.getOffset(player, level, state, pos, hitResult)
                    .placeInWorld(level, (BlockItem) stack.getItem(), player, hand);

        if (!CDGConfig.server().ENGINES_FILLED_WITH_ITEMS.get() || stack.isEmpty() || !(level.getBlockEntity(pos) instanceof SmartBlockEntity be))
            return InteractionResult.TRY_WITH_EMPTY_HAND;

        FluidInventory tank = CDGInv.fluidsAt(level, be.getBlockPos());
        if (tank == null)
            return InteractionResult.TRY_WITH_EMPTY_HAND;

        if (!AllFluidItemInventory.has(stack))
            return InteractionResult.TRY_WITH_EMPTY_HAND;

        try (FluidItemInventory itemTank = AllFluidItemInventory.of(stack)) {
            FluidStack contained = itemTank.getStack(0);
            if (contained.isEmpty())
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            if (!tank.getStack(0).isEmpty())
                return InteractionResult.FAIL;

            int filled = CDGInv.fill(tank, contained, false);
            if (filled <= 0)
                return InteractionResult.FAIL;
            CDGInv.drain(itemTank, filled, false);
            if (!player.isCreative())
                player.setItemInHand(hand, itemTank.getContainer());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        withBlockEntityDo(context.getLevel(), context.getClickedPos(), be -> {
            if(be.upgrade != EngineUpgrades.EMPTY){
                if(!context.getLevel().isClientSide()) {
                    if (!context.getPlayer().isCreative())
                        context.getPlayer().getInventory().placeItemBackInInventory(be.upgrade.getItem());
                    be.upgrade = EngineUpgrades.EMPTY;
                    be.sendData();
                    IWrenchable.playRotateSound(context.getLevel(), context.getClickedPos());
                }
            }
        });
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation otherPos, boolean moving) {
        level.setBlockAndUpdate(pos, state.setValue(POWERED, level.hasNeighborSignal(pos)));

        if (CDGConfig.server().ANALOG_SPEED_CONTROL.get()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof HugeDieselEngineBlockEntity engine) {
                int newSignal = level.getBestNeighborSignal(pos);
                if (newSignal != engine.analogSignal) {
                    engine.setAnalogSignal(newSignal);
                    engine.setSignalChanged(true);
                }
            }
        }

        super.neighborChanged(state, level, pos, block, otherPos, moving);
    }

    public Direction getPreferredFacing(BlockPlaceContext context) {
        Direction preferredSide = null;
        for (Direction side : Iterate.directions) {
            BlockState blockState = context.getLevel()
                    .getBlockState(context.getClickedPos()
                            .relative(side));
            if (blockState.getBlock() instanceof IRotate) {
                if (((IRotate) blockState.getBlock()).hasShaftTowards(context.getLevel(), context.getClickedPos()
                        .relative(side), blockState, side.getOpposite()))
                    if (preferredSide != null && preferredSide.getAxis() != side.getAxis()) {
                        preferredSide = null;
                        break;
                    } else {
                        preferredSide = side;
                    }
            }
        }
        return preferredSide;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        if (preferred == null || (context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown())) {
            Direction nearestLookingDirection = context.getNearestLookingDirection();
            return defaultBlockState().setValue(FACING, context.getPlayer() != null && context.getPlayer()
                    .isShiftKeyDown() ? nearestLookingDirection : nearestLookingDirection.getOpposite());
        }
        return defaultBlockState().setValue(FACING, preferred.getOpposite());
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        BlockPos shaftPos = pos.relative(state.getValue(FACING), 2);
        BlockState shaftState = level.getBlockState(shaftPos);
        if(shaftState.getBlock() instanceof ShaftBlock)
            if(shaftState.getValue(AXIS) != state.getValue(FACING).getAxis())
                level.setBlock(shaftPos, PoweredEngineShaftBlock.getEquivalent(shaftState), 3);
    }

    @Override
    public void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (!movedByPiston) {
            withBlockEntityDo(level, pos, be -> {
                if (be.upgrade != EngineUpgrades.EMPTY)
                    popResource(level, pos, be.upgrade.getItem());
            });
            BlockPos shaftPos = pos.relative(state.getValue(FACING), 2);
            BlockState shaftState = level.getBlockState(shaftPos);
            if (CDGBlocks.POWERED_ENGINE_SHAFT.has(shaftState))
                level.scheduleTick(shaftPos, shaftState.getBlock(), 1);
        }

        if (state.hasBlockEntity())
            level.removeBlockEntity(pos);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public Class<HugeDieselEngineBlockEntity> getBlockEntityClass() {
        return HugeDieselEngineBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends HugeDieselEngineBlockEntity> getBlockEntityType() {
        return CDGBlockEntityTypes.HUGE_DIESEL_ENGINE.get();
    }

    private static class PlacementHelper implements IPlacementHelper {
        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return stack -> stack.is(AllBlocks.SHAFT.asItem());
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return s -> s.getBlock() instanceof HugeDieselEngineBlock;
        }

        @Override
        public PlacementOffset getOffset(Player player, Level level, BlockState state, BlockPos pos,
                                         BlockHitResult ray) {
            BlockPos shaftPos = pos.relative(state.getValue(FACING), 2);
            BlockState shaft = AllBlocks.SHAFT.defaultBlockState();
            for (Direction direction : Direction.orderedByNearest(player)) {
                shaft = shaft.setValue(ShaftBlock.AXIS, direction.getAxis());
                if (shaft.getValue(AXIS) != state.getValue(FACING).getAxis())
                    break;
            }

            BlockState newState = level.getBlockState(shaftPos);
            if (!newState.canBeReplaced())
                return PlacementOffset.fail();

            Direction.Axis axis = shaft.getValue(ShaftBlock.AXIS);
            return PlacementOffset.success(shaftPos,
                    s -> BlockHelper.copyProperties(s, CDGBlocks.POWERED_ENGINE_SHAFT.getDefaultState())
                            .setValue(PoweredShaftBlock.AXIS, axis));
        }
    }
}

package com.jesz.createdieselgenerators.content.distillation;

import com.jesz.createdieselgenerators.CDGBlockEntityTypes;
import com.jesz.createdieselgenerators.CDGRecipes;
import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.client.api.goggles.IHaveHoveringInformation;
import com.zurrtum.create.content.fluids.tank.FluidTankBlock;
import com.zurrtum.create.content.processing.basin.BasinBlockEntity;
import com.zurrtum.create.content.processing.burner.BlazeBurnerBlock;
import com.zurrtum.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.jesz.createdieselgenerators.content.fluids.CDGFluidTank;
import com.zurrtum.create.client.foundation.item.TooltipHelper;
import com.zurrtum.create.foundation.recipe.RecipeFinder;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.infrastructure.config.AllConfigs;
import com.zurrtum.create.catnip.animation.LerpedFloat;
import com.zurrtum.create.client.catnip.lang.FontHelper;
import com.zurrtum.create.client.catnip.lang.Lang;
import com.zurrtum.create.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;
import com.zurrtum.create.foundation.fluid.FluidTank;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.jesz.createdieselgenerators.foundation.CDGInv;
import net.minecraft.world.entity.EntityType;
import com.jesz.createdieselgenerators.CDGFluids;

public class DistillationTankBlockEntity extends SmartBlockEntity implements IMultiBlockEntityContainer.Fluid, IHaveGoggleInformation, IHaveHoveringInformation {
    private static final int MAX_SIZE = 3;

    public float progress;
    public int processingDuration;
    protected FluidInventory fluidCapability;
    protected boolean forceFluidLevelUpdate;
    public FluidTank tankInventory;
    protected BlockPos controller;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected boolean updateCapability;
    public boolean window;
    protected int luminosity;
    protected int width;
    protected int height;
    boolean tanksFull = false;

    private static final int SYNC_RATE = 8;
    protected int syncCooldown;
    protected boolean queuedSync;

    // For rendering purposes only
    private LerpedFloat fluidLevel;
    BlazeBurnerBlock.HeatLevel highestHeatLevel = BlazeBurnerBlock.HeatLevel.NONE;
    int numberOfHighestHeat = 0;

    public DistillationTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        tankInventory = createInventory();
        forceFluidLevelUpdate = true;
        updateConnectivity = false;
        updateCapability = false;
        window = false;
        height = 1;
        width = 1;
        refreshCapability();
    }

    protected CDGFluidTank createInventory() {return new CDGFluidTank(getCapacityMultiplier(), this::onFluidStackChanged);}
    public BlazeBurnerBlock.HeatLevel getHeat() {
        int width = getControllerBE().width;
        BlazeBurnerBlock.HeatLevel highestHeat = BlazeBurnerBlock.HeatLevel.NONE;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos pos = getController().offset(xOffset, -1, zOffset);
                BlockState blockState = level.getBlockState(pos);
                BlazeBurnerBlock.HeatLevel heat = BasinBlockEntity.getHeatLevelOf(blockState);
                if(!highestHeat.isAtLeast(heat))
                    highestHeat = heat;
            }
        }
        return highestHeat;
    }

    // counts the number of burners if an EXACT heat level
    public int getNumberOfHeat(BlazeBurnerBlock.HeatLevel heatTest) {
        int width = getControllerBE().width;
        int heatCount = 0;

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < width; zOffset++) {
                BlockPos pos = getController().offset(xOffset, -1, zOffset);
                BlockState blockState = level.getBlockState(pos);
                BlazeBurnerBlock.HeatLevel heat = BasinBlockEntity.getHeatLevelOf(blockState);
                if(heatTest == heat)
                    heatCount++;
            }
        }
        return heatCount;
    }

    public void updateConnectivity() {
        updateConnectivity = false;
        if (level.isClientSide())
            return;
        if (!isController())
            return;
        ConnectivityHandler.formMulti(this);
    }

    int processingTime = -1;
    DistillationRecipe currentRecipe;
    private void startProcessing() {
        if(currentRecipe == null)
            return;
        processingTime = (currentRecipe.getProcessingDuration());
        if(!level.isClientSide())
            sendData();
    }

    @Override
    public void tick() {
        if (level != null && level.isClientSide())
            com.jesz.createdieselgenerators.client.sound.CDGSounds.tickDistillation(this, isController() && isBottom() && processingTime > -1);
        boolean prevTanksFull = tanksFull;
        tanksFull = false;
        if (isController() && isBottom()) {
            if (processingTime >= 0 && currentRecipe == null) {
                List<Recipe<?>> r = getMatchingRecipes();
                if (!r.isEmpty())
                    currentRecipe = (DistillationRecipe) r.get(0);
            }

            if (processingTime > -1 && currentRecipe != null) {
                boolean canFill = true;
                for (int i = 0; i < currentRecipe.getFluidResults().size(); i++) {
                    if (level.getBlockEntity(getBlockPos().above(i+1)) instanceof DistillationTankBlockEntity be) {
                        if (!isSameMultiBlock(be)) {
                            canFill = false;
                            break;
                        }
                        if (CDGInv.getSpace(be.tankInventory) < (currentRecipe.getFluidResults().get(i).getAmount())) {
                            canFill = false;
                            tanksFull = true;
                            break;
                        }
                    } else {
                        canFill = false;
                        break;
                    }
                }
                if (canFill)
                    processingTime--;

                if (!(tankInventory.getFluid().getAmount() >= currentRecipe.getFluidIngredients().get(0).amount() && currentRecipe.getRequiredHeat().testBlazeBurner(highestHeatLevel))) {
                    currentRecipe = null;
                    processingTime = -1;
                }
            }
            if (processingTime == 0 && currentRecipe != null) {
                if (!level.isClientSide()) {
                    level.playSound(null,
                            getBlockPos().offset(width / 2, height / 2, width / 2),
                            SoundEvents.BREWING_STAND_BREW,
                            SoundSource.BLOCKS, 0.05f, 0.5f);

                    level.playSound(null,
                            getBlockPos().offset(width / 2, height / 2, width / 2),
                            SoundEvents.FIRE_EXTINGUISH,
                            SoundSource.BLOCKS, 0.05f, 0.5f);
                }

                if (currentRecipe.getRequiredHeat().testBlazeBurner(highestHeatLevel)) {
                    for (int j = 0; j < numberOfHighestHeat; j++)
                    {
                        if (tankInventory.getFluid().getAmount() >= currentRecipe.getFluidIngredients().get(0).amount()) {
                            CDGInv.drain(tankInventory, currentRecipe.getFluidIngredients().get(0).amount(), false);
                            if (currentRecipe != null)
                                for (int i = 0; i < currentRecipe.getFluidResults().size(); i++) {
                                    if (!(level.getBlockEntity(getBlockPos().above(i + 1)) instanceof DistillationTankBlockEntity be))
                                        break;

                                    if (!isSameMultiBlock(be))
                                        break;
                                    CDGInv.fill(be.tankInventory, currentRecipe.getFluidResults().get(i), false);
                                }
                        } else {
                            tanksFull = true;
                        }
                    }
                }

                currentRecipe = null;
                processingTime = -1;
                if (!tankInventory.isEmpty() && isController() && isBottom())
                    checkForRecipes();
            }

            progress = currentRecipe != null ? (float) processingTime / (currentRecipe.getProcessingDuration()) : 0;
        }

        super.tick();

        if (tanksFull != prevTanksFull)
            sendData();

        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync)
                sendData();
        }

        if (lastKnownPos == null)
            lastKnownPos = getBlockPos();
        else if (!lastKnownPos.equals(worldPosition)) {
            onPositionChanged();
            return;
        }

        if (updateConnectivity)
            updateConnectivity();

        if (updateCapability) {
            updateCapability = false;
            refreshCapability();
        }
        if (fluidLevel != null)
            fluidLevel.tickChaser();
    }

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public boolean isController() {
        return controller == null ||
                worldPosition.getX() == controller.getX() &&
                worldPosition.getY() == controller.getY() &&
                worldPosition.getZ() == controller.getZ();
    }

    @Override
    public void initialize() {
        super.initialize();
        updateTemperature();
        sendData();
        if (level.isClientSide())
            invalidateRenderBoundingBox();
    }

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }
    protected List<Recipe<?>> getMatchingRecipes() {

        if (!(level instanceof net.minecraft.server.level.ServerLevel serverLevel))
            return List.of();

        List<RecipeHolder<? extends Recipe<?>>> list = RecipeFinder.get(getRecipeCacheKey(), serverLevel, recipe -> recipe.value().getType() == CDGRecipes.DISTILLATION.getType());
        return list.stream()
                .map(RecipeHolder::value)
                .sorted((r1, r2) -> {
                    if(r1 instanceof DistillationRecipe recipe1 && r2 instanceof DistillationRecipe recipe2)
                        return recipe2.getRequiredHeat().ordinal() - recipe1.getRequiredHeat().ordinal();
                    return 0;
                })
                .filter(r ->{
                            if(r instanceof DistillationRecipe recipe){
                                if(!recipe.getRequiredHeat().testBlazeBurner(highestHeatLevel))
                                    return false;
                                return recipe.getFluidIngredients().get(0).test(tankInventory.getFluid());
                            }
                            return false;
                        })
                .collect(Collectors.toList());
    }
    static final Object RECIPE_CACHE_KEY = new Object();
    Object getRecipeCacheKey() {
        return RECIPE_CACHE_KEY;
    }

    protected void onFluidStackChanged(FluidStack newFluidStack) {
        if (!hasLevel())
            return;
        if(!tankInventory.isEmpty() && isController() && isBottom())
            checkForRecipes();

        int luminosity = (int) (newFluidStack.getFluid().defaultFluidState().createLegacyBlock().getLightEmission() / 1.2f);
        boolean reversed = false;
        int maxY = (int) ((getFillState() * height) + 1);

        for (int yOffset = 0; yOffset < height; yOffset++) {
            boolean isBright = reversed ? (height - yOffset <= maxY) : (yOffset < maxY);
            int actualLuminosity = isBright ? luminosity : luminosity > 0 ? 1 : 0;

            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    DistillationTankBlockEntity tankAt = ConnectivityHandler.partAt(getType(), level, pos);
                    if (tankAt == null)
                        continue;
                    level.updateNeighbourForOutputSignal(pos, tankAt.getBlockState()
                            .getBlock());
                    if (tankAt.luminosity == actualLuminosity)
                        continue;
                    tankAt.setLuminosity(actualLuminosity);
                }
            }
        }

        if (!level.isClientSide()) {
            setChanged();
            sendData();
        }

        if (isVirtual()) {
            if (fluidLevel == null)
                fluidLevel = LerpedFloat.linear()
                        .startWithValue(getFillState());
            fluidLevel.chase(getFillState(), .5f, LerpedFloat.Chaser.EXP);
        }
    }

    protected void setLuminosity(int luminosity) {
        if (level.isClientSide())
            return;
        if (this.luminosity == luminosity)
            return;
        this.luminosity = luminosity;
        sendData();
    }

    @SuppressWarnings("unchecked")
    @Override
    public DistillationTankBlockEntity getControllerBE() {
        if (isController())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof DistillationTankBlockEntity)
            return (DistillationTankBlockEntity) blockEntity;
        return null;
    }

    public void applyFluidTankSize(int blocks) {
        tankInventory.setCapacity(blocks * getCapacityMultiplier());
        int overflow = CDGInv.getFluidAmount(tankInventory) - CDGInv.getCapacity(tankInventory);
        if (overflow > 0)
            CDGInv.drain(tankInventory, overflow, false);
        forceFluidLevelUpdate = true;
    }

    public void removeController(boolean keepFluids) {
        if (level.isClientSide())
            return;
        updateConnectivity = true;
        if (!keepFluids)
            applyFluidTankSize(1);
        controller = null;
        width = 1;
        height = 1;
        onFluidStackChanged(tankInventory.getFluid());

        BlockState state = getBlockState();
        if (DistillationTankBlock.isTank(state)) {
            state = state.setValue(DistillationTankBlock.BOTTOM, true);
            state = state.setValue(DistillationTankBlock.TOP, true);
            state = state.setValue(DistillationTankBlock.SHAPE, window ? FluidTankBlock.Shape.WINDOW : FluidTankBlock.Shape.PLAIN);
            level.setBlock(worldPosition, state, 6);
        }

        refreshCapability();
        setChanged();
        sendData();
    }
    public void toggleWindows() {
        DistillationTankBlockEntity be = getControllerBE();
        if (be == null)
            return;
        be.setWindows(!be.window);
    }

    @Override
    public void sendData() {
        if (syncCooldown > 0) {
            queuedSync = true;
            return;
        }
        super.sendData();
        queuedSync = false;
        syncCooldown = SYNC_RATE;
    }

    public void setWindows(boolean window) {
        this.window = window;
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {

                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    BlockState blockState = level.getBlockState(pos);
                    if (!DistillationTankBlock.isTank(blockState))
                        continue;

                    FluidTankBlock.Shape shape = FluidTankBlock.Shape.PLAIN;
                    if (window) {

                        if (width == 1)
                            shape = FluidTankBlock.Shape.WINDOW;

                        if (width == 2)
                            shape = xOffset == 0 ? zOffset == 0 ? FluidTankBlock.Shape.WINDOW_NW : FluidTankBlock.Shape.WINDOW_SW
                                    : zOffset == 0 ? FluidTankBlock.Shape.WINDOW_NE : FluidTankBlock.Shape.WINDOW_SE;

                        if (width == 3 && Math.abs(xOffset - zOffset) == 1)
                            shape = FluidTankBlock.Shape.WINDOW;
                    }
                    level.setBlock(pos, blockState.setValue(DistillationTankBlock.SHAPE, shape), 22);
                    level.getChunkSource()
                            .getLightEngine()
                            .checkBlock(pos);
                }
            }
        }
    }

    @Override
    public void setController(BlockPos controller) {
        if (level.isClientSide() && !isVirtual())
            return;
        if (controller.equals(this.controller))
            return;
        this.controller = controller;
        refreshCapability();
        setChanged();
        sendData();
    }

    public void refreshCapability() {
        fluidCapability = handlerForCapability();
    }

    private FluidInventory handlerForCapability() {
        return isController() ? tankInventory
                : getControllerBE() != null ? getControllerBE().handlerForCapability() : new FluidTank(0);
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        if (isController())
            return super.createRenderBoundingBox().expandTowards(width - 1, 0, width - 1);
        else
            return super.createRenderBoundingBox();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        DistillationTankBlockEntity controllerBE = getControllerBE();
        if (controllerBE == null)
            return false;
        return containedFluidTooltip(tooltip, isPlayerSneaking,
                CDGInv.fluidsAt(level, controllerBE.getBlockPos()));
    }

    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        super.read(tag, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;
        int prevLum = luminosity;
        tanksFull = tag.getBooleanOr("TanksFull", false);

        updateConnectivity = tag.getBooleanOr("Uninitialized", false);
        luminosity = tag.getIntOr("Luminosity", 0);
        controller = null;
        lastKnownPos = null;

        lastKnownPos = tag.read("LastKnownPos", BlockPos.CODEC).orElse(null);
        controller = tag.read("Controller", BlockPos.CODEC).orElse(null);

        if (isController()) {
            window = tag.getBooleanOr("Window", false);
            width = tag.getIntOr("Size", 0);
            height = tag.getIntOr("Height", 0);
            tankInventory.setCapacity(getTotalTankSize() * getCapacityMultiplier());
            tankInventory.read(tag.childOrEmpty("TankContent"));
            if (CDGInv.getSpace(tankInventory) < 0)
                CDGInv.drain(tankInventory, -CDGInv.getSpace(tankInventory), false);
        }
        if (tag.getBooleanOr("ForceFluidLevel", false) || fluidLevel == null)
            fluidLevel = LerpedFloat.linear()
                    .startWithValue(getFillState());

        updateCapability = true;

        if (!clientPacket)
            return;

        boolean changeOfController =
                !Objects.equals(controllerBefore, controller);
        if (changeOfController || prevSize != width || prevHeight != height) {
            if (hasLevel())
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
            if (isController())
                tankInventory.setCapacity(getCapacityMultiplier() * getTotalTankSize());
            invalidateRenderBoundingBox();
        }
        if (isController()) {
            float fillState = getFillState();
            if (tag.getBooleanOr("ForceFluidLevel", false) || fluidLevel == null)
                fluidLevel = LerpedFloat.linear()
                        .startWithValue(fillState);
            fluidLevel.chase(fillState, 0.5f, LerpedFloat.Chaser.EXP);
            processingTime = tag.getIntOr("Progress", 0);
            processingDuration = tag.getIntOr("RecipeDuration", 0);
        }
        if (luminosity != prevLum && hasLevel())
            level.getChunkSource()
                    .getLightEngine()
                    .checkBlock(worldPosition);

        if (tag.getBooleanOr("LazySync", false))
            fluidLevel.chase(fluidLevel.getChaseTarget(), 0.125f, LerpedFloat.Chaser.EXP);
        updateTemperature();
        List<Recipe<?>> r = getMatchingRecipes();
        if (!r.isEmpty()) {
            currentRecipe = (DistillationRecipe) r.get(0);
            if(processingTime <= 0)
                startProcessing();
        }
    }

    public float getFillState() {
        return (float) CDGInv.getFluidAmount(tankInventory) / CDGInv.getCapacity(tankInventory);
    }

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (tanksFull)
            tag.putBoolean("TanksFull", true);
        if (updateConnectivity)
            tag.putBoolean("Uninitialized", true);
        if (lastKnownPos != null)
            tag.store("LastKnownPos", BlockPos.CODEC, lastKnownPos);
        if (!isController())
            tag.store("Controller", BlockPos.CODEC, controller);
        if (isController()) {
            tag.putBoolean("Window", window);
            tankInventory.write(tag.child("TankContent"));
            tag.putInt("Size", width);
            tag.putInt("Height", height);
            tag.putInt("Progress", processingTime);
            tag.putInt("RecipeDuration", currentRecipe == null ? 0 : currentRecipe.getProcessingDuration());

        }
        tag.putInt("Luminosity", luminosity);

        if (!clientPacket)
            return;
        if (forceFluidLevelUpdate)
            tag.putBoolean("ForceFluidLevel", true);
        if (queuedSync)
            tag.putBoolean("LazySync", true);
        forceFluidLevelUpdate = false;
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {

    }

    public int getTotalTankSize() {
        return width * width;
    }

    public static int getCapacityMultiplier() {
        return AllConfigs.server().fluids.fluidTankCapacity.get() * CDGFluids.BUCKET_UNITS;
    }

    public LerpedFloat getFluidLevel() {
        return fluidLevel;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (DistillationTankBlock.isTank(state)) { // safety
            state = state.setValue(DistillationTankBlock.BOTTOM, getBottomConnectivity());
            state = state.setValue(DistillationTankBlock.TOP, getTopConnectivity());
            level.setBlock(getBlockPos(), state, 6);
        }
        if (isController())
            setWindows(window);
        onFluidStackChanged(tankInventory.getFluid());
        setChanged();
    }

    private boolean getBottomConnectivity() {
        if (level.getBlockEntity(getBlockPos().below()) instanceof DistillationTankBlockEntity be)
            return !isSameMultiBlock(be);
        return true;
    }
    private boolean getTopConnectivity() {
        if (level.getBlockEntity(getBlockPos().above()) instanceof DistillationTankBlockEntity be)
            return !isSameMultiBlock(be);
        return true;
    }

    @Override
    public void setExtraData(@Nullable Object data) {
        if (data instanceof Boolean)
            window = (boolean) data;
    }

    @Override
    @Nullable
    public Object getExtraData() {
        return window;
    }

    @Override
    public Object modifyExtraData(Object data) {
        if (data instanceof Boolean windows) {
            windows |= window;
            return windows;
        }
        return data;
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        if (longAxis == Direction.Axis.Y)
            return 1;
        return getMaxWidth();
    }

    @Override
    public int getMaxWidth() {
        return MAX_SIZE;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public boolean hasTank() {
        return true;
    }

    @Override
    public int getTankSize(int tank) {
        return getCapacityMultiplier();
    }

    @Override
    public void setTankSize(int tank, int blocks) {
        applyFluidTankSize(blocks);
    }

    @Override
    public FluidTank getTank(int tank) {
        return tankInventory;
    }

    @Override
    public FluidStack getFluid(int tank) {
        return tankInventory.getFluid()
                .copy();
    }

    public void updateVerticalMulti() {
        BlockState state = this.getBlockState();
        if (DistillationTankBlock.isTank(state)) { // safety
            state = state.setValue(DistillationTankBlock.BOTTOM, getBottomConnectivity());
            state = state.setValue(DistillationTankBlock.TOP, getTopConnectivity());
            if(state != this.getBlockState())
                level.setBlock(getBlockPos(), state, 3);
        }
        if (level.getBlockEntity(getBlockPos().below()) instanceof DistillationTankBlockEntity be)
            be.updateVerticalMulti();
    }

    public boolean isBottom() {
        return !(level.getBlockEntity(getBlockPos().below()) instanceof DistillationTankBlockEntity be && isSameMultiBlock(be));
    }
    void checkForRecipes() {
        if(processingTime <= -1) {
            List<Recipe<?>> r = getMatchingRecipes();
            if (!r.isEmpty()) {
                currentRecipe = (DistillationRecipe) r.get(0);
                startProcessing();
            } else {
                currentRecipe = null;
            }
        }
    }

    private boolean isSameMultiBlock(DistillationTankBlockEntity be) {
        DistillationTankBlockEntity otherControllerBE = be.getControllerBE();
        DistillationTankBlockEntity controllerBE = getControllerBE();
        if(otherControllerBE == null || controllerBE == null)
            return false;
        if(otherControllerBE == controllerBE)
            return true;
        if(otherControllerBE.getBlockPos().getX() == controllerBE.getBlockPos().getX() && otherControllerBE.getBlockPos().getZ() == controllerBE.getBlockPos().getZ())
            return otherControllerBE.width == controllerBE.width;
        return false;
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!isController()) {
            DistillationTankBlockEntity controller = getControllerBE();
            if (controller == null)
                return false;
            return controller.addToTooltip(tooltip, isPlayerSneaking);
        }

        DistillationTankBlockEntity bottomBe = level.getBlockEntity(getBlockPos().below(), CDGBlockEntityTypes.DISTILLATION_TANK.get()).orElse(null);

        if (bottomBe != null && bottomBe.width == width && bottomBe.getController().equals(getController().below())) {
            return bottomBe.addToTooltip(tooltip, isPlayerSneaking);
        }

        if (currentRecipe == null || !tanksFull)
            return false;

        Lang.builder(CreateDieselGenerators.ID)
                .translate("hint.distiller_full.title")
                .style(ChatFormatting.GOLD)
                .forGoggles(tooltip);
        Component hint =
                Lang.builder(CreateDieselGenerators.ID)
                .translate("hint.distiller_full")
                .component();
        List<Component> cutComponent = TooltipHelper.cutTextComponent(hint, FontHelper.Palette.GRAY_AND_WHITE);
        for (Component component : cutComponent)
            CreateLang.builder().add(component).forGoggles(tooltip);
        return true;
    }


    public void updateTemperature() {
        if (!isBottom())
            return;
        if (isController()) {
            highestHeatLevel = getHeat();
            numberOfHighestHeat = getNumberOfHeat(highestHeatLevel);
            sendData();
            onFluidStackChanged(tankInventory.getFluid());
            return;
        }
        DistillationTankBlockEntity be = getControllerBE();
        if (be == null)
            return;
        be.updateTemperature();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        level.removeBlockEntity(pos);
        ConnectivityHandler.splitMulti(this);
    }
}

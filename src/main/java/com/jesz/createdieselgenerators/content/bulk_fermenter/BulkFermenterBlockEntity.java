package com.jesz.createdieselgenerators.content.bulk_fermenter;

import com.jesz.createdieselgenerators.CDGBlockEntityTypes;
import com.jesz.createdieselgenerators.CDGRecipes;
import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.content.processing.basin.BasinBlockEntity;
import com.zurrtum.create.content.processing.burner.BlazeBurnerBlock;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.jesz.createdieselgenerators.content.fluids.CDGFluidTank;
import com.zurrtum.create.foundation.recipe.RecipeFinder;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.infrastructure.config.AllConfigs;
import com.zurrtum.create.client.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;
import com.zurrtum.create.foundation.fluid.FluidTank;
import com.zurrtum.create.infrastructure.items.ItemStackHandler;
import com.zurrtum.create.infrastructure.items.CombinedInvWrapper;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.world.entity.EntityTypes;
import com.jesz.createdieselgenerators.CDGFluids;

public class BulkFermenterBlockEntity extends SmartBlockEntity implements IMultiBlockEntityContainerFluidItem, IHaveGoggleInformation {

    private static final int MAX_SIZE = 3;
    Container itemHandler;
    BulkFermenterInventoryWrapper itemCapability = new BulkFermenterInventoryWrapper();
    public ItemStackHandler inventory;
    FluidInventory fluidCapability;
    BulkFermenterFluidHandler tankInventory;
    BlockPos controller;
    BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected boolean updateCapability;
    int width = 1;
    int height = 1;

    private static final int SYNC_RATE = 8;
    int syncCooldown;
    boolean queuedSync;

    public int processingTime = -1;
    BulkFermentingRecipe currentRecipe;

    public boolean packagerMode;

    BlazeBurnerBlock.HeatLevel highestHeatLevel = BlazeBurnerBlock.HeatLevel.NONE;
    public BulkFermenterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        tankInventory = createInventory();
        updateConnectivity = false;
        updateCapability = false;
        inventory = new ItemStackHandler(5) {
            @Override
            public void setChanged() {
                super.setChanged();
                if (level == null)
                    return;

                refreshRecipe();

                if (!level.isClientSide()) {
                    BulkFermenterBlockEntity.this.setChanged();
                    sendData();
                }
            }
        };
        refreshCapability();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour<?>> behaviours) {

    }

    protected BulkFermenterFluidHandler createInventory() {
        return new BulkFermenterFluidHandler(6, getCapacityMultiplier(), f -> onFluidStackChanged());
    }

    public void updateConnectivity() {
        assert level != null;
        updateConnectivity = false;
        if (level.isClientSide())
            return;
        if (!isController())
            return;
        ConnectivityHandler.formMulti(this);
    }

    private void startProcessing() {
        if(currentRecipe == null)
            return;
        processingTime = (currentRecipe.getProcessingDuration());
        sendData();
    }
    @Override
    public void tick() {
        assert level != null;

        if (isController()) {
            if (processingTime >= 0) {
                if (!level.isClientSide() && processingTime % 20 == 0 && new Random().nextInt() % 4 == 0)
                    level.playSound(null, worldPosition.offset(width / 2, height/2, width / 2), SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
                        SoundSource.BLOCKS, .15f, .75f);

                if (processingTime == 1)
                    level.playSound(null, worldPosition.offset(width / 2, height / 2, width / 2), SoundEvents.BREWING_STAND_BREW,
                            SoundSource.BLOCKS, .15f, .75f);

                if (currentRecipe == null) {
                    List<BulkFermentingRecipe> matching = getMatchingRecipes();
                    if (matching.isEmpty())
                        processingTime = -1;
                    else
                        currentRecipe = matching.getFirst();
                } else {
                   if (processingTime == 0 && !level.isClientSide()) {
                       for (int i = 0; i < width * width; i++) {
                            if (!currentRecipe.apply(this, true))
                                break;
                           currentRecipe.apply(this, false);
                       }

                       processingTime = -1;
                   } else {
                       processingTime = (int) Math.max(0, processingTime - Math.sqrt(width * height));
                   }
                }
            }
            if (processingTime == -1) {
                if (currentRecipe != null) {
                    currentRecipe = null;
                    refreshRecipe();

                    if (!level.isClientSide()) {
                        setChanged();
                        sendData();
                    }
                }
            }
        }
        super.tick();
        if (syncCooldown > 0) {
            syncCooldown--;
            if (syncCooldown == 0 && queuedSync) {
                sendData();
            }
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
    }

    protected List<BulkFermentingRecipe> getMatchingRecipes() {
        if (!(level instanceof ServerLevel serverLevel))
            return List.of();
        List<RecipeHolder<?>> list = RecipeFinder.get(RECIPE_CACHE_KEY, serverLevel,
                holder -> holder.value().getType() == CDGRecipes.BULK_FERMENTING.getType());
        return list.stream()
                .map(RecipeHolder::value)
                .filter(BulkFermentingRecipe.class::isInstance)
                .map(BulkFermentingRecipe.class::cast)
                .sorted((r1, r2) -> r2.getRequiredHeat().ordinal() - r1.getRequiredHeat().ordinal())
                .filter(recipe -> recipe.apply(this, true))
                .collect(Collectors.toList());
    }

    private void refreshRecipe() {
        List<BulkFermentingRecipe> matching = getMatchingRecipes();
        if (!matching.contains(currentRecipe))
            processingTime = -1;
        if (processingTime == -1 && !matching.isEmpty()) {
            currentRecipe = matching.getFirst();
            startProcessing();
        }
    }

    static final Object RECIPE_CACHE_KEY = new Object();

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    @Override
    public boolean isController() {
        return controller == null || worldPosition.getX() == controller.getX()
                && worldPosition.getY() == controller.getY() && worldPosition.getZ() == controller.getZ();
    }

    @Override
    public void initialize() {
        super.initialize();
        sendData();
    }

    private void onPositionChanged() {
        removeController(true);
        lastKnownPos = worldPosition;
    }

    protected void onFluidStackChanged() {
        assert level != null;
        if (!hasLevel())
            return;

        refreshRecipe();

        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    BulkFermenterBlockEntity tankAt = ConnectivityHandler.partAt(getType(), level, pos);
                    if (tankAt == null)
                        continue;
                    level.updateNeighbourForOutputSignal(pos, tankAt.getBlockState()
                            .getBlock());
                }
            }
        }

        if (!level.isClientSide()) {
            setChanged();
            sendData();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public BulkFermenterBlockEntity getControllerBE() {
        assert level != null;
        if (isController())
            return this;
        BlockEntity blockEntity = level.getBlockEntity(controller);
        if (blockEntity instanceof BulkFermenterBlockEntity)
            return (BulkFermenterBlockEntity) blockEntity;
        return null;
    }

    @Override
    protected AABB createRenderBoundingBox() {
        if (isController())
            return super.createRenderBoundingBox().expandTowards(width - 1, 0, width - 1);
        else
            return super.createRenderBoundingBox();
    }

    public void applyFluidTankSize(int blocks) {
        tankInventory.setCapacity(blocks * getCapacityMultiplier());
    }

    @Override
    public void removeController(boolean keepContents) {
        assert level != null;
        if (level.isClientSide())
            return;
        updateConnectivity = true;
        if (!keepContents)
            applyFluidTankSize(1);
        controller = null;
        width = 1;
        height = 1;

        onFluidStackChanged();
        refreshCapability();
        setChanged();
        sendData();
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

    @Override
    public void setController(BlockPos controller) {
        assert level != null;
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

    public void initCapability() {
        assert level != null;
        if (!isController()) {
            BulkFermenterBlockEntity controllerBE = getControllerBE();
            if (controllerBE == null)
                return;
            controllerBE.initCapability();
            itemHandler = controllerBE.itemHandler;
            itemCapability.setItemHandler(itemHandler);
            return;
        }

        Container[] inventories = new Container[height * width * width];
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos vaultPos = worldPosition.offset(xOffset, yOffset, zOffset);
                    BulkFermenterBlockEntity tankAt =
                            ConnectivityHandler.partAt(CDGBlockEntityTypes.BULK_FERMENTER.get(), level, vaultPos);
                    inventories[yOffset * width * width + xOffset * width + zOffset] =
                            tankAt != null ? tankAt.inventory : new ItemStackHandler(0);
                }
            }
        }

        itemHandler = new CombinedInvWrapper(inventories);

        // this is a weird way of setting the item handler without invalidating all caps,
        // in turn invalidating the fluid cap and causing create's pipes to stop extracting
        itemCapability.setItemHandler(itemHandler);
    }

    private FluidInventory handlerForCapability() {
        return isController() ?
                tankInventory :
                getControllerBE() != null ? getControllerBE().handlerForCapability() : new BulkFermenterFluidHandler(0, 0, fs -> {});
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @Override
    protected void read(ValueInput tag, boolean clientPacket) {
        assert level != null;
        super.read(tag, clientPacket);

        BlockPos controllerBefore = controller;
        int prevSize = width;
        int prevHeight = height;

        updateConnectivity = tag.getBooleanOr("Uninitialized", false);
        controller = null;
        lastKnownPos = null;

        lastKnownPos = tag.read("LastKnownPos", BlockPos.CODEC).orElse(null);
        controller = tag.read("Controller", BlockPos.CODEC).orElse(null);

        if (isController()) {
            width = tag.getIntOr("Size", 0);
            height = tag.getIntOr("Height", 0);
            highestHeatLevel = BlazeBurnerBlock.HeatLevel.values()[tag.getIntOr("Heat", 0)];
            tankInventory.setCapacity(getTotalTankSize() * getCapacityMultiplier());
            tankInventory.read(tag.childOrEmpty("TankContent"));

            processingTime = tag.getIntOr("ProcessingTime", 0);
        }

        inventory.read(tag.childOrEmpty("Inventory"));

        updateCapability = true;

        if (!clientPacket)
            return;

        boolean changeOfController =
                !Objects.equals(controllerBefore, controller);

        if (hasLevel() && (changeOfController || prevSize != width || prevHeight != height)) {
            level.setBlocksDirty(getBlockPos(), Blocks.AIR.defaultBlockState(), getBlockState());

            if (isController()) {
                tankInventory.setCapacity(getCapacityMultiplier() * getTotalTankSize());
                invalidateRenderBoundingBox();
            }
        }

    }

    @Override
    protected void write(ValueOutput tag, boolean clientPacket) {
        super.write(tag, clientPacket);

        if (updateConnectivity)
            tag.putBoolean("Uninitialized", true);
        if (lastKnownPos != null)
            tag.store("LastKnownPos", BlockPos.CODEC, lastKnownPos);
        if (!isController())
            tag.store("Controller", BlockPos.CODEC, controller);
        if (isController()) {
            tankInventory.write(tag.child("TankContent"));
            tag.putInt("Size", width);
            tag.putInt("Height", height);
            tag.putInt("ProcessingTime", processingTime);
            tag.putInt("Heat", highestHeatLevel.ordinal());
        }
        inventory.write(tag.child("Inventory"));

        if (!clientPacket)
            return;

        if (queuedSync)
            tag.putBoolean("LazySync", true);
    }

    public int getTotalTankSize() {
        return width * width * height;
    }

    public static int getCapacityMultiplier() {
        return CDGFluids.BUCKET_UNITS;
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public void notifyMultiUpdated() {
        onFluidStackChanged();
        setChanged();
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        return AllConfigs.server().fluids.fluidTankCapacity.get();
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
        return tankInventory.tanks.get(tank);
    }

    @Override
    public FluidStack getFluid(int tank) {
        return tankInventory.getStack(tank).copy();
    }

    public BulkFermentingRecipe getRecipe() {
        return currentRecipe;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        assert level != null;

        BulkFermenterBlockEntity controller = getControllerBE();

        if (controller == null)
            return false;

        controller.initCapability();
        Container items = controller.itemCapability;
        FluidInventory fluids = controller.fluidCapability;

        if (items == null || fluids == null)
            return false;

        boolean isEmpty = true;

        int headerIndex = tooltip.size();
        CreateLang.translate("gui.goggles.basin_contents")
                .forGoggles(tooltip);

        Map<Item, Integer> allItems = new HashMap<>();
        for (int i = 0; i < items.getContainerSize(); i++) {
            ItemStack stackInSlot = items.getItem(i);
            if (stackInSlot.isEmpty())
                continue;
            if (allItems.containsKey(stackInSlot.getItem()))
                allItems.replace(stackInSlot.getItem(), stackInSlot.getCount() + allItems.get(stackInSlot.getItem()));
            else
                allItems.put(stackInSlot.getItem(), stackInSlot.getCount());
            isEmpty = false;
        }

        for (Map.Entry<Item, Integer> e : allItems.entrySet()) {
            CreateLang.text("")
                    .add(Component.translatable(e.getKey().getDescriptionId())
                            .withStyle(ChatFormatting.GRAY))
                    .add(CreateLang.text(" x" + e.getValue())
                            .style(ChatFormatting.GREEN))
                    .forGoggles(tooltip, 1);
        }

        LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
        for (int i = 0; i < fluids.size(); i++) {
            FluidStack fluidStack = fluids.getStack(i);
            if (fluidStack.isEmpty())
                continue;
            CreateLang.text("")
                    .add(CreateLang.fluidName(fluidStack)
                            .add(CreateLang.text(" "))
                            .style(ChatFormatting.GRAY)
                            .add(CreateLang.number(fluidStack.getAmount() / (double) CDGFluids.BUCKET_UNITS * 1000)
                                    .add(mb)
                                    .style(ChatFormatting.BLUE)))
                    .forGoggles(tooltip, 1);
            isEmpty = false;
        }

        if (isEmpty)
            tooltip.remove(headerIndex);

        return true;
    }

    public void updateHeat() {
        assert level != null;
        BulkFermenterBlockEntity controller = getControllerBE();
        int width;
        if (controller == null)
            width = 1;
        else {
            if (controller != this) {
                controller.updateHeat();
                return;
            }
            width = controller.width;
        }

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
        highestHeatLevel = highestHeat;

        refreshRecipe();

        if (!level.isClientSide()) {
            setChanged();
            sendData();
        }
    }

    public static class BulkFermenterFluidHandler implements FluidInventory {
        public final NonNullList<FluidTank> tanks = NonNullList.create();
        final int tankCount;
        int capacity;
        final Consumer<FluidStack> updateCallback;

        public BulkFermenterFluidHandler(int tankCount, int capacity, Consumer<FluidStack> updateCallback) {
            this.tankCount = tankCount;
            this.capacity = capacity;
            this.updateCallback = updateCallback;
            for (int i = 0; i < tankCount; i++)
                tanks.add(new FluidTank(capacity));
        }

        @Override
        public int size() {
            return tankCount;
        }

        @Override
        public FluidStack getStack(int slot) {
            return slot < 0 || slot >= tanks.size() ? FluidStack.EMPTY : tanks.get(slot).getFluid();
        }

        @Override
        public void setStack(int slot, FluidStack stack) {
            if (slot < 0 || slot >= tanks.size())
                return;
            tanks.get(slot).setFluid(stack);
            markDirty();
        }

        @Override
        public int getMaxAmountPerStack() {
            return capacity;
        }

        @Override
        public void markDirty() {
            updateCallback.accept(FluidStack.EMPTY);
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
            for (FluidTank tank : tanks)
                tank.setCapacity(capacity);
        }

        public void write(ValueOutput output) {
            ValueOutput.ValueOutputList list = output.childrenList("Tanks");
            for (FluidTank tank : tanks)
                tank.write(list.addChild());
        }

        public void read(ValueInput input) {
            int index = 0;
            for (ValueInput child : input.childrenListOrEmpty("Tanks")) {
                if (index >= tanks.size())
                    break;
                tanks.get(index).read(child);
                index++;
            }
        }
    }
}

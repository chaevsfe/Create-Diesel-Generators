package com.jesz.createdieselgenerators.content.basin_lid;

import com.jesz.createdieselgenerators.CDGRecipes;
import com.zurrtum.create.content.processing.basin.BasinBlockEntity;
import com.zurrtum.create.content.processing.basin.BasinOperatingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.jesz.createdieselgenerators.content.basin_lid.BasinLidBlock.ON_A_BASIN;
import static com.jesz.createdieselgenerators.content.basin_lid.BasinLidBlock.OPEN;

public class BasinLidBlockEntity extends BasinOperatingBlockEntity {

    public boolean steamInside = false;
    public int processingTime;
    public boolean running;
    public float progress;


    public BasinLidBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void write(ValueOutput compound, boolean clientPacket) {
        super.write(compound, clientPacket);

        compound.putInt("ProcessingTime", this.processingTime);
        compound.putBoolean("Running", this.running);
        compound.putBoolean("SteamInside", this.steamInside);
    }

    @Override
    protected void read(ValueInput compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        this.processingTime = compound.getIntOr("ProcessingTime", 0);
        this.running = compound.getBooleanOr("Running", false);
        this.steamInside = compound.getBooleanOr("SteamInside", false);
    }

    @Override
    protected void onBasinRemoved() {
        if (!this.running) return;
        this.processingTime = 0;
        this.currentRecipe = null;
        this.running = false;
    }

    @Override
    public void tick() {
        super.tick();
        if (currentRecipe instanceof BasinFermentingRecipe fermenting)
            progress = (float) processingTime / fermenting.getProcessingDuration();
        else {
            if (processingTime != -1) {
                Recipe<?> recipe = this.getMatchingRecipes();
                if (recipe != null)
                    this.currentRecipe = recipe;
                else
                    processingTime = -1;
            }
            progress = 0;
        }
        if ((!level.isClientSide() && (currentRecipe == null || processingTime == -1)) || getBlockState().getValue(OPEN) || !getBlockState().getValue(ON_A_BASIN)) {
            this.running = false;
            this.processingTime = -1;
            this.basinChecker.scheduleUpdate();
        }
        if (running)
            steamInside = true;
        if (running && level != null) {
            if (!level.isClientSide() && this.processingTime <= 0) {
                this.processingTime = -1;
                this.applyBasinRecipe();
                this.sendData();
            }
            if (!level.isClientSide() && processingTime % 20 == 0 && new Random().nextInt() % 4 == 0)
                level.playSound(null, worldPosition, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
                        SoundSource.BLOCKS, .15f, speed < 65 ? .75f : 1.5f);

            if (processingTime == 1)
                level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW,
                        SoundSource.BLOCKS, .15f, speed < 65 ? .75f : 1.5f);

            if (processingTime > 0)
                processingTime--;
        }
    }
    @Override
    protected boolean updateBasin() {
        if (this.running) return true;
        if (this.level == null) return true;
        if (this.getBasin().filter(BasinBlockEntity::canContinueProcessing).isEmpty()) return true;

        Recipe<?> recipe = this.getMatchingRecipes();
        if (recipe == null) return true;
        this.currentRecipe = recipe;
        this.startProcessingBasin();
        this.sendData();
        return true;
    }

    @Override
    public void startProcessingBasin() {
        if (this.running && this.processingTime > 0) return;
        super.startProcessingBasin();
        this.running = true;
        this.processingTime = this.currentRecipe instanceof BasinFermentingRecipe processed ? processed.getProcessingDuration() : 20;
    }

    @Override
    protected boolean isRunning() {
        return running;
    }


    @Override
    protected Optional<BasinBlockEntity> getBasin() {
        if (level == null)
            return Optional.empty();
        BlockEntity basinBE = level.getBlockEntity(worldPosition.below(1));
        if (!(basinBE instanceof BasinBlockEntity))
            return Optional.empty();
        if (getBlockState().getValue(OPEN))
            return Optional.empty();
        return Optional.of((BasinBlockEntity) basinBE);
    }

    @Override
    protected boolean matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe) {
        return recipe.value() instanceof BasinFermentingRecipe;
    }

    private static final Object basinFermentingRecipesKey = new Object();
    @Override
    protected Object getRecipeCacheKey() {
        return basinFermentingRecipesKey;
    }
}

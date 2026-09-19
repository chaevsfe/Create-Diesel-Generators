package com.jesz.createdieselgenerators;

import com.jesz.createdieselgenerators.content.andesite_girder.AndesiteGirderBlock;
import com.jesz.createdieselgenerators.content.andesite_girder.AndesiteGirderEncasedShaftBlock;
import com.jesz.createdieselgenerators.content.basin_lid.BasinLidBlock;
import com.jesz.createdieselgenerators.content.bulk_fermenter.BulkFermenterBlock;
import com.jesz.createdieselgenerators.content.burner.BurnerBlock;
import com.jesz.createdieselgenerators.content.burner.BurnerBlockEntity;
import com.jesz.createdieselgenerators.content.canister.CanisterBlock;
import com.jesz.createdieselgenerators.content.canister.CanisterBlockItem;
import com.jesz.createdieselgenerators.content.concrete.ConcreteEncasedFluidPipeBlock;
import com.jesz.createdieselgenerators.content.diesel_engine.huge.HugeDieselEngineBlock;
import com.jesz.createdieselgenerators.content.diesel_engine.huge.PoweredEngineShaftBlock;
import com.jesz.createdieselgenerators.content.diesel_engine.modular.ModularDieselEngineBlock;
import com.jesz.createdieselgenerators.content.diesel_engine.normal.DieselEngineBlock;
import com.jesz.createdieselgenerators.content.distillation.DistillationTankBlock;
import com.jesz.createdieselgenerators.content.items.MultiBlockContainerBlockItem;
import com.jesz.createdieselgenerators.content.oil_barrel.OilBarrelBlock;
import com.jesz.createdieselgenerators.content.pumpjack.*;
import com.jesz.createdieselgenerators.content.sheetmetal.SheetMetalPanelBlock;
import com.jesz.createdieselgenerators.content.turret.ChemicalTurretBlock;
import com.jesz.createdieselgenerators.contraption.DieselEngineMovementBehaviour;
import com.jesz.createdieselgenerators.contraption.PumpjackBearingBMovementBehaviour;
import com.jesz.createdieselgenerators.contraption.PumpjackHeadMovementBehaviour;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllBlockTags;
import com.zurrtum.create.AllItemTags;
import com.zurrtum.create.api.behaviour.movement.MovementBehaviour;
import com.zurrtum.create.api.boiler.BoilerHeater;
import com.zurrtum.create.api.stress.BlockStressValues;
import com.zurrtum.create.api.contraption.storage.fluid.MountedFluidStorageType;
import com.jesz.createdieselgenerators.registrate.data.SharedProperties;
import com.jesz.createdieselgenerators.registrate.entry.BlockEntry;
import com.jesz.createdieselgenerators.registrate.fn.NonNullConsumer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.HashMap;
import java.util.Map;

import static com.jesz.createdieselgenerators.CreateDieselGenerators.REGISTRATE;
import static com.jesz.createdieselgenerators.registrate.data.TagGen.axeOnly;
import static com.jesz.createdieselgenerators.registrate.data.TagGen.pickaxeOnly;

public class CDGBlocks {
    
    public static final BlockEntry<BurnerBlock> BURNER = REGISTRATE.block("burner", BurnerBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .transform(pickaxeOnly())
            .tag(CDGTags.HEAT_SOURCES)
            
            .onRegister((b) -> BoilerHeater.REGISTRY.register(b, ((level, pos, state) -> {
                if(level.getBlockEntity(pos) instanceof BurnerBlockEntity be)
                    return state.getValue(BurnerBlock.LIT) ? Math.min(2, be.heat) : -1;
                return -1;
            })))
            .item().build()
            .register();

    public static final BlockEntry<ChemicalTurretBlock> CHEMICAL_TURRET = REGISTRATE.block("chemical_turret", ChemicalTurretBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .transform(pickaxeOnly())
            
            .onRegister(b -> BlockStressValues.IMPACTS.register(b, () -> 4))
            .item().build()
            .register();

    public static final BlockEntry<DieselEngineBlock> DIESEL_ENGINE = REGISTRATE.block("diesel_engine", DieselEngineBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW))
            .transform(pickaxeOnly())
            
            .onRegister(movementBehaviour(new DieselEngineMovementBehaviour()))
            .item()
            .tag(AllItemTags.CONTRAPTION_CONTROLLED)
            
            .build()
            .register();


    public static final BlockEntry<ModularDieselEngineBlock> MODULAR_DIESEL_ENGINE = REGISTRATE.block("large_diesel_engine", ModularDieselEngineBlock::new)
            .lang("Modular Diesel Engine")
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW))
            .transform(pickaxeOnly())
            .onRegister(movementBehaviour(new DieselEngineMovementBehaviour()))
            .item().build()
            .register();

    public static final BlockEntry<HugeDieselEngineBlock> HUGE_DIESEL_ENGINE = REGISTRATE.block("huge_diesel_engine", HugeDieselEngineBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_YELLOW))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            
            .item().build()
            .register();

    public static final BlockEntry<PoweredEngineShaftBlock> POWERED_ENGINE_SHAFT = REGISTRATE.block("powered_engine_shaft", PoweredEngineShaftBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.METAL))
            .transform(pickaxeOnly())
            
            
            .register();

    public static final BlockEntry<BasinLidBlock> BASIN_LID = REGISTRATE.block("basin_lid", BasinLidBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .transform(pickaxeOnly())
            
            .item().build()
            .register();

    public static final BlockEntry<PumpjackBearingBlock> PUMPJACK_BEARING = REGISTRATE.block("pumpjack_bearing", PumpjackBearingBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.GLOW_LICHEN))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            
            .item().build()
            .register();

    public static final BlockEntry<PumpjackHeadBlock> PUMPJACK_HEAD = REGISTRATE.block("pumpjack_head", PumpjackHeadBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.GLOW_LICHEN))
            .transform(pickaxeOnly())
            
            .onRegister(movementBehaviour(new PumpjackHeadMovementBehaviour()))
            .simpleItem()
            .register();

    public static final BlockEntry<PumpjackBearingBBlock> PUMPJACK_BEARING_B = REGISTRATE.block("pumpjack_bearing_b", PumpjackBearingBBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.GLOW_LICHEN))
            .transform(pickaxeOnly())
            
            
            .onRegister(movementBehaviour(new PumpjackBearingBMovementBehaviour()))
            .register();

    public static final BlockEntry<PumpjackHoleBlock> PUMPJACK_HOLE = REGISTRATE.block("pumpjack_hole", PumpjackHoleBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .transform(pickaxeOnly())
            
            .item().build()
            .register();

    public static final BlockEntry<PumpjackCrankBlock> PUMPJACK_CRANK = REGISTRATE.block("pumpjack_crank", PumpjackCrankBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.GLOW_LICHEN))
            .transform(pickaxeOnly())
            
            .item().build()
            .register();

    public static final BlockEntry<CanisterBlock> CANISTER = REGISTRATE.block("canister", CanisterBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.METAL))
            .transform(pickaxeOnly())
            
            .item(CanisterBlockItem::new)
            .properties(p -> p.enchantable(1))
            .build()
            .register();

    public static final BlockEntry<DistillationTankBlock> DISTILLATION_TANK = REGISTRATE.block("distillation_tank", DistillationTankBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .properties(p -> p.isRedstoneConductor((p1, p2, p3) -> true))
            .transform(pickaxeOnly())
            .register();

    public static final BlockEntry<BulkFermenterBlock> BULK_FERMENTER = REGISTRATE.block("bulk_fermenter", BulkFermenterBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.METAL))
            .properties(p -> p.isRedstoneConductor((p1, p2, p3) -> true))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .item(MultiBlockContainerBlockItem::new)
            .build()
            .register();

    public static final BlockEntry<OilBarrelBlock> OIL_BARREL = REGISTRATE.block("oil_barrel", OilBarrelBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.METAL))
            .properties(p -> p.isRedstoneConductor((p1, p2, p3) -> true))
            .transform(pickaxeOnly())
            .tag(AllBlockTags.COPYCAT_ALLOW)
            
            .onRegister(b -> MountedFluidStorageType.REGISTRY.register(b, CDGMountedStorageTypes.OIL_BARREL.get()))
            .item(MultiBlockContainerBlockItem::new)
            .build()
            .register();

    public static final BlockEntry<RotatedPillarBlock> CHIP_WOOD_BLOCK = REGISTRATE.block("chip_wood_block", RotatedPillarBlock::new)
            .initialProperties(() -> Blocks.OAK_PLANKS)
            .tag(BlockTags.PLANKS)
            .transform(axeOnly())
            
            .item().tag(ItemTags.PLANKS).build()
            .register();

    public static final BlockEntry<RotatedPillarBlock> CHIP_WOOD_BEAM = REGISTRATE.block("chip_wood_beam", RotatedPillarBlock::new)
            .initialProperties(() -> Blocks.STRIPPED_OAK_LOG)
            .transform(axeOnly())
            
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> CHIP_WOOD_SLAB = REGISTRATE.block("chip_wood_slab", SlabBlock::new)
            .initialProperties(() -> Blocks.OAK_SLAB)
            .transform(axeOnly())
            
            
            .item()
            .tag(ItemTags.WOODEN_SLABS).build()
            .register();

    public static final BlockEntry<StairBlock> CHIP_WOOD_STAIRS = REGISTRATE.block("chip_wood_stairs", p -> new StairBlock(Blocks.ANDESITE_STAIRS.defaultBlockState(), p))
            .initialProperties(() -> Blocks.OAK_STAIRS)
            .transform(axeOnly())
            
            .item()
            .tag(ItemTags.WOODEN_STAIRS).build()
            .register();

    public static final BlockEntry<Block> ASPHALT_BLOCK = REGISTRATE.block("asphalt_block", Block::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK))
            .properties(p -> p.speedFactor(1.25f))
            .transform(pickaxeOnly())
            
            .simpleItem()
            .register();

    public static final BlockEntry<SlabBlock> ASPHALT_SLAB = REGISTRATE.block("asphalt_slab", SlabBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK))
            .properties(p -> p.speedFactor(1.25f))
            .transform(pickaxeOnly())
            
            
            .simpleItem()
            .register();

    public static final BlockEntry<StairBlock> ASPHALT_STAIRS = REGISTRATE.block("asphalt_stairs", p -> new StairBlock(Blocks.ANDESITE_STAIRS.defaultBlockState(), p))
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK))
            .properties(p -> p.speedFactor(1.25f))
            .transform(pickaxeOnly())
            
            .simpleItem()
            .register();

    public static final BlockEntry<AndesiteGirderBlock> ANDESITE_GIRDER =
            REGISTRATE.block("andesite_girder", AndesiteGirderBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
                    .transform(pickaxeOnly())
                    
                    .item().build()
                    .register();

    public static final BlockEntry<AndesiteGirderEncasedShaftBlock> ANDESITE_GIRDER_ENCASED_SHAFT =
            REGISTRATE.block("andesite_girder_encased_shaft", AndesiteGirderEncasedShaftBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK))
                    .transform(pickaxeOnly())
                    
                    
                    .register();

    public static final BlockEntry<SheetMetalPanelBlock> SHEET_METAL_PANEL =
            REGISTRATE.block("sheet_metal_panel", SheetMetalPanelBlock::new)
                    .initialProperties(SharedProperties::softMetal)
                    .properties(p -> p.mapColor(MapColor.COLOR_LIGHT_GRAY).sound(SoundType.NETHERITE_BLOCK))
                    .transform(pickaxeOnly())
                    
                    .simpleItem()
                    .register();

    public static final Map<DyeColor, BlockEntry<ConcreteEncasedFluidPipeBlock>> CONCRETE_ENCASED_FLUID_PIPES = new HashMap<>();
    static {
        for (DyeColor color : DyeColor.values()) {
            CONCRETE_ENCASED_FLUID_PIPES.put(color,
                REGISTRATE.block(color.getName() + "_concrete_encased_fluid_pipe", ConcreteEncasedFluidPipeBlock::new)
                        .properties(p -> p.mapColor(color.getMapColor()).sound(SoundType.STONE))
                        .transform(pickaxeOnly())
                        
                        
                        .register()
            );
        }
    }
    public static void register() {
    }

    private static NonNullConsumer<? super Block> movementBehaviour(MovementBehaviour movementBehaviour) {
        return b -> MovementBehaviour.REGISTRY.register(b, movementBehaviour);
    }

}

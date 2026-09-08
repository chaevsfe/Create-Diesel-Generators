package com.jesz.createdieselgenerators.events;

import com.jesz.createdieselgenerators.CDGConfig;
import com.jesz.createdieselgenerators.CDGItems;
import com.jesz.createdieselgenerators.CDGRegistries;
import com.jesz.createdieselgenerators.commands.CDGCommands;
import com.jesz.createdieselgenerators.content.entity_filter.ReverseLootTable;
import com.jesz.createdieselgenerators.fuel_type.FuelType;
import com.jesz.createdieselgenerators.mixins.LootItemAccessor;
import com.jesz.createdieselgenerators.mixins.LootPoolAccessor;
import com.jesz.createdieselgenerators.mixins.LootTableAccessor;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameEvents {
    private static final Map<Level, Set<BlockPos>> toExplode = new HashMap<>();

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> new CDGCommands(dispatcher));
        LootTableEvents.ALL_LOADED.register((resourceManager, registry) -> {
            ReverseLootTable.ALL.clear();
            registry.listElements().forEach(holder -> indexLootTable(holder.key().identifier(), holder.value()));
        });
        ServerTickEvents.END_LEVEL_TICK.register(GameEvents::onServerTick);
        FuelValueEvents.BUILD.register((builder, context) -> builder.add(CDGItems.WOOD_CHIPS.get(), CDGItems.WOOD_CHIPS.get().getBurnTime()));
    }

    private static void indexLootTable(Identifier tableId, LootTable table) {
        if (tableId == null || !tableId.getPath().startsWith("entities/"))
            return;
        String path = tableId.getPath().replaceAll("entities/", "");
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath(tableId.getNamespace(), path));
        ((LootTableAccessor) table).getPools().forEach(pool -> {
            for (LootPoolEntryContainer entry : ((LootPoolAccessor) pool).getEntries())
                if (entry instanceof LootItemAccessor lootItem) {
                    List<EntityType<?>> types = ReverseLootTable.ALL.computeIfAbsent(lootItem.getItem().value(), s -> new ArrayList<>());
                    if (!types.contains(type))
                        types.add(type);
                }
        });
    }

    private static void onServerTick(ServerLevel level) {
        Set<BlockPos> pending = toExplode.get(level);
        if (pending == null || pending.isEmpty())
            return;
        for (BlockPos pos : List.copyOf(pending)) {
            level.explode(null, null, null, pos.getX(), pos.getY(), pos.getZ(), 1, true, Level.ExplosionInteraction.BLOCK);
            pending.remove(pos);
        }
        if (pending.isEmpty())
            toExplode.remove(level);
    }

    public static void onExplosion(Level level, double centerX, double centerY, double centerZ) {
        if (!CDGConfig.server().COMBUSTIBLES_BLOW_UP.get() || level.isClientSide())
            return;
        for (int x = -2; x < 2; x++)
            for (int y = -2; y < 2; y++)
                for (int z = -2; z < 2; z++) {
                    BlockPos pos = new BlockPos((int) (x + centerX), (int) (y + centerY), (int) (z + centerZ));
                    if (!level.isInWorldBounds(pos))
                        continue;
                    if (Math.abs(Math.sqrt(x * x + y * y + z * z)) >= 2)
                        continue;
                    FluidState fluidState = level.getFluidState(pos);
                    boolean flammable = FuelType.getTypeFor(level.registryAccess().lookupOrThrow(CDGRegistries.FUEL_TYPE), fluidState.getType()).normal().speed() != 0;
                    if (!flammable)
                        continue;
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    toExplode.computeIfAbsent(level, l -> new HashSet<>()).add(pos);
                    return;
                }
    }
}

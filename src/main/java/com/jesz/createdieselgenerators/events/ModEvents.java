package com.jesz.createdieselgenerators.events;

import com.jesz.createdieselgenerators.CDGBlockEntityTypes;
import com.jesz.createdieselgenerators.CDGBlocks;
import com.jesz.createdieselgenerators.CDGItems;
import com.jesz.createdieselgenerators.content.tools.FueledToolFluidInventory;
import com.zurrtum.create.AllFluidItemInventory;
import net.minecraft.world.item.Item;
import com.jesz.createdieselgenerators.content.bulk_fermenter.BulkFermenterUnpackingHandler;
import com.jesz.createdieselgenerators.content.canister.SpoutCanisterFilling;
import com.jesz.createdieselgenerators.content.molds.BasinSpoutCasting;
import com.zurrtum.create.AllBlockEntityTypes;
import com.zurrtum.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import net.minecraft.world.entity.EntityTypes;

public class ModEvents {
    public static void setup() {
        BlockSpoutingBehaviour.BY_BLOCK_ENTITY.register(CDGBlockEntityTypes.CANISTER.get(), new SpoutCanisterFilling());
        BlockSpoutingBehaviour.BY_BLOCK_ENTITY.register(AllBlockEntityTypes.BASIN, new BasinSpoutCasting());
        BulkFermenterUnpackingHandler.register();
        registerFueledToolInventories();
    }

    private static void registerFueledToolInventories() {
        for (Item item : new Item[]{CDGItems.LIGHTER.get(), CDGItems.CHEMICAL_SPRAYER.get(), CDGItems.CHEMICAL_SPRAYER_LIGHTER.get(), CDGBlocks.CANISTER.asItem()})
            AllFluidItemInventory.ALL.put(item, new AllFluidItemInventory.Entry(FueledToolFluidInventory::new));
    }
}

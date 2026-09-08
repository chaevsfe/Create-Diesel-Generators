package com.jesz.createdieselgenerators.content.bulk_fermenter;

import com.zurrtum.create.infrastructure.items.ItemInventory;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class BulkFermenterInventoryWrapper implements ItemInventory {
    private Container itemHandler;

    public void setItemHandler(Container itemHandler) {
        this.itemHandler = itemHandler;
    }

    @Override
    public int getContainerSize() {
        return itemHandler == null ? 0 : itemHandler.getContainerSize();
    }

    @Override
    public ItemStack getItem(int slot) {
        return itemHandler == null ? ItemStack.EMPTY : itemHandler.getItem(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (itemHandler != null)
            itemHandler.setItem(slot, stack);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return itemHandler != null && itemHandler.canPlaceItem(slot, stack);
    }

    @Override
    public void setChanged() {
        if (itemHandler != null)
            itemHandler.setChanged();
    }
}

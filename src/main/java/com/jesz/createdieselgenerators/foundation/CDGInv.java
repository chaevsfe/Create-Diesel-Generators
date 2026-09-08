package com.jesz.createdieselgenerators.foundation;

import com.zurrtum.create.infrastructure.fluids.FluidInventory;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import com.zurrtum.create.infrastructure.fluids.FluidInventoryProvider;
import com.zurrtum.create.infrastructure.items.ItemInventoryProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CDGInv {

    public static Container itemsAt(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof ItemInventoryProvider<?> provider)
            return provider.getInventory(state, level, pos, level.getBlockEntity(pos), null);
        if (level.getBlockEntity(pos) instanceof Container container)
            return container;
        return null;
    }

    public static FluidInventory fluidsAt(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof FluidInventoryProvider<?> provider)
            return provider.getFluidInventory(state, level, pos, level.getBlockEntity(pos), null);
        return null;
    }

    public static FluidInventory fluidsAt(LevelAccessor level, BlockPos pos, Direction side) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof FluidInventoryProvider<?> provider)
            return provider.getFluidInventory(state, level, pos, level.getBlockEntity(pos), side);
        return null;
    }

    public static int slotLimit(Container container, int slot, ItemStack stack) {
        return Math.min(container.getMaxStackSize(), stack.isEmpty() ? 64 : stack.getMaxStackSize());
    }

    public static ItemStack insertItem(Container container, int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;
        if (!container.canPlaceItem(slot, stack))
            return stack;

        ItemStack existing = container.getItem(slot);
        int limit = slotLimit(container, slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(existing, stack))
                return stack;
            limit -= existing.getCount();
        }
        if (limit <= 0)
            return stack;

        int inserted = Math.min(limit, stack.getCount());
        if (!simulate) {
            if (existing.isEmpty())
                container.setItem(slot, stack.copyWithCount(inserted));
            else {
                existing.grow(inserted);
                container.setItem(slot, existing);
            }
            container.setChanged();
        }
        return inserted >= stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - inserted);
    }

    public static ItemStack insertItemStacked(Container container, ItemStack stack, boolean simulate) {
        ItemStack remainder = stack;
        for (int slot = 0; slot < container.getContainerSize() && !remainder.isEmpty(); slot++)
            if (!container.getItem(slot).isEmpty())
                remainder = insertItem(container, slot, remainder, simulate);
        for (int slot = 0; slot < container.getContainerSize() && !remainder.isEmpty(); slot++)
            if (container.getItem(slot).isEmpty())
                remainder = insertItem(container, slot, remainder, simulate);
        return remainder;
    }

    public static ItemStack extractItem(Container container, int slot, int amount, boolean simulate) {
        if (amount <= 0)
            return ItemStack.EMPTY;
        ItemStack existing = container.getItem(slot);
        if (existing.isEmpty())
            return ItemStack.EMPTY;
        int extracted = Math.min(amount, existing.getCount());
        ItemStack result = existing.copyWithCount(extracted);
        if (!simulate) {
            if (extracted >= existing.getCount())
                container.setItem(slot, ItemStack.EMPTY);
            else {
                existing.shrink(extracted);
                container.setItem(slot, existing);
            }
            container.setChanged();
        }
        return result;
    }

    public static int getFluidAmount(FluidInventory inventory) {
        int total = 0;
        for (int slot = 0; slot < inventory.size(); slot++)
            total += inventory.getStack(slot).getAmount();
        return total;
    }

    public static int getCapacity(FluidInventory inventory) {
        return inventory.getMaxAmountPerStack() * inventory.size();
    }

    public static int getSpace(FluidInventory inventory) {
        return getCapacity(inventory) - getFluidAmount(inventory);
    }

    public static int fill(FluidInventory inventory, FluidStack resource, boolean simulate) {
        if (resource.isEmpty())
            return 0;
        int max = inventory.getMaxAmountPerStack();
        int remaining = resource.getAmount();
        int filled = 0;
        for (int slot = 0; slot < inventory.size() && remaining > 0; slot++) {
            FluidStack inSlot = inventory.getStack(slot);
            if (inSlot.isEmpty() || !FluidStack.areFluidsAndComponentsEqualIgnoreCapacity(inSlot, resource))
                continue;
            int space = max - inSlot.getAmount();
            if (space <= 0)
                continue;
            int placed = Math.min(space, remaining);
            if (!simulate)
                inventory.setStack(slot, inSlot.copyWithAmount(inSlot.getAmount() + placed));
            remaining -= placed;
            filled += placed;
        }
        for (int slot = 0; slot < inventory.size() && remaining > 0; slot++) {
            if (!inventory.getStack(slot).isEmpty())
                continue;
            int placed = Math.min(max, remaining);
            if (!simulate)
                inventory.setStack(slot, resource.copyWithAmount(placed));
            remaining -= placed;
            filled += placed;
        }
        if (!simulate && filled > 0)
            inventory.markDirty();
        return filled;
    }

    public static FluidStack drain(FluidInventory inventory, int maxDrain, boolean simulate) {
        return drain(inventory, null, maxDrain, simulate);
    }

    public static FluidStack drain(FluidInventory inventory, FluidStack resource, boolean simulate) {
        return resource.isEmpty() ? FluidStack.EMPTY : drain(inventory, resource, resource.getAmount(), simulate);
    }

    private static FluidStack drain(FluidInventory inventory, FluidStack filter, int maxDrain, boolean simulate) {
        if (maxDrain <= 0)
            return FluidStack.EMPTY;
        FluidStack result = FluidStack.EMPTY;
        int remaining = maxDrain;
        for (int slot = 0; slot < inventory.size() && remaining > 0; slot++) {
            FluidStack inSlot = inventory.getStack(slot);
            if (inSlot.isEmpty())
                continue;
            if (filter != null && !FluidStack.areFluidsAndComponentsEqualIgnoreCapacity(inSlot, filter))
                continue;
            if (!result.isEmpty() && !FluidStack.areFluidsAndComponentsEqualIgnoreCapacity(inSlot, result))
                continue;
            int taken = Math.min(inSlot.getAmount(), remaining);
            if (!simulate) {
                int left = inSlot.getAmount() - taken;
                inventory.setStack(slot, left <= 0 ? FluidStack.EMPTY : inSlot.copyWithAmount(left));
            }
            remaining -= taken;
            result = result.isEmpty() ? inSlot.copyWithAmount(taken) : result.copyWithAmount(result.getAmount() + taken);
        }
        if (!simulate && !result.isEmpty())
            inventory.markDirty();
        return result;
    }

    public static void actionBar(Player player, Component message, boolean actionBar) {
        if (player instanceof ServerPlayer serverPlayer)
            serverPlayer.sendSystemMessage(message, actionBar);
    }
}

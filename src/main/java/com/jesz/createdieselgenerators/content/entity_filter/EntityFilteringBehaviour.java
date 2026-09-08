package com.jesz.createdieselgenerators.content.entity_filter;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.AllSoundEvents;
import com.zurrtum.create.content.logistics.filter.FilterItem;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.filtering.ServerFilteringBehaviour;
import com.zurrtum.create.foundation.item.ItemHelper;
import com.zurrtum.create.client.foundation.utility.CreateLang;
import com.zurrtum.create.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import com.jesz.createdieselgenerators.foundation.CDGInv;

public class EntityFilteringBehaviour extends ServerFilteringBehaviour {
    public EntityFilteringBehaviour(SmartBlockEntity be) {
        super(be);
    }

    public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
        Level level = getLevel();
        BlockPos pos = getPos();
        ItemStack itemInHand = player.getItemInHand(hand);
        ItemStack toApply = itemInHand.copy();

        if (toApply.is(AllItems.WRENCH))
            return;
        if (toApply.is(AllBlocks.MECHANICAL_ARM.asItem()))
            return;
        if (level.isClientSide())
            return;

        if (getFilter().getItem() instanceof EntityFilterItem) {
            if (!player.isCreative() || !hasCopyOf(player, getFilter()))
                player.getInventory()
                        .placeItemBackInInventory(getFilter());
        }

        if (toApply.getItem() instanceof EntityFilterItem)
            toApply.setCount(1);

        if (!super.setFilter(side, toApply)) {
            CDGInv.actionBar(player, CreateLang.translateDirect("logistics.filter.invalid_item"), true);
            AllSoundEvents.DENY.playOnServer(player.level(), player.blockPosition(), 1, 1);
            return;
        }

        if (!player.isCreative()) {
            if (toApply.getItem() instanceof EntityFilterItem) {
                if (itemInHand.getCount() == 1)
                    player.setItemInHand(hand, ItemStack.EMPTY);
                else
                    itemInHand.shrink(1);
            }
        }

        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, .25f, .1f);

    }

    private static boolean hasCopyOf(Player player, ItemStack filter) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++)
            if (ItemStack.isSameItemSameComponents(player.getInventory().getItem(slot), filter))
                return true;
        return false;
    }

    @Override
    public boolean setFilter(ItemStack stack) {
        if(stack.getItem() instanceof FilterItem)
            return false;
        if(stack.getItem() instanceof EntityFilterItem || stack.isEmpty())
            return super.setFilter(stack);
        return false;
    }

    @Override
    public void destroy() {
        if (getFilter().getItem() instanceof EntityFilterItem) {
            Vec3 pos = VecHelper.getCenterOf(getPos());
            Level level = getLevel();
            level.addFreshEntity(new ItemEntity(level, pos.x, pos.y, pos.z, getFilter().copy()));
        }
        super.destroy();
    }
}

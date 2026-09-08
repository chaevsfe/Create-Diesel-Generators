package com.jesz.createdieselgenerators.client.mixins;

import com.jesz.createdieselgenerators.CDGItems;
import com.jesz.createdieselgenerators.client.render.MoldBasinLayout;
import com.jesz.createdieselgenerators.client.render.MoldBasinRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.catnip.math.VecHelper;
import com.zurrtum.create.client.content.processing.basin.BasinRenderer;
import com.zurrtum.create.content.processing.basin.BasinBlockEntity;
import com.zurrtum.create.content.processing.basin.BasinInventory;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(BasinRenderer.class)
public abstract class BasinRendererMixin {
    @Shadow
    @Final
    protected ItemModelResolver itemModelManager;

    @Inject(method = "updateIngredients", at = @At("HEAD"), cancellable = true)
    private void createdieselgenerators$layOutOnMold(
            BasinBlockEntity basin, BlockPos pos, BasinRenderer.BasinRenderState state, float partialTicks, float fluidLevel, CallbackInfo ci) {
        MoldBasinRenderState holder = (MoldBasinRenderState) state;
        BasinInventory inventory = basin.itemCapability;
        if (inventory == null) {
            holder.createdieselgenerators$setMoldLayout(null);
            return;
        }

        List<ItemStack> items = new ArrayList<>();
        boolean hasMold = false;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty())
                continue;
            items.add(stack);
            if (CDGItems.MOLD.isIn(stack))
                hasMold = true;
        }
        if (!hasMold) {
            holder.createdieselgenerators$setMoldLayout(null);
            return;
        }

        Level level = basin.getLevel();
        RandomSource random = RandomSource.create(pos.hashCode());
        List<MoldBasinLayout.Entry> entries = new ArrayList<>(items.size());
        for (ItemStack stack : items) {
            boolean mold = CDGItems.MOLD.isIn(stack);
            ItemStackRenderState renderState = new ItemStackRenderState();
            renderState.displayContext = mold ? ItemDisplayContext.GROUND : ItemDisplayContext.FIXED;
            itemModelManager.appendItemLayers(renderState, stack, renderState.displayContext, level, null, 0);
            Vec3 offset = mold ? Vec3.ZERO : VecHelper.offsetRandomly(Vec3.ZERO, random, 1 / 16f);
            entries.add(new MoldBasinLayout.Entry(renderState, offset, mold));
        }

        holder.createdieselgenerators$setMoldLayout(new MoldBasinLayout(entries));
        state.ingredients = null;
        ci.cancel();
    }

    @Inject(
            method = "submit(Lcom/zurrtum/create/client/content/processing/basin/BasinRenderer$BasinRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("TAIL"))
    private void createdieselgenerators$submitMold(
            BasinRenderer.BasinRenderState state, PoseStack ms, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        MoldBasinLayout layout = ((MoldBasinRenderState) state).createdieselgenerators$getMoldLayout();
        if (layout == null)
            return;
        for (MoldBasinLayout.Entry entry : layout.entries()) {
            ms.pushPose();
            if (entry.mold()) {
                ms.translate(0.5, 0.7, 0.5);
                ms.mulPose(Axis.XP.rotationDegrees(90));
                ms.scale(1.75f, 1.75f, 1.75f);
                ms.translate(0, -0.125, 0);
            } else {
                ms.translate(0.5, 0.74, 0.5);
                ms.mulPose(Axis.XP.rotationDegrees(90));
                ms.scale(0.5f, 0.5f, 0.5f);
                ms.translate(entry.offset());
            }
            entry.renderState().submit(ms, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            ms.popPose();
        }
    }
}

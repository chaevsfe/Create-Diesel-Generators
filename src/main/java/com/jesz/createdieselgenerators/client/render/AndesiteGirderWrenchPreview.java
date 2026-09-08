package com.jesz.createdieselgenerators.client.render;

import com.jesz.createdieselgenerators.CDGBlocks;
import com.jesz.createdieselgenerators.content.andesite_girder.AndesiteGirderWrenchBehaviour;
import com.jesz.createdieselgenerators.content.andesite_girder.AndesiteGirderWrenchBehaviour.Action;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.catnip.data.Pair;
import com.zurrtum.create.catnip.math.VecHelper;
import com.zurrtum.create.catnip.theme.Color;
import com.zurrtum.create.client.catnip.outliner.Outliner;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public final class AndesiteGirderWrenchPreview {
    private AndesiteGirderWrenchPreview() {
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || !(mc.hitResult instanceof BlockHitResult result))
            return;

        ClientLevel world = mc.level;
        BlockPos pos = result.getBlockPos();
        Player player = mc.player;
        ItemStack heldItem = player.getMainHandItem();

        if (player.isSteppingCarefully())
            return;
        if (!CDGBlocks.ANDESITE_GIRDER.has(world.getBlockState(pos)))
            return;
        if (!heldItem.is(AllItems.WRENCH))
            return;

        Pair<Direction, Action> dirPair = AndesiteGirderWrenchBehaviour.getDirectionAndAction(result, world, pos);
        if (dirPair == null)
            return;

        Vec3 center = VecHelper.getCenterOf(pos);
        Vec3 edge = center.add(Vec3.atLowerCornerOf(dirPair.getFirst().getUnitVec3i()).scale(0.4));
        Direction.Axis[] axes = Arrays.stream(Iterate.axes)
                .filter(axis -> axis != dirPair.getFirst().getAxis())
                .toArray(Direction.Axis[]::new);

        double normalMultiplier = dirPair.getSecond() == Action.PAIR ? 4 : 1;
        Vec3 corner1 = edge
                .add(Vec3.atLowerCornerOf(Direction.fromAxisAndDirection(axes[0], Direction.AxisDirection.POSITIVE).getUnitVec3i()).scale(0.3))
                .add(Vec3.atLowerCornerOf(Direction.fromAxisAndDirection(axes[1], Direction.AxisDirection.POSITIVE).getUnitVec3i()).scale(0.3))
                .add(Vec3.atLowerCornerOf(dirPair.getFirst().getUnitVec3i()).scale(0.1 * normalMultiplier));

        normalMultiplier = dirPair.getSecond() == Action.HORIZONTAL ? 9 : 2;
        Vec3 corner2 = edge
                .add(Vec3.atLowerCornerOf(Direction.fromAxisAndDirection(axes[0], Direction.AxisDirection.NEGATIVE).getUnitVec3i()).scale(0.3))
                .add(Vec3.atLowerCornerOf(Direction.fromAxisAndDirection(axes[1], Direction.AxisDirection.NEGATIVE).getUnitVec3i()).scale(0.3))
                .add(Vec3.atLowerCornerOf(dirPair.getFirst().getOpposite().getUnitVec3i()).scale(0.1 * normalMultiplier));

        Outliner.getInstance().showAABB("andesiteGirderWrench", new AABB(corner1, corner2))
                .lineWidth(1 / 32f)
                .colored(new Color(95, 95, 255));
    }
}

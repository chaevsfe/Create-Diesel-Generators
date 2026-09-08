package com.jesz.createdieselgenerators.content.sheetmetal;

import com.jesz.createdieselgenerators.CDGBlocks;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.infrastructure.model.WrapperBlockStateModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static com.jesz.createdieselgenerators.content.sheetmetal.SheetMetalPanelBlock.FACING;
import static com.jesz.createdieselgenerators.content.sheetmetal.SheetMetalPanelBlock.ROLL;

@Environment(EnvType.CLIENT)
public class SheetMetalPanelModel extends WrapperBlockStateModel {
    public SheetMetalPanelModel(BlockState state, BlockStateModel.UnbakedRoot root) {
        super(state, root);
    }

    public static BiFunction<BlockState, BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> of() {
        return SheetMetalPanelModel::new;
    }

    @Override
    public void addPartsWithInfo(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        int first = parts.size();
        super.addPartsWithInfo(level, pos, state, random, parts);
        boolean[] culled = new boolean[Direction.Axis.values().length];
        for (Direction d : Iterate.directions) {
            if (d.getAxis() == state.getValue(FACING).getAxis())
                continue;
            BlockState otherState = level.getBlockState(pos.relative(d));
            if (!CDGBlocks.SHEET_METAL_PANEL.has(otherState))
                continue;
            if (otherState.getValue(FACING) != state.getValue(FACING))
                continue;
            if (otherState.getValue(ROLL) != state.getValue(ROLL))
                continue;
            if (!state.getValue(ROLL) && state.getValue(FACING).getAxis().isHorizontal() && d.getAxis().isHorizontal())
                continue;
            if (state.getValue(ROLL) && state.getValue(FACING).getAxis().isHorizontal() && d.getAxis().isVertical())
                continue;
            if (state.getValue(FACING).getAxis().isVertical()
                    && (d.getAxis() == Direction.Axis.Z && state.getValue(ROLL) || d.getAxis() == Direction.Axis.X && !state.getValue(ROLL)))
                continue;
            culled[d.getAxis().ordinal()] = true;
        }
        for (int i = first; i < parts.size(); i++)
            parts.set(i, new FilteredPart(parts.get(i), culled));
    }

    private record FilteredPart(BlockStateModelPart delegate, boolean[] culled) implements BlockStateModelPart {
        @Override
        public List<BakedQuad> getQuads(Direction direction) {
            List<BakedQuad> quads = new ArrayList<>(delegate.getQuads(direction));
            quads.removeIf(quad -> quad.direction() != null && culled[quad.direction().getAxis().ordinal()]);
            return quads;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return delegate.useAmbientOcclusion();
        }

        @Override
        public Material.Baked particleMaterial() {
            return delegate.particleMaterial();
        }

        @Override
        public int materialFlags() {
            return delegate.materialFlags();
        }
    }
}

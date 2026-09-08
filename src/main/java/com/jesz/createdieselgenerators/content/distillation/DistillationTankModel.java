package com.jesz.createdieselgenerators.content.distillation;

import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.infrastructure.model.CTModel;
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
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

@Environment(EnvType.CLIENT)
public class DistillationTankModel extends CTModel {
    public DistillationTankModel(BlockState state, BlockStateModel.UnbakedRoot root) {
        super(state, root, new DistillationTankCTBehavior());
    }

    public static BiFunction<BlockState, BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> of() {
        return DistillationTankModel::new;
    }

    @Override
    public void addPartsWithInfo(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        int first = parts.size();
        super.addPartsWithInfo(level, pos, state, random, parts);
        boolean[] culled = new boolean[Direction.values().length];
        for (Direction d : Iterate.horizontalDirections)
            culled[d.ordinal()] = ConnectivityHandler.isConnected(level, pos, pos.relative(d));
        for (int i = first; i < parts.size(); i++)
            parts.set(i, new MergedPart(parts.get(i), culled));
    }

    private record MergedPart(BlockStateModelPart delegate, boolean[] culled) implements BlockStateModelPart {
        @Override
        public List<BakedQuad> getQuads(Direction direction) {
            if (direction != null)
                return Collections.emptyList();
            List<BakedQuad> quads = new ArrayList<>(delegate.getQuads(null));
            for (Direction d : Iterate.directions)
                if (!culled[d.ordinal()])
                    quads.addAll(delegate.getQuads(d));
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

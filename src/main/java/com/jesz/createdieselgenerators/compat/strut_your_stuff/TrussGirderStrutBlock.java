package com.jesz.createdieselgenerators.compat.strut_your_stuff;

import com.cake.struts.content.StrutModelType;
import com.cake.struts.content.block.StrutBlock;
import com.cake.struts.content.block.StrutBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TrussGirderStrutBlock extends StrutBlock {
    public TrussGirderStrutBlock(Properties properties, StrutModelType modelType) {
        super(properties, modelType);
    }

    @Override
    protected BlockEntityType<? extends StrutBlockEntity> getStrutBlockEntityType() {
        return StrutYourStuffBlockEntityTypes.ANDESITE_GIRDER_STRUT.get();
    }
}

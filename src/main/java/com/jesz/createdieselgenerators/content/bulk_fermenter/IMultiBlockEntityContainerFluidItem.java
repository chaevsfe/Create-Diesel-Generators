package com.jesz.createdieselgenerators.content.bulk_fermenter;

import com.zurrtum.create.foundation.blockEntity.IMultiBlockEntityContainer;

public interface IMultiBlockEntityContainerFluidItem extends IMultiBlockEntityContainer.Fluid {
    default boolean hasInventory() { return false; }
}

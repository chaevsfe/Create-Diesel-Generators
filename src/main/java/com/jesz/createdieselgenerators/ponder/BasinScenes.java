package com.jesz.createdieselgenerators.ponder;

import com.jesz.createdieselgenerators.CDGFluids;
import com.zurrtum.create.content.fluids.tank.FluidTankBlockEntity;
import com.zurrtum.create.client.foundation.ponder.CreateSceneBuilder;
import com.zurrtum.create.client.ponder.api.element.ElementLink;
import com.zurrtum.create.client.ponder.api.element.EntityElement;
import com.zurrtum.create.client.ponder.api.scene.SceneBuilder;
import com.zurrtum.create.client.ponder.api.scene.SceneBuildingUtil;
import com.zurrtum.create.client.ponder.api.scene.Selection;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import com.zurrtum.create.infrastructure.fluids.FluidStack;
import com.zurrtum.create.infrastructure.fluids.FluidInventory;

import static com.jesz.createdieselgenerators.content.basin_lid.BasinLidBlock.OPEN;
import com.jesz.createdieselgenerators.foundation.CDGInv;

public class BasinScenes {
    public static void basin_lid(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("basin_fermenting_station", "Setting up a Basin Processing Station");
        scene.configureBasePlate(0, 0, 3);
        scene.showBasePlate();

        Selection basinSection = util.select().position(1, 1, 1);
        Selection basinLidSection = util.select().position(1, 2, 1);

        scene.idle(40);

        scene.world().showSection(basinSection, Direction.DOWN);
        scene.world().showSection(basinLidSection, Direction.DOWN);
        scene.idle(40);
        scene.overlay().showText(50)
                .attachKeyFrame()
                .text("Basin Lids allow you to process fluids and items into other fluids and items.")
                .pointAt(util.vector().blockSurface(util.grid().at(1, 1, 1), Direction.NORTH))
                .placeNearTarget();
        scene.idle(60);
        scene.overlay().showText(30)
                .attachKeyFrame()
                .text("Once you give it the required ingredients ...")
                .pointAt(util.vector().blockSurface(util.grid().at(1, 1, 1), Direction.NORTH))
                .placeNearTarget();
        scene.idle(30);
        scene.world().modifyBlock(util.grid().at(1, 2, 1), s -> s.setValue(OPEN, true), false);
        scene.idle(15);
        ElementLink<EntityElement> bone_meal =
                scene.world().createItemEntity(new Vec3(1.5, 5, 1.5), util.vector().of(0, 0.2, 0), Items.BONE_MEAL.getDefaultInstance());
        scene.idle(15);
        ElementLink<EntityElement> sugar =
                scene.world().createItemEntity(new Vec3(1.5, 5, 1.5), util.vector().of(0, 0.2, 0), Items.SUGAR.getDefaultInstance());
        scene.idle(30);

        scene.world().modifyBlock(util.grid().at(1, 2, 1), s -> s.setValue(OPEN, false), false);
        scene.idle(50);

        scene.overlay().showText(30)
                .attachKeyFrame()
                .text("... The Basin Processing Station will start processing them.")
                .pointAt(util.vector().blockSurface(util.grid().at(1, 1, 1), Direction.NORTH))
                .placeNearTarget();
        scene.idle(400);

        scene.world().showSection(util.select().fromTo(3, 0, 1, 4, 1, 1), Direction.SOUTH);
        scene.world().showSection(util.select().position(2, 1, 1), Direction.SOUTH);
        scene.idle(20);
        scene.world().setKineticSpeed(util.select().position(3, 0, 0), 16f);
        scene.world().setKineticSpeed(util.select().position(3, 1, 1), -16f);
        scene.idle(10);
        FluidStack content = new FluidStack(CDGFluids.ETHANOL.get()
                .getSource(), CDGFluids.mb(200));
        scene.world().modifyBlockEntity(util.grid().at(4, 0, 1), FluidTankBlockEntity.class, be -> be.getTankInventory()
                .setStack(0, content));
        scene.idle(60);
    }
}

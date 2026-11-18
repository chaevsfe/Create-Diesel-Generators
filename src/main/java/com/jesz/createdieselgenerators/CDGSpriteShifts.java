package com.jesz.createdieselgenerators;

import com.zurrtum.create.client.foundation.block.connected.AllCTTypes;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShifter;
import net.minecraft.resources.Identifier;

public class CDGSpriteShifts {
    public static final CTSpriteShiftEntry MODULAR_DIESEL_ENGINE = horizontal("diesel_engine_big");
    public static final CTSpriteShiftEntry DISTILLATION_TANK = rectangle("distillation_tower/distillation_tank"),
            DISTILLATION_TANK_TOP = rectangle("distillation_tower/distillation_tank_top"),
            DISTILLATION_TANK_NORTH = rectangle("distillation_tower/distillation_tank", "distillation_tower/distillation_tank_pipes_connected");

    public static final CTSpriteShiftEntry BULK_FERMENTER = rectangle("bulk_fermenter/side"),
            BULK_FERMENTER_TOP = rectangle("bulk_fermenter/top");

    public static final CTSpriteShiftEntry OIL_BARREL_TOP = rectangle("oil_barrel/top"),
            OIL_BARREL_SIDE = rectangle("oil_barrel/sideways/iron"),
            OIL_BARREL_SIDE_WHITE = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/white_connected"),
            OIL_BARREL_SIDE_ORANGE = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/orange_connected"),
            OIL_BARREL_SIDE_MAGENTA = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/magenta_connected"),
            OIL_BARREL_SIDE_LIGHT_BLUE = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/light_blue_connected"),
            OIL_BARREL_SIDE_YELLOW = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/yellow_connected"),
            OIL_BARREL_SIDE_LIME = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/lime_connected"),
            OIL_BARREL_SIDE_PINK = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/pink_connected"),
            OIL_BARREL_SIDE_GRAY = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/gray_connected"),
            OIL_BARREL_SIDE_LIGHT_GRAY = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/light_gray_connected"),
            OIL_BARREL_SIDE_CYAN = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/cyan_connected"),
            OIL_BARREL_SIDE_PURPLE = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/purple_connected"),
            OIL_BARREL_SIDE_BLUE = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/blue_connected"),
            OIL_BARREL_SIDE_BROWN = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/brown_connected"),
            OIL_BARREL_SIDE_GREEN = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/green_connected"),
            OIL_BARREL_SIDE_RED = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/red_connected"),
            OIL_BARREL_SIDE_BLACK = rectangleWithOriginal("oil_barrel/sideways/iron", "oil_barrel/sideways/black_connected"),
            OIL_BARREL = rectangle("oil_barrel/vertical/iron"),
            OIL_BARREL_WHITE = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/white_connected"),
            OIL_BARREL_ORANGE = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/orange_connected"),
            OIL_BARREL_MAGENTA = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/magenta_connected"),
            OIL_BARREL_LIGHT_BLUE = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/light_blue_connected"),
            OIL_BARREL_YELLOW = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/yellow_connected"),
            OIL_BARREL_LIME = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/lime_connected"),
            OIL_BARREL_PINK = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/pink_connected"),
            OIL_BARREL_GRAY = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/gray_connected"),
            OIL_BARREL_LIGHT_GRAY = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/light_gray_connected"),
            OIL_BARREL_CYAN = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/cyan_connected"),
            OIL_BARREL_PURPLE = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/purple_connected"),
            OIL_BARREL_BLUE = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/blue_connected"),
            OIL_BARREL_BROWN = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/brown_connected"),
            OIL_BARREL_GREEN = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/green_connected"),
            OIL_BARREL_RED = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/red_connected"),
            OIL_BARREL_BLACK = rectangleWithOriginal("oil_barrel/vertical/iron", "oil_barrel/vertical/black_connected");
    public static void init(){}
    private static CTSpriteShiftEntry horizontal(String name) {
        return CTSpriteShifter.getCT(AllCTTypes.CROSS,  CreateDieselGenerators.rl("block/"+name),
                CreateDieselGenerators.rl("block/"+name+"_connected"));
    }
    private static CTSpriteShiftEntry rectangle(String name) {
        return CTSpriteShifter.getCT(AllCTTypes.RECTANGLE,  CreateDieselGenerators.rl("block/"+name),
                CreateDieselGenerators.rl("block/"+name+"_connected"));
    }

    private static CTSpriteShiftEntry rectangle(String name, String connectedName) {
        return CTSpriteShifter.getCT(AllCTTypes.RECTANGLE,  CreateDieselGenerators.rl("block/"+name),
                CreateDieselGenerators.rl("block/"+connectedName));
    }

    private static CTSpriteShiftEntry rectangleWithOriginal(String name, String connectedName) {
        return CTSpriteShifter.getCT(AllCTTypes.RECTANGLE_WITH_ORIGINAL,  CreateDieselGenerators.rl("block/"+name),
                CreateDieselGenerators.rl("block/"+connectedName));
    }

}

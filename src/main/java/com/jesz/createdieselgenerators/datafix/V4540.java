package com.jesz.createdieselgenerators.datafix;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.Map;
import java.util.function.Supplier;

public class V4540 extends NamespacedSchema {
    public V4540(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    public static void register(DataFixerBuilder builder) {
        Schema schema = builder.addSchema(4540, V4540::new);
        builder.addFixer(new AddNewChoices(schema, "Added Diesel Generators entities", References.ENTITY));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
        schema.registerSimple(map, "createdieselgenerators:chemical_sprayer_projectile");
        return map;
    }
}

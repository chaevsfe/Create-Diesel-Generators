package com.jesz.createdieselgenerators.config;

import com.zurrtum.create.catnip.config.ConfigBase;

public class CDGCommonConfig extends ConfigBase {
    public final ConfigGroup capacities = group(0, "capacities", "Capacities");
    public final ConfigInt TOOL_CAPACITY = i(200, 0, Integer.MAX_VALUE, "tool_capacity", "Capacity of tools requiring fluids, in mB.");
    public final ConfigInt TOOL_CAPACITY_ENCHANTMENT = i(100, 0, Integer.MAX_VALUE, "tool_capacity_enchantment", "Capacity added to tools by the Capacity enchantment, in mB.");
    public final ConfigInt CANISTER_CAPACITY = i(4000, 0, Integer.MAX_VALUE, "canister_capacity", "Canister capacity, in mB.");
    public final ConfigInt CANISTER_CAPACITY_ENCHANTMENT = i(1000, 0, Integer.MAX_VALUE, "canister_capacity_enchantment", "Capacity added to Canisters by the Capacity enchantment, in mB.");

    @Override
    public String getName() {
        return "common";
    }
}

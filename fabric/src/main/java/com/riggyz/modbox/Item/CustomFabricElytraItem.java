package com.riggyz.modbox.Item;

import com.riggyz.modbox.item.CustomElytraItem;

import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;

public class CustomFabricElytraItem extends CustomElytraItem implements FabricElytraItem {
    public CustomFabricElytraItem(Properties props) {
        super(props);
    }
}
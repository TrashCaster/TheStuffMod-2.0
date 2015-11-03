package com.trashcaster.tsm.item;

import com.trashcaster.tsm.TSM;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemAccessory extends Item {

    private final String name;
    private final AccessoryType type;

    public ItemAccessory(String name, AccessoryType type) {
        this.name = name;
        this.type = type;
        GameRegistry.registerItem(this, name);
        setUnlocalizedName(TSM.MODID + "." + name);
        setCreativeTab(CreativeTabs.tabTools);
    }

    public String getName() {
        return name;
    }

    public AccessoryType getType() {
        return type;
    }

    public static enum AccessoryType {
        AMULET;
    }

}

package net.magentagtstudios.javanesedelight.item;

import net.magentagtstudios.javanesedelight.JavaneseDelight;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.swing.*;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JavaneseDelight.MODID);

    public static final DeferredItem<Item> SOYBEANS = ITEMS.register("soy_beans",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TOFU = ITEMS.register("tofu",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TEMPEH = ITEMS.register("tempeh",
            () -> new Item(new Item.Properties()));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}

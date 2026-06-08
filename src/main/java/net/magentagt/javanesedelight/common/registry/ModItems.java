package net.magentagt.javanesedelight.common.registry;

import net.magentagt.javanesedelight.JavaneseDelight;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(JavaneseDelight.MODID);

    // Raw ingredients
    public static final DeferredItem<Item> SOYBEANS = ITEMS.register("soybeans",
            () -> new Item(new Item.Properties()));

    // Processed ingredients
    public static final DeferredItem<Item> COOKED_SOYBEANS = ITEMS.register("cooked_soybeans",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TOFU = ITEMS.register("tofu",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> UNFERMENTED_TEMPEH = ITEMS.register("unfermented_tempeh",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> TEMPEH = ITEMS.register("tempeh",
            () -> new Item(new Item.Properties()));

    //Meals

    //Feasts



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}

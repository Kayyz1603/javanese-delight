package net.magentagt.javanesedelight.common.registry;

import net.magentagt.javanesedelight.JavaneseDelight;
import net.magentagt.javanesedelight.common.FoodValues;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(JavaneseDelight.MODID);

    // Item property helper methods
    public static Item.Properties basicItem() {
        return new Item.Properties();
    }

    public static Item.Properties foodItem(FoodProperties foodProperties) {
        return new Item.Properties().food(foodProperties);
    }

    public static Item.Properties bowlFoodItem(FoodProperties foodProperties) {
        return new Item.Properties().food(foodProperties).craftRemainder(Items.BOWL).stacksTo(16);
    }

    public static Item.Properties bottleItem() {
        return new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16);
    }

    public static Item.Properties bottleFoodItem(FoodProperties foodProperties) {
        return new Item.Properties().food(foodProperties).craftRemainder(Items.GLASS_BOTTLE).stacksTo(16);
    }


    // Raw ingredients
    public static final DeferredItem<Item> SOYBEANS = ITEMS.register("soybeans",
            () -> new Item(basicItem()));

    // Liquids
    public static final DeferredItem<Item> SOY_SAUCE = ITEMS.register("soy_sauce",
            () -> new Item(bottleItem()));

    // Processed ingredients
    public static final DeferredItem<Item> COOKED_SOYBEANS = ITEMS.register("cooked_soybeans",
            () -> new Item(foodItem(FoodValues.COOKED_SOYBEANS)));
    public static final DeferredItem<Item> TOFU = ITEMS.register("tofu",
            () -> new Item(foodItem(FoodValues.TOFU)));
    public static final DeferredItem<Item> TEMPEH = ITEMS.register("tempeh",
            () -> new Item(foodItem(FoodValues.TEMPEH)));

    //Meals

    //Feasts



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
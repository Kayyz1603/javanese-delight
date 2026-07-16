package net.magentagt.javanesedelight.common.registry;

import net.magentagt.javanesedelight.JavaneseDelight;
import net.magentagt.javanesedelight.common.FoodValues;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.item.MilkBottleItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(JavaneseDelight.MODID);

    // Item property helper methods
    public static Item.Properties basicItem() {
        return new Item.Properties();
    }

    public static Item.Properties foodItem(FoodProperties foodProperties) {
        return (new Item.Properties()).food(foodProperties);
    }

    public static Item.Properties bowlFoodItem(FoodProperties foodProperties) {
        return (new Item.Properties()).food(foodProperties).craftRemainder(Items.BOWL).stacksTo(16);
    }

    public static Item.Properties bottleItem() {
        return (new Item.Properties()).craftRemainder(Items.GLASS_BOTTLE).stacksTo(16);
    }


    // Raw ingredients
    public static final DeferredItem<Item> SOYBEANS = ITEMS.register("soybeans",
            () -> new Item(basicItem()));

    // Liquids
    public static final DeferredItem<Item> SOY_SAUCE = ITEMS.register("soy_sauce_bottle",
            () -> new Item(bottleItem()));
    public static final DeferredItem<Item> SWEET_SOY_SAUCE = ITEMS.register("sweet_soy_sauce_bottle",
            () -> new Item(bottleItem()));
    public static final DeferredItem<Item> SOY_MILK = ITEMS.register("soy_milk_bottle",
            () -> new MilkBottleItem(bottleItem()));
    public static final DeferredItem<Item> VINEGAR = ITEMS.register("vinegar_bottle",
            () -> new Item(bottleItem()));

    // Processed ingredients
    public static final DeferredItem<Item> COOKED_SOYBEANS = ITEMS.register("cooked_soybeans",
            () -> new Item(foodItem(FoodValues.COOKED_SOYBEANS)));
    public static final DeferredItem<Item> TOFU = ITEMS.register("tofu",
            () -> new Item(foodItem(FoodValues.TOFU)));
    public static final DeferredItem<Item> TEMPEH = ITEMS.register("tempeh",
            () -> new Item(foodItem(FoodValues.TEMPEH)));
    public static final DeferredItem<Item> UNFERMENTED_SOY_SAUCE_MIX = ITEMS.register("unfermented_soy_sauce_mix",
            () -> new Item(basicItem()));

    // Snacks
    public static final DeferredItem<Item> FRIED_TOFU = ITEMS.register("fried_tofu",
            () -> new Item(foodItem(FoodValues.FRIED_TOFU)));
    public static final DeferredItem<Item> FRIED_TEMPEH = ITEMS.register("fried_tempeh",
            () -> new Item(foodItem(FoodValues.FRIED_TEMPEH)));

    // Meals

    // Feasts



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
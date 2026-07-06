package net.magentagt.javanesedelight.common;

import net.minecraft.world.food.FoodProperties;

public class FoodValues {
    // Raw ingredients

    // Liquids
    public static final FoodProperties SOY_MILK = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.2f).build();


    // Processed ingredients
    public static final FoodProperties COOKED_SOYBEANS = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.4f).build();
    public static final FoodProperties TOFU = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.4f).build();
    public static final FoodProperties TEMPEH = new FoodProperties.Builder()
            .nutrition(4).saturationModifier(0.4f).build();

    // Meals

    // Feasts

}

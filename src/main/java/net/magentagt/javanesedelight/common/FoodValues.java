package net.magentagt.javanesedelight.common;

import net.minecraft.world.food.FoodProperties;

public class FoodValues {
    // Raw ingredients


    // Processed ingredients
    public static final FoodProperties COOKED_SOYBEANS = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.4f).build();
    public static final FoodProperties TOFU = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.4f).build();
    public static final FoodProperties TEMPEH = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.4f).build();

    // Snacks
    public static final FoodProperties FRIED_TOFU = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(4.0f).build();
    public static final FoodProperties FRIED_TEMPEH = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(4.0f).build();

    // Meals

    // Feasts

}

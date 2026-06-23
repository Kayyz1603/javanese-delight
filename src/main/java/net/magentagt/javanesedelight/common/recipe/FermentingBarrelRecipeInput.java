package net.magentagt.javanesedelight.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record FermentingBarrelRecipeInput(List<ItemStack> items) implements RecipeInput {
    @Override
    public ItemStack getItem(int i) {
        return items.get(i);
    }

    public ItemStack getItem(int row, int col) {
        return items.get(row * 2 + col);
    }

    @Override
    public int size() {
        return 4;
    }
}

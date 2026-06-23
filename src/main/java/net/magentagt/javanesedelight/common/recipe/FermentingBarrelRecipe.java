package net.magentagt.javanesedelight.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.magentagt.javanesedelight.common.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

import java.util.List;

public record FermentingBarrelRecipe(NonNullList<Ingredient> inputItems, ItemStack output) implements Recipe<FermentingBarrelRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }

    @Override
    public boolean matches(FermentingBarrelRecipeInput input, Level level) {
        if (level.isClientSide) {
            return false;
        }

        NonNullList<ItemStack> nonEmptyItems = NonNullList.create();
        for (ItemStack item : input.items()) {
            if (!item.isEmpty()) {
                nonEmptyItems.add(item);
            }
        }

        return RecipeMatcher.findMatches(nonEmptyItems, this.inputItems) != null;
    }

    @Override
    public ItemStack assemble(FermentingBarrelRecipeInput fermentingBarrelRecipeInput, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= inputItems.size();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FERMENTING_BARREL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FERMENTING_BARREL_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<FermentingBarrelRecipe> {
        public static final MapCodec<FermentingBarrelRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").xmap(ingredients -> {
                    NonNullList<Ingredient> nonNullList = NonNullList.create();
                    nonNullList.addAll(ingredients);
                    return nonNullList;
                }, ingredients -> ingredients).forGetter(FermentingBarrelRecipe::getIngredients),
                ItemStack.CODEC.fieldOf("result").forGetter(FermentingBarrelRecipe::output)
        ).apply(inst, FermentingBarrelRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FermentingBarrelRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC
                                .apply(ByteBufCodecs.list())
                                .map(list -> {
                                    NonNullList<Ingredient> nonNullList = NonNullList.create();
                                    nonNullList.addAll(list);
                                    return nonNullList;
                                }, list -> list),
                        FermentingBarrelRecipe::inputItems,
                        ItemStack.STREAM_CODEC,
                        FermentingBarrelRecipe::output,
                        FermentingBarrelRecipe::new);

        @Override
        public MapCodec<FermentingBarrelRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FermentingBarrelRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

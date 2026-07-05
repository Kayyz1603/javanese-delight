package net.magentagt.javanesedelight.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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
import vectorwing.farmersdelight.client.recipebook.CookingPotRecipeBookTab;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

public record FermentingBarrelRecipe(NonNullList<Ingredient> inputItems, ItemStack output, Integer fermentingTime, Float experience) implements Recipe<FermentingBarrelRecipeInput> {
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
                ItemStack.CODEC.fieldOf("result").forGetter(FermentingBarrelRecipe::output),
                Codec.INT.optionalFieldOf("fermentingtime", 3600).forGetter(FermentingBarrelRecipe::fermentingTime),
                Codec.FLOAT.fieldOf("experience").forGetter(FermentingBarrelRecipe::experience)
        ).apply(inst, FermentingBarrelRecipe::new));

        // This code is "inspired" by Farmer's Delight (I can't be bothered to figure it out myself)
        public static final StreamCodec<RegistryFriendlyByteBuf, FermentingBarrelRecipe> STREAM_CODEC = StreamCodec.of(FermentingBarrelRecipe.Serializer::toNetwork, FermentingBarrelRecipe.Serializer::fromNetwork);

        private static FermentingBarrelRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            NonNullList<Ingredient> inputItems = NonNullList.withSize(i, Ingredient.EMPTY);
            inputItems.replaceAll((ignored) -> (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buffer);
            float experience = buffer.readFloat();
            int fermentingTime = buffer.readVarInt();
            return new FermentingBarrelRecipe(inputItems, output, fermentingTime, experience);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, FermentingBarrelRecipe recipe) {
            buffer.writeVarInt(recipe.inputItems.size());

            for(Ingredient ingredient : recipe.inputItems) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.fermentingTime);
        }

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

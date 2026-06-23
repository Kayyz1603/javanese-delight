package net.magentagt.javanesedelight.common.registry;

import net.magentagt.javanesedelight.JavaneseDelight;
import net.magentagt.javanesedelight.common.recipe.FermentingBarrelRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, JavaneseDelight.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, JavaneseDelight.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FermentingBarrelRecipe>> FERMENTING_BARREL_SERIALIZER =
            SERIALIZERS.register("fermenting_barrel", FermentingBarrelRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<FermentingBarrelRecipe>> FERMENTING_BARREL_TYPE =
            TYPES.register("fermenting_barrel", () -> new RecipeType<FermentingBarrelRecipe>() {
                @Override
                public String toString() {
                    return "fermenting_barrel";
                }
            });


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}

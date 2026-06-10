package net.magentagt.javanesedelight.common.registry;

import net.magentagt.javanesedelight.JavaneseDelight;
import net.magentagt.javanesedelight.common.block.entity.FermentingBarrelBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, JavaneseDelight.MODID);

    public static final Supplier<BlockEntityType<FermentingBarrelBlockEntity>> FERMENTING_BARREL_BE =
            BLOCK_ENTITES.register("fermenting_barrel_be", () -> BlockEntityType.Builder.of(
                    FermentingBarrelBlockEntity::new, ModBlocks.FERMENTING_BARREL.get()
            ).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITES.register(eventBus);
    }
}

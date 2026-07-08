package net.magentagt.javanesedelight.common.block.entity;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.magentagt.javanesedelight.common.recipe.FermentingBarrelRecipe;
import net.magentagt.javanesedelight.common.recipe.FermentingBarrelRecipeInput;
import net.magentagt.javanesedelight.common.registry.ModBlockEntities;
import net.magentagt.javanesedelight.common.registry.ModRecipes;
import net.magentagt.javanesedelight.common.screen.custom.FermentingBarrelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FermentingBarrelBlockEntity extends BlockEntity implements MenuProvider, RecipeCraftingHolder {

    public static final int RESULT_SLOT = 4;
    public static final int INVENTORY_SIZE = RESULT_SLOT + 1;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    private final Object2IntOpenHashMap<ResourceLocation> usedRecipeTracker;

    public final ItemStackHandler itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 5;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public FermentingBarrelBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FERMENTING_BARREL_BE.get(), pos, blockState);

        this.usedRecipeTracker = new Object2IntOpenHashMap<>();

        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> FermentingBarrelBlockEntity.this.progress;
                    case 1 -> FermentingBarrelBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0: FermentingBarrelBlockEntity.this.progress = value;
                    case 1: FermentingBarrelBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void clearContents() {
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public NonNullList<ItemStack> drops() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            drops.add(itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, drops);

        return drops;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.javanesedelight.fermenting_barrel");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory itemHandler, @NotNull Player player) {
        return new FermentingBarrelMenu(i, itemHandler, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        tag.put("itemHandler", itemHandler.serializeNBT(registries));
        tag.putInt("fermenting_barrel.progress", progress);
        tag.putInt("fermenting_barrel.max_progress", maxProgress);

        CompoundTag compoundRecipes = new CompoundTag();
        this.usedRecipeTracker.forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
        tag.put("recipesUsed", compoundRecipes);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);

        itemHandler.deserializeNBT(registries, tag.getCompound("itemHandler"));
        progress = tag.getInt("fermenting_barrel.progress");
        maxProgress = tag.getInt("fermenting_barrel.max_progress");

        CompoundTag compoundRecipes = tag.getCompound("recipesUsed");
        for (String key : compoundRecipes.getAllKeys()) {
            this.usedRecipeTracker.put(ResourceLocation.parse(key), compoundRecipes.getInt(key));
        }
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (level.isClientSide) {
            return;
        }

        if (canExecuteRecipe()) {
            Optional<RecipeHolder<FermentingBarrelRecipe>> recipe = getCurrentRecipe();
            maxProgress = recipe.get().value().fermentingTime();
            progress++;
            setChanged(level, blockPos, blockState);

            if (progress >= maxProgress) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void craftItem() {
        Optional<RecipeHolder<FermentingBarrelRecipe>> recipe = getCurrentRecipe();

        ItemStack output = recipe.get().value().output();

        for (int i = 0; i < 4; i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                itemHandler.extractItem(i, 1, false);
            }
        }

        itemHandler.setStackInSlot(RESULT_SLOT, new ItemStack(output.getItem(),
                itemHandler.getStackInSlot(RESULT_SLOT).getCount() + output.getCount()));

        this.setRecipeUsed(recipe.get());
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 3600;
    }

    private boolean canExecuteRecipe() {
        Optional<RecipeHolder<FermentingBarrelRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        ItemStack output = recipe.get().value().output();

        return canInsertAmountIntoResultSlot(output.getCount()) && canInsertItemIntoResultSlot(output);
    }

    private Optional<RecipeHolder<FermentingBarrelRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.FERMENTING_BARREL_TYPE.get(), new FermentingBarrelRecipeInput(List.of(
                        itemHandler.getStackInSlot(0),
                        itemHandler.getStackInSlot(1),
                        itemHandler.getStackInSlot(2),
                        itemHandler.getStackInSlot(3)
                )), level);
    }

    private boolean canInsertItemIntoResultSlot(ItemStack output) {
        ItemStack resultSlotStack = itemHandler.getStackInSlot(RESULT_SLOT);

        return resultSlotStack.isEmpty() ||
                resultSlotStack.is(output.getItem());
    }

    private boolean canInsertAmountIntoResultSlot(int count) {
        ItemStack resultSlotStack = itemHandler.getStackInSlot(RESULT_SLOT);

        int maxCount = resultSlotStack.isEmpty() ? 64 :
                resultSlotStack.getMaxStackSize();
        int currentCount = resultSlotStack.getCount();

        return maxCount >= currentCount + count;
    }

    public void setRecipeUsed(RecipeHolder<?> recipe) {
        if (recipe != null) {
            ResourceLocation recipeId = recipe.id();
            this.usedRecipeTracker.addTo(recipeId, 1);
        }
    }

    public RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    public void awardUsedRecipes(Player player, List<ItemStack> items) {
        if (level.isClientSide) {
            return;
        }

        List<RecipeHolder<?>> usedRecipes = this.getUsedRecipesAndPopExperience(player.level(), player.position());
//        player.awardRecipes(usedRecipes);
        this.usedRecipeTracker.clear();
    }

    public List<RecipeHolder<?>> getUsedRecipesAndPopExperience(Level level, Vec3 position) {
        List<RecipeHolder<?>> list = Lists.newArrayList();
        ObjectIterator iterator = this.usedRecipeTracker.object2IntEntrySet().iterator();

        while(iterator.hasNext()) {
            Object2IntMap.Entry<ResourceLocation> entry = (Object2IntMap.Entry) iterator.next();
            level.getRecipeManager().byKey((ResourceLocation)entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                splitAndSpawnExperience((ServerLevel)level, position, entry.getIntValue(), ((FermentingBarrelRecipe)recipe.value()).experience());
            });
        }

        return list;
    }

    private void splitAndSpawnExperience(ServerLevel level, Vec3 position, int craftedAmount, float experience) {
        int expTotal = Mth.floor((float) craftedAmount * experience);
        float expFraction = Mth.frac((float) craftedAmount * experience);

        if (expFraction != 0.0F && Math.random() < (double)expFraction) {
            ++expTotal;
        }

        ExperienceOrb.award(level, position, expTotal);
    }


    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

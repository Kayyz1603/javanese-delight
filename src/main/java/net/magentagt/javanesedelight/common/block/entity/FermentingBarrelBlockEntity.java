package net.magentagt.javanesedelight.common.block.entity;

import net.magentagt.javanesedelight.common.recipe.FermentingBarrelRecipe;
import net.magentagt.javanesedelight.common.recipe.FermentingBarrelRecipeInput;
import net.magentagt.javanesedelight.common.registry.ModBlockEntities;
import net.magentagt.javanesedelight.common.registry.ModItems;
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
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class FermentingBarrelBlockEntity extends BlockEntity implements MenuProvider {

    public static final int RESULT_SLOT = 4;
    public static final int INVENTORY_SIZE = RESULT_SLOT + 1;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72;

    public final ItemStackHandler itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
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

    public void drops() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            drops.add(itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, drops);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.javanesedelight.fermenting_barrel");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory itemHandler, Player player) {
        return new FermentingBarrelMenu(i, itemHandler, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("itemHandler", itemHandler.serializeNBT(registries));
        tag.putInt("growth_chamber.progress", progress);
        tag.putInt("growth_chamber.max_progress", maxProgress);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        itemHandler.deserializeNBT(registries, tag.getCompound("itemHandler"));
        progress = tag.getInt("growth_chamber.progress");
        maxProgress = tag.getInt("growth_chamber.max_progress");
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (canExecuteRecipe()) {
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
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 72;
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


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

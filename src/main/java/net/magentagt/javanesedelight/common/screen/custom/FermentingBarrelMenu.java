package net.magentagt.javanesedelight.common.screen.custom;

import net.magentagt.javanesedelight.common.block.entity.FermentingBarrelBlockEntity;
import net.magentagt.javanesedelight.common.block.entity.container.FermentingBarrelResultSlot;
import net.magentagt.javanesedelight.common.registry.ModBlocks;
import net.magentagt.javanesedelight.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FermentingBarrelMenu extends AbstractContainerMenu {
    public final FermentingBarrelBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public FermentingBarrelMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public FermentingBarrelMenu(int containerId, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.FERMENTING_BARREL_MENU.get(), containerId);
        this.blockEntity = ((FermentingBarrelBlockEntity) blockEntity);
        this.level = inv.player.level();
        this.data = data;

        // Input slots
        int startX = 38;
        int startY = 26;
        int slotSize = 18;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                this.addSlot(new SlotItemHandler(
                        this.blockEntity.itemHandler,
                        (row * 2) + col,
                        startX + (col * slotSize),
                        startY + (row * slotSize)));
            }
        }

        // Result slot
        this.addSlot(new FermentingBarrelResultSlot(inv.player, this.blockEntity, this.blockEntity.itemHandler, 4, 116, 35));


        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(data);
    }


    private static final int TE_INVENTORY_SLOT_COUNT = 5;

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        int indexResult = 4;
        int playerInvStartIndex = indexResult + 1;
        int playerInvEndIndex = playerInvStartIndex + 36;

        Slot slot = this.slots.get(index);
        ItemStack slotStackCopy = ItemStack.EMPTY;

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            slotStackCopy = slotStack.copy();

            if (index == indexResult) {
                if (!this.moveItemStackTo(slotStack, playerInvStartIndex, playerInvEndIndex, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < indexResult) {
                if (!this.moveItemStackTo(slotStack, playerInvStartIndex, playerInvEndIndex, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, indexResult, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == slotStackCopy.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return slotStackCopy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.FERMENTING_BARREL.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int l = 0; l < 9; l++) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledArrowProgress() {
        float progress = (float) data.get(0);
        float maxProgress = (float) data.get(1);
        float arrowPixelSize = 24.0F;

        return maxProgress != 0 && progress != 0 ? Math.max(2, Math.round(progress * arrowPixelSize / maxProgress)) : 0;
    }
}

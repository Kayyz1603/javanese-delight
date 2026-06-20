package net.magentagt.javanesedelight.common.screen.custom;

import net.magentagt.javanesedelight.common.block.entity.FermentingBarrelBlockEntity;
import net.magentagt.javanesedelight.common.registry.ModBlocks;
import net.magentagt.javanesedelight.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class FermentingBarrelMenu extends AbstractContainerMenu {
    public final FermentingBarrelBlockEntity blockEntity;
    private final Level level;

    public FermentingBarrelMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public FermentingBarrelMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(ModMenuTypes.FERMENTING_BARREL_MENU.get(), containerId);
        this.blockEntity = ((FermentingBarrelBlockEntity) blockEntity);
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // Input slots
        int startX = 38;
        int startY = 26;
        int slotSize = 18;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                this.addSlot(new SlotItemHandler(
                        this.blockEntity.inventory,
                        (row * 2) + col,
                        startX + (col * slotSize),
                        startY + (row * slotSize)));
            }
        }

        // Result slot
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 4, 116, 35));

    }


    private static final int TE_INVENTORY_SLOT_COUNT = 5;

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
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
}

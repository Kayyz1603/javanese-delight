package net.magentagt.javanesedelight.common.block.entity.container;

import net.magentagt.javanesedelight.common.block.entity.FermentingBarrelBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class FermentingBarrelResultSlot extends SlotItemHandler {
    private int removeCount;

    public FermentingBarrelResultSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Nonnull
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }

        return super.remove(amount);
    }

    public void onTake(Player thePlayer, ItemStack stack) {
        super.onTake(thePlayer, stack);
    }

    protected void onQuickCraft(ItemStack stack, int amount) {
        this.removeCount += amount;
    }
}

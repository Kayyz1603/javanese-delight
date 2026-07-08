package net.magentagt.javanesedelight.common.block.entity.container;

import net.magentagt.javanesedelight.common.block.entity.FermentingBarrelBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class FermentingBarrelResultSlot extends SlotItemHandler {
    private final FermentingBarrelBlockEntity tileEntity;
    private final Player player;
    private int removeCount;

    public FermentingBarrelResultSlot(Player player, FermentingBarrelBlockEntity tile, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.player = player;
        this.tileEntity = tile;
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
        this.checkTakeAchievements(stack);
        super.onTake(thePlayer, stack);
    }

    protected void onQuickCraft(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.checkTakeAchievements(stack);
    }

    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
        if (!this.player.level().isClientSide) {
            this.tileEntity.awardUsedRecipes(this.player, Collections.emptyList());
        }

        this.removeCount = 0;
    }
}

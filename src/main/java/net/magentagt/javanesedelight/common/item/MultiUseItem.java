package net.magentagt.javanesedelight.common.item;

import net.magentagt.javanesedelight.common.registry.ModDataComponents;
import net.magentagt.javanesedelight.common.registry.ModItems;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MultiUseItem extends Item {

    private final int maxUses;
    private final Item finalRemainder;

    public MultiUseItem(Properties properties, int maxUses, Item finalRemainder) {
        super(properties);
        this.maxUses = maxUses;
        this.finalRemainder = finalRemainder;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        if (itemStack.get(ModDataComponents.USES_LEFT) == null) {
            ItemStack remaining = new ItemStack(itemStack.getItem(), 1);
            remaining.set(ModDataComponents.USES_LEFT, this.maxUses - 1);
            return remaining;
        } else {
            int usesLeft = itemStack.get(ModDataComponents.USES_LEFT);

            if (usesLeft <= 1) {
                return new ItemStack(this.finalRemainder, 1);
            } else {
                ItemStack remaining = new ItemStack(itemStack.getItem(), 1);
                remaining.set(ModDataComponents.USES_LEFT, usesLeft - 1);
                return remaining;
            }
        }

    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.has(ModDataComponents.USES_LEFT);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (stack.has(ModDataComponents.USES_LEFT)) {
            return Math.round((float)stack.get(ModDataComponents.USES_LEFT) * 13.0F / (float)this.maxUses);
        }

        return 0;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if (stack.has(ModDataComponents.USES_LEFT)) {
            float f = Math.max(0.0F, ((float)stack.get(ModDataComponents.USES_LEFT) / (float)this.maxUses));
            return Mth.hsvToRgb(f / 3.0F, 1.0F,1.0F);
        }

        return Mth.hsvToRgb(1.0F / 3.0F, 1.0F,1.0F);
    }
}

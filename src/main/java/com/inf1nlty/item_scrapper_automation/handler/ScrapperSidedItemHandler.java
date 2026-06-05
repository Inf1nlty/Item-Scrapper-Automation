package com.inf1nlty.item_scrapper_automation.handler;

import com.keerdm.item_scrapper.blocks.ScrapperBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public final class ScrapperSidedItemHandler implements IItemHandler {

    private final ScrapperBlockEntity blockEntity;
    private final ScrapperSidedAccess access;

    public ScrapperSidedItemHandler(ScrapperBlockEntity blockEntity, ScrapperSidedAccess access) {
        this.blockEntity = blockEntity;
        this.access = access;
    }

    @Override
    public int getSlots() {
        return switch (access) {
            case INPUT -> 1;
            case OUTPUT -> output().getSlots();
            case COMBINED -> 1 + output().getSlots();
        };
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        if (access == ScrapperSidedAccess.INPUT) {
            return slot == 0 ? input().getStackInSlot(0) : ItemStack.EMPTY;
        }
        if (access == ScrapperSidedAccess.OUTPUT) {
            return isOutputSlot(slot) ? output().getStackInSlot(slot) : ItemStack.EMPTY;
        }
        if (slot == 0) {
            return input().getStackInSlot(0);
        }
        int outputSlot = slot - 1;
        return isOutputSlot(outputSlot) ? output().getStackInSlot(outputSlot) : ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (access == ScrapperSidedAccess.INPUT && slot == 0) {
            return input().insertItem(0, stack, simulate);
        }
        if (access == ScrapperSidedAccess.COMBINED && slot == 0) {
            return input().insertItem(0, stack, simulate);
        }
        return stack;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        if (access == ScrapperSidedAccess.OUTPUT && isOutputSlot(slot)) {
            return output().extractItem(slot, amount, simulate);
        }
        if (access == ScrapperSidedAccess.COMBINED) {
            int outputSlot = slot - 1;
            if (isOutputSlot(outputSlot)) {
                return output().extractItem(outputSlot, amount, simulate);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        if (access == ScrapperSidedAccess.INPUT && slot == 0) {
            return input().getSlotLimit(0);
        }
        if (access == ScrapperSidedAccess.OUTPUT && isOutputSlot(slot)) {
            return output().getSlotLimit(slot);
        }
        if (access == ScrapperSidedAccess.COMBINED) {
            if (slot == 0) {
                return input().getSlotLimit(0);
            }
            int outputSlot = slot - 1;
            if (isOutputSlot(outputSlot)) {
                return output().getSlotLimit(outputSlot);
            }
        }
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (access == ScrapperSidedAccess.INPUT && slot == 0) {
            return input().isItemValid(0, stack);
        }
        if (access == ScrapperSidedAccess.COMBINED && slot == 0) {
            return input().isItemValid(0, stack);
        }
        return false;
    }

    private ItemStackHandler input() {
        return blockEntity.getInputItemHandler();
    }

    private ItemStackHandler output() {
        return blockEntity.getOutputItemHandler();
    }

    private boolean isOutputSlot(int slot) {
        return slot >= 0 && slot < output().getSlots();
    }
}

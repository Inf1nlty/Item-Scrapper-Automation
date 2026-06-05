package com.inf1nlty.item_scrapper_automation.mixin;

import com.inf1nlty.item_scrapper_automation.handler.ScrapperSidedItemHandler;
import com.inf1nlty.item_scrapper_automation.handler.ScrapperSidedAccess;
import com.keerdm.item_scrapper.blocks.ScrapperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mixin(value = ScrapperBlockEntity.class, remap = false)
public abstract class ScrapperBlockEntityMixin extends BlockEntity {

    @Unique
    private LazyOptional<IItemHandler> item_scrapper_automation$inputHandler;
    @Unique
    private LazyOptional<IItemHandler> item_scrapper_automation$outputHandler;
    @Unique
    private LazyOptional<IItemHandler> item_scrapper_automation$combinedHandler;

    protected ScrapperBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.DOWN) {
                return item_scrapper_automation$output().cast();
            }
            if (side != null && side.getAxis().isHorizontal()) {
                return item_scrapper_automation$input().cast();
            }
            if (side == null) {
                return item_scrapper_automation$combined().cast();
            }
            return LazyOptional.empty();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        item_scrapper_automation$invalidateHandlers();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        item_scrapper_automation$inputHandler = null;
        item_scrapper_automation$outputHandler = null;
        item_scrapper_automation$combinedHandler = null;
    }

    @Unique
    private LazyOptional<IItemHandler> item_scrapper_automation$input() {
        if (item_scrapper_automation$inputHandler == null) {
            item_scrapper_automation$inputHandler = LazyOptional.of(() ->
                    new ScrapperSidedItemHandler(item_scrapper_automation$self(), ScrapperSidedAccess.INPUT));
        }
        return item_scrapper_automation$inputHandler;
    }

    @Unique
    private LazyOptional<IItemHandler> item_scrapper_automation$output() {
        if (item_scrapper_automation$outputHandler == null) {
            item_scrapper_automation$outputHandler = LazyOptional.of(() ->
                    new ScrapperSidedItemHandler(item_scrapper_automation$self(), ScrapperSidedAccess.OUTPUT));
        }
        return item_scrapper_automation$outputHandler;
    }

    @Unique
    private LazyOptional<IItemHandler> item_scrapper_automation$combined() {
        if (item_scrapper_automation$combinedHandler == null) {
            item_scrapper_automation$combinedHandler = LazyOptional.of(() ->
                    new ScrapperSidedItemHandler(item_scrapper_automation$self(), ScrapperSidedAccess.COMBINED));
        }
        return item_scrapper_automation$combinedHandler;
    }

    @Unique
    private void item_scrapper_automation$invalidateHandlers() {
        if (item_scrapper_automation$inputHandler != null) {
            item_scrapper_automation$inputHandler.invalidate();
        }
        if (item_scrapper_automation$outputHandler != null) {
            item_scrapper_automation$outputHandler.invalidate();
        }
        if (item_scrapper_automation$combinedHandler != null) {
            item_scrapper_automation$combinedHandler.invalidate();
        }
        item_scrapper_automation$inputHandler = null;
        item_scrapper_automation$outputHandler = null;
        item_scrapper_automation$combinedHandler = null;
    }

    @Unique
    private ScrapperBlockEntity item_scrapper_automation$self() {
        return (ScrapperBlockEntity) (Object) this;
    }
}

package com.inf1nlty.item_scrapper_automation.handler;

import com.inf1nlty.item_scrapper_automation.ModConfigHandler;
import com.keerdm.item_scrapper.ItemScrapper;
import com.keerdm.item_scrapper.blocks.ScrapperBlock;
import com.keerdm.item_scrapper.blocks.ScrapperBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;

public final class ScrapperAutomationHooks {

    public static boolean tryHandleDeployerHammer(DeployerFakePlayer player, BlockPos targetPos, Vec3 movement, Enum<?> mode) {
        if (!"PUNCH".equals(mode.name()) || movement.y >= -0.5D) {
            return false;
        }

        ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.is(ItemScrapper.SCRAPPING_HAMMER.get())) {
            return false;
        }

        ServerLevel level = player.serverLevel();
        ScrapperBlockEntity scrapper = findScrapper(level, targetPos);
        if (scrapper == null) {
            return false;
        }

        if (scrapper.getInputItemHandler().getStackInSlot(0).isEmpty()) {
            return true;
        }
        if (isOutputFull(scrapper.getOutputItemHandler())) {
            return true;
        }

        scrapper.addHit(ModConfigHandler.deployerHitPower, player);
        return true;
    }

    private static ScrapperBlockEntity findScrapper(ServerLevel level, BlockPos targetPos) {
        BlockState state = level.getBlockState(targetPos);
        if (!(state.getBlock() instanceof ScrapperBlock)) {
            return null;
        }

        BlockPos mainPos = targetPos;
        if (!state.getValue(ScrapperBlock.IS_MAIN)) {
            Direction facing = state.getValue(ScrapperBlock.FACING);
            mainPos = targetPos.relative(facing.getOpposite());
        }

        BlockEntity blockEntity = level.getBlockEntity(mainPos);
        return blockEntity instanceof ScrapperBlockEntity scrapper ? scrapper : null;
    }

    private static boolean isOutputFull(ItemStackHandler output) {
        for (int slot = 0; slot < output.getSlots(); slot++) {
            ItemStack stack = output.getStackInSlot(slot);
            if (stack.isEmpty()) {
                return false;
            }
            int limit = Math.min(stack.getMaxStackSize(), output.getSlotLimit(slot));
            if (stack.getCount() < limit) {
                return false;
            }
        }
        return true;
    }
}

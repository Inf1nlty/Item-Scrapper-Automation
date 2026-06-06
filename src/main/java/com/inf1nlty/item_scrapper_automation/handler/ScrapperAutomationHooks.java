package com.inf1nlty.item_scrapper_automation.handler;

import com.inf1nlty.item_scrapper_automation.ItemScrapperAutomationConfig;
import com.keerdm.item_scrapper.ItemScrapper;
import com.keerdm.item_scrapper.blocks.ScrapperBlock;
import com.keerdm.item_scrapper.blocks.ScrapperBlockEntity;
import com.keerdm.item_scrapper.data.ScrapperDataManager;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

        if (!canHammerScrapper(scrapper) || shouldStopBeforeHammerBreak(heldItem)) {
            return true;
        }

        damageHammer(heldItem, player);
        scrapper.scrapperLogic.addHit(ItemScrapperAutomationConfig.deployerHitPower, scrapper.getLevel());
        return true;
    }

    public static boolean shouldStartDeployerHammer(Level level, BlockPos blockPos, BlockState state, DeployerFakePlayer player) {
        if (!(level instanceof ServerLevel serverLevel) || player == null) {
            return true;
        }

        ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.is(ItemScrapper.SCRAPPING_HAMMER.get())) {
            return true;
        }

        if (!state.hasProperty(BlockStateProperties.FACING)) {
            return true;
        }

        Direction facing = state.getValue(BlockStateProperties.FACING);
        if (facing != Direction.DOWN) {
            return true;
        }

        ScrapperBlockEntity scrapper = findScrapper(serverLevel, blockPos.relative(facing, 2));
        return scrapper == null || canHammerScrapper(scrapper) && !shouldStopBeforeHammerBreak(heldItem);
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

    private static boolean canHammerScrapper(ScrapperBlockEntity scrapper) {
        ItemStack input = scrapper.getInputItemHandler().getStackInSlot(0);
        return isScrappable(input) && !isOutputFull(scrapper.getOutputItemHandler());
    }

    private static boolean isScrappable(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return ScrapperDataManager.getConfigForItem(itemId, stack.getTag()) != null;
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

    private static void damageHammer(ItemStack hammer, DeployerFakePlayer player) {
        int durabilityCost = ItemScrapperAutomationConfig.deployerHammerDurabilityCost;
        if (durabilityCost <= 0 || !hammer.isDamageableItem()) {
            return;
        }

        hammer.hurtAndBreak(durabilityCost, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(InteractionHand.MAIN_HAND));
    }

    private static boolean shouldStopBeforeHammerBreak(ItemStack hammer) {
        int durabilityCost = ItemScrapperAutomationConfig.deployerHammerDurabilityCost;
        if (!ItemScrapperAutomationConfig.deployerStopBeforeHammerBreak || durabilityCost <= 0 || !hammer.isDamageableItem()) {
            return false;
        }

        int remainingDurability = hammer.getMaxDamage() - hammer.getDamageValue();
        return durabilityCost >= remainingDurability;
    }

}

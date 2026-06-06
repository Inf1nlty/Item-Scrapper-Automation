package com.inf1nlty.item_scrapper_automation.mixin;

import com.inf1nlty.item_scrapper_automation.handler.ScrapperAutomationHooks;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DeployerBlockEntity.class, remap = false)
public abstract class DeployerBlockEntityMixin extends BlockEntity {

    @Shadow
    protected DeployerFakePlayer player;

    protected DeployerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/kinetics/deployer/DeployerBlockEntity;startFistBump(Lnet/minecraft/core/Direction;)Z"), cancellable = true, remap = false)
    private void item_scrapper_automation$skipIdleScrapperHammer(CallbackInfo ci) {
        if (!ScrapperAutomationHooks.shouldStartDeployerHammer(level, worldPosition, getBlockState(), player)) {
            ci.cancel();
        }
    }
}

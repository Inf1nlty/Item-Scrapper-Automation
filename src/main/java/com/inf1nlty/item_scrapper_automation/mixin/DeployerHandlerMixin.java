package com.inf1nlty.item_scrapper_automation.mixin;

import com.inf1nlty.item_scrapper_automation.handler.ScrapperAutomationHooks;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.content.kinetics.deployer.DeployerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DeployerHandler.class, remap = false)
public abstract class DeployerHandlerMixin {

    @Inject(method = "activate", at = @At("HEAD"), cancellable = true, remap = false)
    private static void item_scrapper_automation$hammerScrapper(DeployerFakePlayer player, Vec3 vec, BlockPos clickedPos, Vec3 extensionVector, @Coerce Enum<?> mode, CallbackInfo ci) {
        if (ScrapperAutomationHooks.tryHandleDeployerHammer(player, clickedPos, extensionVector, mode)) {
            ci.cancel();
        }
    }
}

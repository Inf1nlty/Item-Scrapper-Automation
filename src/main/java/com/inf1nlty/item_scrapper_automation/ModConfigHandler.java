package com.inf1nlty.item_scrapper_automation;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ItemScrapperAutomationAddon.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)

public final class ModConfigHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue DEPLOYER_HIT_POWER = BUILDER
            .comment("Hit progress added by one valid Deployer hammer stroke. The default matches a fully charged manual scrapping hammer hit.")
            .defineInRange("deployerHitPower", 1.0D, 0.01D, 100.0D);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static float deployerHitPower;

    @SubscribeEvent
    static void onConfigLoad(ModConfigEvent event) {
        deployerHitPower = DEPLOYER_HIT_POWER.get().floatValue();
    }
}

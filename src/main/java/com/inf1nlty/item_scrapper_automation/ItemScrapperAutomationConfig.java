package com.inf1nlty.item_scrapper_automation;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ItemScrapperAutomationAddon.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)

public final class ItemScrapperAutomationConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue DEPLOYER_HIT_POWER = BUILDER
            .comment("Hit progress added by one valid Deployer hammer stroke. The default matches a fully charged manual scrapping hammer hit.")
            .defineInRange("deployerHitPower", 1.0D, 0.01D, 100.0D);

    private static final ForgeConfigSpec.IntValue DEPLOYER_HAMMER_DURABILITY_COST = BUILDER
            .comment("Scrapping Hammer durability consumed by one valid Deployer hammer stroke. Set to 0 to disable durability consumption for automated hits.")
            .defineInRange("deployerHammerDurabilityCost", 1, 0, 1000);

    private static final ForgeConfigSpec.BooleanValue DEPLOYER_STOP_BEFORE_HAMMER_BREAK = BUILDER
            .comment("When true, Deployers stop automated scrapping if the next configured durability cost would break the Scrapping Hammer.")
            .define("deployerStopBeforeHammerBreak", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static float deployerHitPower;
    public static int deployerHammerDurabilityCost;
    public static boolean deployerStopBeforeHammerBreak;

    @SubscribeEvent
    static void onConfigLoad(ModConfigEvent event) {
        deployerHitPower = DEPLOYER_HIT_POWER.get().floatValue();
        deployerHammerDurabilityCost = DEPLOYER_HAMMER_DURABILITY_COST.get();
        deployerStopBeforeHammerBreak = DEPLOYER_STOP_BEFORE_HAMMER_BREAK.get();
    }

}

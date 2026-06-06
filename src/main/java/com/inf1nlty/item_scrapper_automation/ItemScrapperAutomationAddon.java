package com.inf1nlty.item_scrapper_automation;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ItemScrapperAutomationAddon.MODID)
public final class ItemScrapperAutomationAddon {

    public static final String MODID = "item_scrapper_automation";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ItemScrapperAutomationAddon(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, ItemScrapperAutomationConfig.SPEC);
    }
}

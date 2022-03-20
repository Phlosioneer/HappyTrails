// 15.2 -Happy Trails
package com.mactso.happytrails;

import com.mactso.happytrails.config.HappyTrailsConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Main.MODID)
public class Main {

	public static final String MODID = "happytrails";

	public Main() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, HappyTrailsConfig.COMMON_SPEC);
	}
}

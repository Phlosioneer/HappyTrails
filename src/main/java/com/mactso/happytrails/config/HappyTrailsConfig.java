package com.mactso.happytrails.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import com.mactso.happytrails.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HappyTrailsConfig {

	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	public static final BlockSpeedConfig BLOCK_SPEED;
	public static final ForgeConfigSpec BLOCK_SPEED_SUB_SPEC;

	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();

		var subSpecPair = new ForgeConfigSpec.Builder().configure(BlockSpeedConfig::new);
		BLOCK_SPEED_SUB_SPEC = subSpecPair.getRight();
		BLOCK_SPEED = subSpecPair.getLeft();
	}

	// public static int aHappyTrailSpeed;
	public static boolean aParticlesOn;
	private static Map<String, BlockSpeedInfo> trailBlocks = new HashMap<>();

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfigEvent configEvent) {
		if (configEvent.getConfig().getSpec() == HappyTrailsConfig.COMMON_SPEC) {
			bakeConfig();
		}
	}

	public static void bakeConfig() {
		trailBlocks = COMMON.getBlockSpeeds();
		aParticlesOn = COMMON.particlesOn.get();
		if (COMMON.showConfigDebugInfo.get()) {
			logDebugInfo();
		}
	}

	public static BlockSpeedInfo getBlockSpeed(ResourceLocation block) {
		return getBlockSpeed(block.toString());
	}

	public static BlockSpeedInfo getBlockSpeed(String block) {
		if (trailBlocks.containsKey(block)) {
			return trailBlocks.get(block);
		}
		for (var pair : trailBlocks.entrySet()) {
			if (block.matches(pair.getKey())) {
				return pair.getValue();
			}
		}
		return null;
	}

	public static void logDebugInfo() {
		var logger = LogManager.getLogger();

		logger.info("Current block speed config:");
		for (var pair : trailBlocks.entrySet()) {
			logger.info("\t\"{}\": {}", pair.getKey(), pair.getValue());
		}

		logger.info("Effect for each registered block:");
		ForgeRegistries.BLOCKS.getKeys()
				.stream()
				.sorted()
				.forEachOrdered(block -> {
					var info = getBlockSpeed(block);
					if (info == null) {
						logger.info("\t\"{}\": No effects", block);
					} else {
						logger.info("\t\"{}\": {}", block, info);
					}
				});

		logger.info("Blocks that match multiple patterns:");
		var badBlocks = new HashMap<String, ArrayList<String>>();
		var currentMatches = new ArrayList<String>(1);
		for (var block : ForgeRegistries.BLOCKS) {
			currentMatches.clear();
			var name = block.getRegistryName().toString();
			for (var pattern : trailBlocks.keySet()) {
				if (name.matches(pattern)) {
					currentMatches.add(pattern);
				}
			}
			if (currentMatches.size() > 1) {
				badBlocks.put(name, new ArrayList<>(currentMatches));
			}
		}
		badBlocks.keySet()
				.stream()
				.sorted()
				.forEachOrdered(block -> logger.info("\tBlock {} matches the patterns: {}", block, badBlocks.get(block)));
		if (badBlocks.size() == 0) {
			logger.info("\tNo blocks match multiple patterns. Yay!");
		}
	}
}

package com.mactso.happytrails.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import com.electronwill.nightconfig.core.CommentedConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Common {

	public final BooleanValue particlesOn;
	public final BooleanValue showConfigDebugInfo;
	private final ConfigValue<List<? extends CommentedConfig>> trailBlocks;

	public Common(ForgeConfigSpec.Builder builder) {

		particlesOn = builder
				.comment(BlockSpeedConfig.TOP_OF_FILE_COMMENTS)
				.define("particlesOn", () -> true);

		showConfigDebugInfo = builder.comment("When true, HappyTrails will:",
				"1) print out all the configured speed values",
				"2) print out the speed for every block in the registry",
				"3) print out any blocks that match multiple configured patterns")
				.define("printDebugInfo", false);

		trailBlocks = builder.defineList("BlockSpeeds", BlockSpeedConfig::getDefaultBlockPatterns, e -> {
			LogManager.getLogger().info(e);
			return e instanceof CommentedConfig;
		});
	}

	public Map<String, BlockSpeedInfo> getBlockSpeeds() {
		var ret = new HashMap<String, BlockSpeedInfo>();
		var logger = LogManager.getLogger();
		var configName = "HappyTrails.Config.Common";
		boolean hasWarnedForFile = false;
		var list = trailBlocks.get();
		for (var i = 0; i < list.size(); i++) {
			var element = list.get(i);

			HappyTrailsConfig.BLOCK_SPEED_SUB_SPEC.acceptConfig(element);
			var useVanilla = HappyTrailsConfig.BLOCK_SPEED.useVanillaEffect.get();
			var speed = HappyTrailsConfig.BLOCK_SPEED.speed.get();

			// Correct the config if needed
			if (useVanilla && speed < -10) {
				if (!hasWarnedForFile) {
					logger.warn("Configuration file {} is not correct. Correcting", configName);
					hasWarnedForFile = true;
				}
				logger.warn("Incorrect key BLOCK_SPEED[{}].speed was corrected from {} to its minimum value, -10, because {}=true.",
						i, speed, BlockSpeedConfig.USE_VANILLA_EFFECT);
				HappyTrailsConfig.BLOCK_SPEED.speed.set(-10);
				speed = -10;
			} else if (useVanilla && speed > 10) {
				if (!hasWarnedForFile) {
					logger.warn("Configuration file {} is not correct. Correcting", configName);
					hasWarnedForFile = true;
				}
				logger.warn("Incorrect key BLOCK_SPEED[{}].speed was corrected from {} to its maximum value, 10, because {}=true.",
						i, speed, BlockSpeedConfig.USE_VANILLA_EFFECT);
				HappyTrailsConfig.BLOCK_SPEED.speed.set(10);
				speed = 10;
			}

			var blockInfo = new BlockSpeedInfo(useVanilla, speed);
			for (var pattern : HappyTrailsConfig.BLOCK_SPEED.blockPatterns.get()) {
				pattern = pattern.replaceAll("\\*", ".*");
				if (!pattern.contains(":")) {
					pattern = "minecraft:" + pattern;
				}
				if (ret.containsKey(pattern)) {
					var oldBlockInfo = ret.get(pattern);
					if (blockInfo.equals(oldBlockInfo)) {
						logger.warn("Multiple instances of the pattern \"{}\", but they have the same speed config so it doesn't matter.", pattern);
					} else {
						logger.warn("Multiple instances of the pattern \"{}\", using the first one found: {}", pattern, oldBlockInfo);
					}
				} else {
					ret.put(pattern, blockInfo);
				}
			}
		}

		return ret;
	}
}
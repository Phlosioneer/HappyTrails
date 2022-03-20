package com.mactso.happytrails.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class BlockSpeedConfig {
	public static final String[] TOP_OF_FILE_COMMENTS = new String[]{
			"Docs for useVanillaEffect:",
			"Whether to use the vanilla speed effects (Speed I-X, Slowness I-X) or happytrails effects for these blocks.",
			"Speed <level> speeds up the player by (20% * level), up to 3x faster than the base speed.",
			"Slowness <level> slows down the player by (15% * level), down to making motion impossible at level 7 or higher.",
			"The happytrails effect speeds up or slows down the player by level%. So level 200 is 2x speed, while level -70 is",
			"30% speed (70% slower than normal).",
			"",
			"Some useful values:",
			"Player sneaking: -69%; Player walking: 0%; Slowest horse: 13%; Player sprinting: 50%; Dolphin's Grace: 127%;",
			"Fastest horse: 237%; Dolphin: 1100%",
			"",
			""
	};

	public static final String USE_VANILLA_EFFECT = "useVanillaEffect";
	private static final String SPEED = "speed";
	private static final String BLOCK_PATTERNS = "blocks";

	public final BooleanValue useVanillaEffect;
	public final IntValue speed;
	public final ConfigValue<List<? extends String>> blockPatterns;

	public BlockSpeedConfig(ForgeConfigSpec.Builder builder) {
		useVanillaEffect = builder.comment("See the comment on useVanillaEffect at the top of the file.")
				.define(USE_VANILLA_EFFECT, false);
		speed = builder.comment("The size of the speedup effect. If useVanillaEffect=true, this will be limited to between -10 and 10.")
				.defineInRange(SPEED, 11, -100, 6400);
		blockPatterns = builder.comment("The blocks affected by this speedup effect, in namespace:block_name format. Glob * patterns",
				"are allowed in the namespace or block_name parts, but cannot match ':'. If no namespace is provided, defaults to minecraft:.")
				.defineList(BLOCK_PATTERNS, () -> List.of(), e -> e instanceof String);
	}

	public static List<CommentedConfig> getDefaultBlockPatterns() {
		return Arrays.asList(new CommentedConfig[]{
				constBlockConfig(false, -20, new String[]{
						"minecraft:tall_grass",
						"minecraft:snow",
						"minecraft:large_fern"
				}),
				constBlockConfig(false, -10, new String[]{"minecraft:grass"}),
				constBlockConfig(false, -5, new String[]{
						"minecraft:sand",
						"minecraft:gravel",
						"minecraft:red_sand"
				}),
				constBlockConfig(false, -5, new String[]{"minecraft:water"}),
				constBlockConfig(false, 10, new String[]{
						"minecraft:dirt_path",
						"minecraft:*oxidized*copper*"
				}),
				constBlockConfig(false, 20, new String[]{"minecraft:*weathered*copper*"}),
				constBlockConfig(false, 30, new String[]{
						"minecraft:exposed*copper*",
						"minecraft:stone_bricks",
						"minecraft:stone_brick_slab"
				}),
				constBlockConfig(false, 40, new String[]{
						"minecraft:cut_copper*",
						"minecraft:waxed_cut_copper*"
				}),
		});
	}

	private static CommentedConfig constBlockConfig(boolean useVanilla, int speed, String[] patterns) {
		return CommentedConfig.of(() -> {
			var ret = new HashMap<String, Object>();
			ret.put(USE_VANILLA_EFFECT, useVanilla);
			ret.put(SPEED, speed);
			ret.put(BLOCK_PATTERNS, Arrays.asList(patterns));
			return ret;
		}, InMemoryCommentedFormat.defaultInstance());
	}
}
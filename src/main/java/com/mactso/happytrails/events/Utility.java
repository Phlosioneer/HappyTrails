package com.mactso.happytrails.events;

import java.util.UUID;
import com.mactso.happytrails.config.BlockSpeedInfo;
import com.mactso.happytrails.config.HappyTrailsConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;

public class Utility {
	public final static int TWO_SECONDS = 40;

	public static final UUID UUID_HAPPYMODSPEED = UUID.fromString("793fcced-972d-45cb-b385-84694056001a");
	public static final String SPEED_ATTR_NAME = "happytrailsspeed";

	public static BlockSpeedInfo getSpeedAmplifier(LivingEntity le) {
		Block b = le.level.getBlockState(le.blockPosition()).getBlock();
		BlockSpeedInfo info = HappyTrailsConfig.getBlockSpeed(b.getRegistryName());

		if (info == null) { // standing on/in block with no configuration entry
			b = le.level.getBlockState(le.blockPosition().below()).getBlock();
			info = HappyTrailsConfig.getBlockSpeed(b.getRegistryName());
			if (info == null) { // lower block also has no configuration entry
				return null;
			}
		}
		return info;
	}

}

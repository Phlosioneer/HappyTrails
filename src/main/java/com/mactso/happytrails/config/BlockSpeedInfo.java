package com.mactso.happytrails.config;

import java.util.Objects;

public class BlockSpeedInfo {
	public final boolean useVanilla;
	public final int speed;

	public BlockSpeedInfo(boolean useVanilla, int speed) {
		this.useVanilla = useVanilla;
		this.speed = speed;
	}

	@Override
	public String toString() {
		String friendly;
		if (speed == 0) {
			friendly = "None";
		}
		if (useVanilla) {
			if (speed < 0) {
				friendly = "Slowness " + speed;
			} else {
				friendly = "Speed " + speed;
			}
		} else {
			friendly = "Modifier ";
			if (speed > 0) {
				friendly += "+";
			}
			friendly += (speed * 100) + "%";
		}
		return friendly + " (useVanilla: " + useVanilla + ", speed: " + speed + ")";
	}

	@Override
	public int hashCode() {
		return Objects.hash(useVanilla, speed);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BlockSpeedInfo)) {
			return false;
		}
		var other = (BlockSpeedInfo) obj;
		return useVanilla == other.useVanilla && speed == other.speed;
	}
}
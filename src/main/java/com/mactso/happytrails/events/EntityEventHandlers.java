package com.mactso.happytrails.events;

import com.mactso.happytrails.Main;
import com.mactso.happytrails.config.BlockSpeedInfo;
import com.mactso.happytrails.config.HappyTrailsConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class EntityEventHandlers {

	@SubscribeEvent
	public static void playerMove(PlayerTickEvent event) {

		if (!(event.player instanceof ServerPlayer)) {
			return;
		}

		commonMove(event.player);
	}

	@SubscribeEvent
	public static void steedMove(LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();

		if (entity.getFirstPassenger() instanceof ServerPlayer) {
			commonMove(entity);
		}
	}

	public static void commonMove(LivingEntity entity) {
		BlockSpeedInfo info = Utility.getSpeedAmplifier(entity);
		var moveAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
		moveAttribute.removePermanentModifier(Utility.UUID_HAPPYMODSPEED);
		if (info == null || info.speed == 0) {
			return;
		}
		if (info.useVanilla) {
			var effect = info.speed < 0 ? MobEffects.MOVEMENT_SLOWDOWN : MobEffects.MOVEMENT_SPEED;

			entity.addEffect(new MobEffectInstance(effect, Utility.TWO_SECONDS, info.speed - 1, true, HappyTrailsConfig.aParticlesOn));
		} else {
			var modifier = new AttributeModifier(Utility.UUID_HAPPYMODSPEED, Utility.SPEED_ATTR_NAME, info.speed * 0.01, Operation.MULTIPLY_BASE);
			moveAttribute.addPermanentModifier(modifier);
		}
	}

	@SubscribeEvent
	public static void onMountOrDismount(EntityMountEvent event) {
		// Make sure dismounted steeds don't keep modifiers.
		var steed = event.getEntityMounting().getVehicle();
		if (steed != null && steed instanceof LivingEntity) {
			((LivingEntity) steed).getAttribute(Attributes.MOVEMENT_SPEED).removePermanentModifier(Utility.UUID_HAPPYMODSPEED);
		}
	}
}

package com.mrbysco.flatterentities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Flattener {
	public static boolean renderingEnabled = true;

	public static final List<EntityType<?>> entityBlacklist = new ArrayList<>();
	public static final Map<ResourceKey<Level>, List<EntityType<?>>> entityDimensionWhitelist = new HashMap<>();
	public static final List<ResourceKey<Level>> dimensionBlacklist = new ArrayList<>();
	public static boolean dimensionListIsWhitelist = false;

	public static void prepareFlatRendering(float f, double x, double z, PoseStack poseStack, Entity entityIn) {
		if (renderingEnabled) {
			final EntityType<?> entityType = entityIn.getType();
			final ResourceKey<Level> entityDimension = entityIn.getCommandSenderWorld().dimension();
			final boolean entityInList = entityBlacklist.contains(entityIn.getType());
			final boolean worldInList = dimensionBlacklist.contains(entityDimension);
			boolean entityBlacklisted = !entityBlacklist.isEmpty() && entityInList;
			boolean dimensionFlat = dimensionBlacklist.isEmpty() || (!dimensionBlacklist.isEmpty() && dimensionListIsWhitelist == worldInList);
			boolean renderAnyway = false;

			if (!dimensionBlacklist.isEmpty() && !entityDimensionWhitelist.isEmpty()) {
				List<EntityType<?>> whitelist = entityDimensionWhitelist.getOrDefault(entityDimension, new ArrayList<>());
				renderAnyway = whitelist.contains(entityType) && !entityBlacklisted;
			}

			if (!entityBlacklisted && (dimensionFlat || renderAnyway)) {
				double angle1 = Mth.wrapDegrees(Math.atan2(z, x) / Math.PI * 180.0D);
				double angle2 = Mth.wrapDegrees(Math.floor((f - angle1) / 45.0) * 45.0D);

				final CameraType viewPoint = Minecraft.getInstance().options.getCameraType();
				boolean isPlayer = entityIn == Minecraft.getInstance().player;
				float offset = 0;
				if (isPlayer && entityIn instanceof LivingEntity livingEntity) {
					offset = Mth.wrapDegrees(livingEntity.yHeadRot - livingEntity.yHeadRotO);
				}

				if (isPlayer) {
					if (viewPoint == CameraType.FIRST_PERSON || viewPoint == CameraType.THIRD_PERSON_BACK) {
						angle1 = -90.0F - offset;
					}
					if (viewPoint == CameraType.THIRD_PERSON_FRONT) {
						angle1 = 90 + offset;
					}
				}

				poseStack.mulPose(Axis.YP.rotationDegrees((float) angle1));

				poseStack.scale(0.02F, 1.0F, 1.0F);

				if (isPlayer) {
					if (viewPoint == CameraType.FIRST_PERSON || viewPoint == CameraType.THIRD_PERSON_BACK) {
						angle2 = 90 + offset;
					}
					if (viewPoint == CameraType.THIRD_PERSON_FRONT) {
						angle2 = -90 - offset;
					}
				}
				poseStack.mulPose(Axis.YP.rotationDegrees((float) angle2));
			}
		}
	}
}
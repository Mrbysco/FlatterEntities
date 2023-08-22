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

	/**
	 * Prepares the rendering of an entity in a flat style based on certain conditions. This method adjusts the pose stack
	 * to achieve the desired flat rendering effect for the entity.
	 *
	 * @param f The yaw rotation angle of the entity, in degrees. This angle is often the result of linearly interpolating
	 *          between the entity's previous and current yaw rotations using Mth.rotLerp(partialTicks, entityIn.yRotO, entityIn.getYRot()).
	 * @param x The X-coordinate of the entity's position.
	 * @param z The Z-coordinate of the entity's position.
	 * @param poseStack The PoseStack used for rendering transformations.
	 * @param entityIn The entity to be rendered.
	 */
	public static void prepareFlatRendering(float f, double x, double z, PoseStack poseStack, Entity entityIn) {
		if (renderingEnabled) {
			// Extract entity and dimension information
			final EntityType<?> entityType = entityIn.getType();
			final ResourceKey<Level> entityDimension = entityIn.getCommandSenderWorld().dimension();

			// Check if entity and dimension are blacklisted
			final boolean entityInList = entityBlacklist.contains(entityIn.getType());
			final boolean worldInList = dimensionBlacklist.contains(entityDimension);
			boolean entityBlacklisted = !entityBlacklist.isEmpty() && entityInList;
			boolean dimensionFlat = dimensionBlacklist.isEmpty() || (!dimensionBlacklist.isEmpty() && dimensionListIsWhitelist == worldInList);
			boolean renderAnyway = false;

			// Check if entity should be rendered based on whitelist
			if (!dimensionBlacklist.isEmpty() && !entityDimensionWhitelist.isEmpty()) {
				List<EntityType<?>> whitelist = entityDimensionWhitelist.getOrDefault(entityDimension, new ArrayList<>());
				renderAnyway = whitelist.contains(entityType) && !entityBlacklisted;
			}

			// Apply flat rendering adjustments if necessary
			if (!entityBlacklisted && (dimensionFlat || renderAnyway)) {
				// Calculate rotation angles
				double angle1 = Mth.wrapDegrees(Math.atan2(z, x) / Math.PI * 180.0D);
				double angle2 = Mth.wrapDegrees(Math.floor((f - angle1) / 45.0) * 45.0D);

				// Get camera view type and player information
				final CameraType viewPoint = Minecraft.getInstance().options.getCameraType();
				boolean isPlayer = entityIn == Minecraft.getInstance().player;
				float offset = 0;

				// Adjust offset for player's head rotation
				if (isPlayer && entityIn instanceof LivingEntity livingEntity) {
					offset = Mth.wrapDegrees(livingEntity.yHeadRot - livingEntity.yHeadRotO);
				}

				// Adjust angles based on camera view type
				if (isPlayer) {
					if (viewPoint == CameraType.FIRST_PERSON || viewPoint == CameraType.THIRD_PERSON_BACK) {
						angle1 = -90.0F - offset;
					}
					if (viewPoint == CameraType.THIRD_PERSON_FRONT) {
						angle1 = 90 + offset;
					}
				}

				// Apply Y-axis rotation transformation
				poseStack.mulPose(Axis.YP.rotationDegrees((float) angle1));

				// Scale entity for flat rendering effect
				poseStack.scale(0.02F, 1.0F, 1.0F);

				// Adjust angles based on camera view type again
				if (isPlayer) {
					if (viewPoint == CameraType.FIRST_PERSON || viewPoint == CameraType.THIRD_PERSON_BACK) {
						angle2 = 90 + offset;
					}
					if (viewPoint == CameraType.THIRD_PERSON_FRONT) {
						angle2 = -90 - offset;
					}
				}

				// Apply additional Y-axis rotation transformation
				poseStack.mulPose(Axis.YP.rotationDegrees((float) angle2));
			}
		}
	}
}
package com.mrbysco.flatterentities.mixin.gecko;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrbysco.flatterentities.Flattener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

@Mixin(software.bernie.geckolib.renderer.GeoEntityRenderer.class)
public class GeoEntityRendererMixin<T extends Entity & GeoAnimatable> {

	@Inject(method = "actuallyRender(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/Entity;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V",
			remap = false,
			locals = LocalCapture.NO_CAPTURE, at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
			shift = Shift.AFTER,
			ordinal = 1))
	public void flatterentities_actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
		if (animatable instanceof LivingEntity entityIn) {
			final boolean shouldSit = entityIn.isPassenger() && (entityIn.getVehicle() != null && entityIn.getVehicle().shouldRiderSit());
			float f = Mth.rotLerp(partialTick, entityIn.yBodyRotO, entityIn.yBodyRot);
			final float f1 = Mth.rotLerp(partialTick, entityIn.yHeadRotO, entityIn.yHeadRot);
			if (shouldSit && entityIn.getVehicle() instanceof final LivingEntity livingentity) {
				f = Mth.rotLerp(partialTick, livingentity.yBodyRotO, livingentity.yBodyRot);
				final float f2 = f1 - f;
				float f3 = Mth.wrapDegrees(f2);
				if (f3 < -85.0F) {
					f3 = -85.0F;
				}

				if (f3 >= 85.0F) {
					f3 = 85.0F;
				}

				f = f1 - f3;
				if (f3 * f3 > 2500.0F) {
					f += f3 * 0.2F;
				}
			}
			double x = entityIn.getX();
			double z = entityIn.getZ();

			final Player player = Minecraft.getInstance().player;
			if (player != null) {
				x -= player.getX();
				z -= player.getZ();
			}

			Flattener.prepareFlatRendering(f, x, z, poseStack, entityIn);
		}
	}
}

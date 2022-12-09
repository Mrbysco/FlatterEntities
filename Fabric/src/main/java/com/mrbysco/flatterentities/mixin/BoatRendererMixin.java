package com.mrbysco.flatterentities.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.flatterentities.Flattener;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BoatRenderer.class)
public class BoatRendererMixin<T extends Boat> {

	@Inject(method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V",
			shift = Shift.AFTER,
			ordinal = 0))
	public void flatterRender(T entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn, CallbackInfo ci) {
		if (entityIn.isVehicle()) {
			final Entity passenger = entityIn.getPassengers().get(0);
			if (passenger instanceof LivingEntity rider) {
				if (rider.getMainHandItem().hasCustomHoverName()) {
					final String s = ChatFormatting.stripFormatting(rider.getMainHandItem().getDisplayName().getString());
					if (s != null && s.equals("Float")) {
						final float f = Mth.rotLerp(partialTicks, entityIn.yRotO, entityIn.getYRot());
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
		}
	}
}

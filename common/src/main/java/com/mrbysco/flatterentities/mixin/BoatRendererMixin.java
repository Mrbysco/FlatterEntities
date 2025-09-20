package com.mrbysco.flatterentities.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.flatterentities.Flattener;
import com.mrbysco.flatterentities.FlatterInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractBoatRenderer.class)
public class BoatRendererMixin<T extends Boat> {

	@Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
			shift = Shift.AFTER,
			ordinal = 0))
	public void flatterRender(BoatRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
		String s = renderState.nameTag != null ? ChatFormatting.stripFormatting(renderState.nameTag.getString()) : "";
		if ("Float".equals(s)) {
			if (renderState instanceof FlatterInfo flatterLerp) {
				final float yawLerp = flatterLerp.flatterentities$getYawLerp();
				double x = renderState.x;
				double z = renderState.y;

				final Player player = Minecraft.getInstance().player;
				if (player != null) {
					x -= player.getX();
					z -= player.getZ();
				}

				Flattener.prepareFlatRendering(yawLerp, x, z, poseStack, renderState);
			}
		}
	}
}

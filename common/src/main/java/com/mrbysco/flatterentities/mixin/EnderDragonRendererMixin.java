package com.mrbysco.flatterentities.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.flatterentities.Flattener;
import com.mrbysco.flatterentities.FlatterInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EnderDragonRenderer.class)
public class EnderDragonRendererMixin {

	@Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
			shift = Shift.AFTER,
			ordinal = 1))
	public void flatterRender(EnderDragonRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector,
	                          CameraRenderState cameraRenderState, CallbackInfo ci) {
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

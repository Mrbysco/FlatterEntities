package com.mrbysco.flatterentities.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.flatterentities.Flattener;
import com.mrbysco.flatterentities.FlatterInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractMinecartRenderer.class)
public class MinecartRendererMixin<T extends AbstractMinecart, S extends MinecartRenderState> {

	@Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",
			shift = Shift.AFTER,
			ordinal = 0))
	public void flatterRender(MinecartRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector,
	                          CameraRenderState cameraRenderState, CallbackInfo ci) {
		String s = renderState.nameTag != null ? ChatFormatting.stripFormatting(renderState.nameTag.getString()) : "";
		if ("Flinecart".equals(s)) {
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

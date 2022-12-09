package com.mrbysco.flatterentities.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.flatterentities.Flattener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LlamaSpitRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LlamaSpit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LlamaSpitRenderer.class)
public class LlamaSpitRendererMixin {
	@Inject(method = "render(Lnet/minecraft/world/entity/projectile/LlamaSpit;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V",
			shift = Shift.AFTER,
			ordinal = 0))
	public void flatterRender(LlamaSpit entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn, CallbackInfo ci) {
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

package com.mrbysco.flatterentities.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrbysco.flatterentities.Flattener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LlamaSpitRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LlamaSpitRenderer.class)
public class LlamaSpitRendererMixin {
	@Inject(method = "render(Lnet/minecraft/entity/projectile/LlamaSpitEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/matrix/MatrixStack;translate(DDD)V",
			shift = Shift.AFTER,
			ordinal = 0))
	public void flatterRender(LlamaSpitEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CallbackInfo ci) {
		final float f = MathHelper.lerp(partialTicks, entityIn.yRotO, entityIn.yRot);
		double x = entityIn.getX();
		double z = entityIn.getZ();

		final PlayerEntity player = Minecraft.getInstance().player;
		if(player != null) {
			x -= player.getX();
			z -= player.getZ();
		}

		Flattener.prepareFlatRendering(f, x, z, matrixStackIn, entityIn);
	}
}

package com.mrbysco.flatterentities.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrbysco.flatterentities.Flattener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.TridentRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TridentRenderer.class)
public class TridentRendererMixin {
	@Inject(method = "render(Lnet/minecraft/entity/projectile/TridentEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/matrix/MatrixStack;rotate(Lnet/minecraft/util/math/vector/Quaternion;)V",
			shift = Shift.AFTER,
			ordinal = 1))
	public void flatterRender(TridentEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CallbackInfo ci) {
		final float f = MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw);
		double x = entityIn.getPosX();
		double z = entityIn.getPosZ();

		final PlayerEntity player = Minecraft.getInstance().player;
		if(player != null) {
			x -= player.getPosX();
			z -= player.getPosZ();
		}

		Flattener.prepareFlatRendering(f, x, z, matrixStackIn, entityIn);
	}
}

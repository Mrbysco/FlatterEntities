package com.mrbysco.flatterentities.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrbysco.flatterentities.Flattener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecartRenderer.class)
public class MinecartRendererMixin<T extends AbstractMinecartEntity> {

	@Inject(method = "render(Lnet/minecraft/entity/item/minecart/AbstractMinecartEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION, at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/matrix/MatrixStack;translate(DDD)V",
			shift = Shift.AFTER,
			ordinal = 2))
	public void flatterRender(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CallbackInfo ci) {
		String s = TextFormatting.getTextWithoutFormattingCodes(entityIn.getName().getString());
		if ("Flinecart".equals(s)) {
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
}

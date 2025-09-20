package com.mrbysco.flatterentities.mixin;

import com.mrbysco.flatterentities.Flattener;
import com.mrbysco.flatterentities.FlatterInfo;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/entity/state/EntityRenderState;F)V",
			at = @At("HEAD"))
	private void dostuff(T entity, S reusedState, float partialTick, CallbackInfo ci) {
		if (reusedState instanceof FlatterInfo flatterLerp) {
			if (entity instanceof LivingEntity livingEntity) {
				Flattener.getYawRotation(livingEntity, partialTick, entity.isPassenger());
			} else {
				flatterLerp.flatterentities$setYawLerp(Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot()));
			}
			flatterLerp.flatterentities$setEntityType(entity.getType());
			flatterLerp.flatterentities$setDimension(entity.level().dimension());
		}
	}
}

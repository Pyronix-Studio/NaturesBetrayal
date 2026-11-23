package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CustomPhatomRenderer extends MobRenderer<CustomPhatom, CustomPhatomModel<CustomPhatom>> {

    private static final ResourceLocation PHANTOM_LOCATION =
            ResourceLocation.withDefaultNamespace("textures/entity/phantom.png");

    public CustomPhatomRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CustomPhatomModel<>(ctx.bakeLayer(ModelLayers.PHANTOM)), 0.75F);
        this.addLayer(new CustomPhatomEyesLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(CustomPhatom entity) {
        return PHANTOM_LOCATION;
    }

    @Override
    protected void scale(CustomPhatom entity, PoseStack stack, float partialTick) {
        stack.scale(1.0F, 1.0F, 1.0F);
        stack.translate(0.0F, 1.3125F, 0.1875F);
    }

    @Override
    protected void setupRotations(CustomPhatom entity, PoseStack stack,
                                  float ageInTicks, float rotationYaw,
                                  float partialTicks, float scale) {
        super.setupRotations(entity, stack, ageInTicks, rotationYaw, partialTicks, scale);
        stack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
    }
}
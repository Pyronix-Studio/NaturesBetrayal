package es.pyronixstudio.natures_betrayal.common.mobs.entity.phantom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CustomPhantomRenderer extends MobRenderer<CustomPhantom, CustomPhantomModel<CustomPhantom>> {

    private static final ResourceLocation PHANTOM_LOCATION =
            NaturesBetrayal.resource("textures/entity/custom_phantom/phantom.png");

    public CustomPhantomRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CustomPhantomModel<>(ctx.bakeLayer(ModelLayers.PHANTOM)), 0.75F);
        this.addLayer(new CustomPhantomEyesLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(CustomPhantom entity) {
        return PHANTOM_LOCATION;
    }

    @Override
    protected void scale(CustomPhantom entity, PoseStack stack, float partialTick) {
        stack.scale(1.0F, 1.0F, 1.0F);
        stack.translate(0.0F, 1.3125F, 0.1875F);
    }

    @Override
    protected void setupRotations(CustomPhantom entity, PoseStack stack,
                                  float ageInTicks, float rotationYaw,
                                  float partialTicks, float scale) {
        super.setupRotations(entity, stack, ageInTicks, rotationYaw, partialTicks, scale);
        stack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
        stack.mulPose(Axis.ZP.rotationDegrees(entity.getBank()));
    }
}
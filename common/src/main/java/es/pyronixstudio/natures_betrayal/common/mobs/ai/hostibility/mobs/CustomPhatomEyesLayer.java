package es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class CustomPhatomEyesLayer<T extends CustomPhatom> extends EyesLayer<T, CustomPhatomModel<T>> {

    private static final RenderType PHANTOM_EYES = RenderType.eyes(
            ResourceLocation.withDefaultNamespace("textures/entity/phantom_eyes.png")
    );

    public CustomPhatomEyesLayer(RenderLayerParent<T, CustomPhatomModel<T>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public RenderType renderType() {
        return PHANTOM_EYES;
    }
}
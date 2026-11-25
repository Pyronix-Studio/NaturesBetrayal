package es.pyronixstudio.natures_betrayal.common.mobs.entity.phantom;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;

public class CustomPhantomEyesLayer<T extends CustomPhantom> extends EyesLayer<T, CustomPhantomModel<T>> {

    private static final RenderType PHANTOM_EYES = RenderType.eyes(
           NaturesBetrayal.resource("textures/entity/custom_phantom/phantom_eyes.png")
    );

    public CustomPhantomEyesLayer(RenderLayerParent<T, CustomPhantomModel<T>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public RenderType renderType() {
        return PHANTOM_EYES;
    }
}
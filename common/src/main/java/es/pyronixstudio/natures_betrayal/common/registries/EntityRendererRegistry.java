package es.pyronixstudio.natures_betrayal.common.registries;

import es.pyronixstudio.natures_betrayal.common.entity.custom_phantom.CustomPhantomRenderer;
import es.pyronixstudio.natures_betrayal.common.registries.DeferredRegistry.RegistryObject;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;


public class EntityRendererRegistry {

    private record RendererEntry<T extends Entity>(RegistryObject<EntityType.Builder<T>, EntityType<T>> entityType, EntityRendererProvider<T> renderer) {
    }

    private final List<RendererEntry<?>> entries = new ArrayList<>();

    public EntityRendererRegistry() {
        register(Registries.ENTITY_TYPES.CUSTOM_PHANTOM, CustomPhantomRenderer::new);
    }

    public <T extends Entity> void register(RegistryObject<EntityType.Builder<T>, EntityType<T>> entityType, EntityRendererProvider<T> renderer) {
        entries.add(new RendererEntry<>(entityType, renderer));
    }

    public <T extends Entity> void forEachEntity(BiConsumer<EntityType<T>, EntityRendererProvider<T>> consumer) {
        for (RendererEntry<?> entry : entries) {
            @SuppressWarnings("unchecked") RendererEntry<T> typed = (RendererEntry<T>) entry;
            consumer.accept(typed.entityType().get(), typed.renderer());
        }
    }
}

package es.pyronixstudio.natures_betrayal.common.registries;

import es.pyronixstudio.natures_betrayal.common.mobs.entity.phantom.CustomPhantom;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EntityTypeRegistry extends DeferredRegistry<EntityType.Builder<?>, EntityType<?>> {

    public final RegistryObject<EntityType.Builder<CustomPhantom>, EntityType<CustomPhantom>> CUSTOM_PHANTOM = registerEntity("custom_phantom", () -> EntityType.Builder.of(CustomPhantom::new, MobCategory.MONSTER).sized(0.9f, 0.5f).clientTrackingRange(8));

    private <T extends Entity> RegistryObject<EntityType.Builder<T>, EntityType<T>> registerEntity(String name, Supplier<EntityType.Builder<T>> factory) {
        @SuppressWarnings("unchecked") RegistryObject<EntityType.Builder<T>, EntityType<T>> typed = (RegistryObject<EntityType.Builder<T>, EntityType<T>>) (RegistryObject<?, ?>) register(name, () -> factory.get());
        return typed;
    }

    public <T extends Entity> void forEachEntity(BiConsumer<String, RegistryObject<EntityType.Builder<T>, EntityType<T>>> consumer) {
        forEach((name, object) -> {
            @SuppressWarnings("unchecked") RegistryObject<EntityType.Builder<T>, EntityType<T>> typed = (RegistryObject<EntityType.Builder<T>, EntityType<T>>) (RegistryObject<?, ?>) object;
            consumer.accept(name, typed);
        });
    }
}

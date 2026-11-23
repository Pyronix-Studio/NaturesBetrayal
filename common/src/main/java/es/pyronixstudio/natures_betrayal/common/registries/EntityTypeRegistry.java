package es.pyronixstudio.natures_betrayal.common.registries;

import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs.CustomPhatom;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class EntityTypeRegistry {

    private final Map<String, RegistryEntry<?, ?>> ENTITY_TYPES = new LinkedHashMap<>();

    public final RegistryEntry<EntityType.Builder<CustomPhatom>, EntityType<CustomPhatom>> CUSTOM_PHANTOM =
            register("custom_phantom", EntityType.Builder.of(CustomPhatom::new, MobCategory.MONSTER).sized(0.9f, 0.5f).clientTrackingRange(8));

    public <T extends Entity> RegistryEntry<EntityType.Builder<T>, EntityType<T>> register(String name, EntityType.Builder<T> builder) {

        RegistryEntry<EntityType.Builder<T>, EntityType<T>> entry = new RegistryEntry<>(name, builder);

        ENTITY_TYPES.put(name, entry);
        return entry;
    }

    public <T extends Entity> void forEach(BiConsumer<String, RegistryEntry<EntityType.Builder<T>, EntityType<T>>> consumer) {
        ENTITY_TYPES.forEach((name, entry) -> {
            @SuppressWarnings("unchecked") RegistryEntry<EntityType.Builder<T>, EntityType<T>> typedEntry = (RegistryEntry<EntityType.Builder<T>, EntityType<T>>) entry;

            consumer.accept(name, typedEntry);
        });
    }
}
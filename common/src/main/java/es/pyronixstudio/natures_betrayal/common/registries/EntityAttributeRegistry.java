package es.pyronixstudio.natures_betrayal.common.registries;

import es.pyronixstudio.natures_betrayal.common.NaturesBetrayal;
import es.pyronixstudio.natures_betrayal.common.mobs.ai.hostibility.mobs.CustomPhatom;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class EntityAttributeRegistry {

    private record AttributeEntry<T extends Mob>(RegistryEntry<EntityType.Builder<T>, EntityType<T>> entityType, Supplier<AttributeSupplier.Builder> attributes) {
        void accept(BiConsumer<EntityType<? extends Mob>, AttributeSupplier.Builder> consumer) {
            consumer.accept(entityType.get(), attributes.get());
        }
    }

    private final List<AttributeEntry<?>> entries = new ArrayList<>();

    public EntityAttributeRegistry() {
        register(NaturesBetrayal.registries.ENTITY_TYPES.CUSTOM_PHANTOM,
                CustomPhatom::createAttributes);
    }

    public <T extends Mob> void register(RegistryEntry<EntityType.Builder<T>, EntityType<T>> entityType, Supplier<AttributeSupplier.Builder> attributes) {
        entries.add(new AttributeEntry<>(entityType, attributes));
    }

    public void forEach(BiConsumer<EntityType<? extends Mob>, AttributeSupplier.Builder> consumer) {
        entries.forEach(entry -> entry.accept(consumer));
    }
}
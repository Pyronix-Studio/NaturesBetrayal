package es.pyronixstudio.natures_betrayal.common.mixins.mobs.injects;

import es.pyronixstudio.natures_betrayal.common.config.NaturesBetrayalConfig;
import es.pyronixstudio.natures_betrayal.common.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeleton.class)
public abstract class SkeletonMixin extends Monster {

    @Unique
    private ItemStack naturesBetrayal$rangedWeapon = ItemStack.EMPTY;
    @Unique
    private ItemStack naturesBetrayal$meleeWeapon = ItemStack.EMPTY;

    @Unique
    private boolean naturesBetrayal$meleeWeaponActivated = false;

    protected SkeletonMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }


    @Inject(method = "populateDefaultEquipmentSlots", at = @At("TAIL"))
    private void naturesBetrayal$populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci){
        naturesBetrayal$rangedWeapon = getWeaponItem();

        naturesBetrayal$meleeWeapon = new ItemStack(Items.IRON_SWORD);

        EnchantmentHelper.enchantItemFromProvider(
                naturesBetrayal$meleeWeapon,
                level().registryAccess(),
                VanillaEnchantmentProviders.MOB_SPAWN_EQUIPMENT,
                difficulty,
                random
        );
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void naturesBetrayal$aiStep(CallbackInfo ci){
        LivingEntity target = getTarget();
        if(target == null || !target.isAlive())return;
        if(!naturesBetrayal$isInMele(target)){
            if(naturesBetrayal$meleeWeaponActivated){
                naturesBetrayal$meleeWeaponActivated = false;
                setItemSlot(EquipmentSlot.MAINHAND, naturesBetrayal$rangedWeapon);
            }
            return;
        }
        if(!naturesBetrayal$meleeWeaponActivated){
            setItemSlot(EquipmentSlot.MAINHAND, naturesBetrayal$meleeWeapon);
            naturesBetrayal$meleeWeaponActivated = true;
        }
    }

    @Unique
    private boolean naturesBetrayal$isInMele(LivingEntity target){
        double configDistance = NaturesBetrayalConfig.SKELETON_MELEE_WEAPON.get();
        return distanceToSqr(target) < configDistance * configDistance;
    }

   @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
   private void naturesBetrayal$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci){
        naturesBetrayal$rangedWeapon = ItemStack.parseOptional(registryAccess(), compoundTag.getCompound(Registries.KEYS.RANGED_WEAPON));
        naturesBetrayal$meleeWeapon = ItemStack.parseOptional(registryAccess(), compoundTag.getCompound(Registries.KEYS.MELEE_WEAPON));
        if(naturesBetrayal$meleeWeapon == ItemStack.EMPTY)
            naturesBetrayal$meleeWeapon = new ItemStack(Items.IRON_SWORD);

   }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put(Registries.KEYS.RANGED_WEAPON, naturesBetrayal$rangedWeapon.save(registryAccess()));
        compound.put(Registries.KEYS.MELEE_WEAPON, naturesBetrayal$meleeWeapon.save(registryAccess()));

    }
}

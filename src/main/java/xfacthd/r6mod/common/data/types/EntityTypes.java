package xfacthd.r6mod.common.data.types;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.entities.camera.*;
import xfacthd.r6mod.common.entities.grenade.*;

public class EntityTypes
{
    private static IForgeRegistry<EntityType<?>> typeRegistry;

    //Building entities
    public static EntityType<EntityCamera>            entityTypeCamera;

    //Generic gadget entities
    //public static EntityType<EntityFragGrenade>       entityTypeFragGrenade;
    //public static EntityType<EntityStunGrenade>       entityTypeStunGrenade;
    //public static EntityType<EntitySmokeGrenade>      entityTypeSmokeGrenade;
    public static EntityType<EntityImpactGrenade>     entityTypeImpactGrenade;
    //public static EntityType<EntityNitroCell>         entityTypeNitroCell;
    //public static EntityType<EntityDrone>             entityTypeDrone;
    public static EntityType<EntityBulletproofCamera> entityTypeBulletproofCamera;
    //public static EntityType<EntityProximityAlarm>    entityTypeProximityAlarm;

    //Operator specific gadget entities (attack)
    public static EntityType<EntityEMPGrenade>          entityTypeEMPGrenade;
    //public static EntityType<EntityBreachingRound>    entityTypeBreachingRound;
    //public static EntityType<EntityShockDrone>        entityTypeShockDrone;
    //public static EntityType<EntityClusterGrenade>    entityTypClusterGrenadee;
    //public static EntityType<EntityCrossbowBolt>      entityTypeCrossbowBolt;
    //public static EntityType<EntityXKairosPellet>     entityTypeXKairosPellet;
    public static EntityType<EntityCandelaGrenade>    entityTypeCandelaGrenade;
    public static EntityType<EntityCandelaFlash>      entityTypeCandelaFlash;
    //public static EntityType<EntityLifelineGrenade>   entityTypeLifelineGrenade;
    //public static EntityType<EntityAirjab>            entityTypeAirjab;
    //public static EntityType<EntityTraxStingers>      entityTypeTraxStingers;
    //public static EntityType<EntityExplosiveLance>    entityTypeExplosiveLance;
    //public static EntityType<EntityGeminiReplicator>  entityTypeGeminiReplicator;
    //public static EntityType<EntitySELMA>             entityTypeSELMA;

    //Operator specific gadget entities (defense)
    //public static EntityType<EntityGasGrenade>        entityTypeGasGrenade;
    //public static EntityType<EntityBlackEye>          entityTypeBlackEye;
    public static EntityType<EntityYokaiDrone>        entityTypeYokaiDrone;
    //public static EntityType<EntityGuMine>            entityTypeGuMine;
    //public static EntityType<EntityGrzmotMine>        entityTypeGrzmotMine;
    //public static EntityType<EntityPrisma>            entityTypePrisma;
    public static EntityType<EntityEvilEyeCamera>     entityTypeEvilEye;
    //public static EntityType<EntityElectroclaw>       entityTypeElectroclaw;
    //public static EntityType<EntityPest>              entityTypePest;
    //public static EntityType<EntityMagNet>            entityTypeMagNet;

    public static void setRegistry(IForgeRegistry<EntityType<?>> registry) { typeRegistry = registry; }

    public static<T extends Entity> EntityType<T> create(EntityType.IFactory<T> factory, String name)
    {
        return create(factory, name, EntityClassification.MISC);
    }

    public static<T extends Entity> EntityType<T> create(EntityType.IFactory<T> factory, String name, EntityClassification classification)
    {
        EntityType<T> entityType = EntityType.Builder.create(factory, classification).build(name);
        entityType.setRegistryName(R6Mod.MODID, name);
        typeRegistry.register(entityType);
        return entityType;
    }

    public static<T extends Entity> EntityType<T> create(EntityType.IFactory<T> factory, String name, float width, float height, int updateInterval)
    {
        return create(factory, name, width, height, updateInterval, EntityClassification.MISC, false);
    }

    public static <T extends Entity> EntityType<T> create(EntityType.IFactory<T> factory, String name, float width, float height, int updateInterval, boolean blockSummon)
    {
        return create(factory, name, width, height, updateInterval, EntityClassification.MISC, blockSummon);
    }

    public static<T extends Entity> EntityType<T> create(EntityType.IFactory<T> factory, String name, float width, float height, int updateInterval, EntityClassification classification, boolean blockSummon)
    {
        EntityType.Builder<T> builder = EntityType.Builder.create(factory, classification).size(width, height).setUpdateInterval(updateInterval);
        if (blockSummon) { builder.disableSummoning(); }
        EntityType<T> entityType = builder.build(name);
        entityType.setRegistryName(R6Mod.MODID, name);
        typeRegistry.register(entityType);
        return entityType;
    }



    public static <T extends AbstractEntityGrenade> EntityType<T> createGrenade(EntityType.IFactory<T> factory, String name)
    {
        return create(factory, name, .25F, .25F, 1, true);
    }
}
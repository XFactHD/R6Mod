package xfacthd.r6mod.common.data.types;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.common.tileentities.building.*;
import xfacthd.r6mod.common.tileentities.gadgets.*;
import xfacthd.r6mod.common.tileentities.misc.*;

import java.util.function.Supplier;

public class TileEntityTypes
{
    private static IForgeRegistry<TileEntityType<?>> typeRegistry;

    public static TileEntityType<TileEntityAmmoBox>           tileTypeAmmoBox;
    public static TileEntityType<TileEntityMagFiller>         tileTypeMagFiller;
    public static TileEntityType<TileEntityBulletPress>       tileTypeBulletPress;
    public static TileEntityType<TileEntityFakeFire>          tileTypeFakeFire;

    public static TileEntityType<TileEntityTeamSpawn>         tileTypeTeamSpawn;

    public static TileEntityType<TileEntityCamera>            tileTypeCamera;

    public static TileEntityType<TileEntityBreachCharge>      tileTypeBreachCharge;
    public static TileEntityType<TileEntityClaymore>          tileTypeClaymore;
    public static TileEntityType<TileEntityBarbedWire>        tileTypeBarbedWire;
    public static TileEntityType<TileEntityDeployableShield>  tileTypeDeployableShield;
    public static TileEntityType<TileEntityBulletproofCamera> tileTypeBulletproofCamera;

    public static TileEntityType<TileEntityThermiteCharge>    tileTypeThermiteCharge;
    public static TileEntityType<TileEntityToughBarricade>    tileTypeToughBarricade;
    public static TileEntityType<TileEntityWelcomeMat>        tileTypeWelcomeMat;
    public static TileEntityType<TileEntityBlackMirror>       tileTypeBlackMirror;
    public static TileEntityType<TileEntityEvilEye>           tileTypeEvilEye;
    public static TileEntityType<TileEntityVolcanShield>      tileTypeVolcanShield;
    public static TileEntityType<TileEntityBanshee>           tileTypeBanshee;

    public static void setRegistry(IForgeRegistry<TileEntityType<?>> registry) { typeRegistry = registry; }

    @SuppressWarnings("ConstantConditions")
    public static<T extends TileEntity> TileEntityType<T> create(Supplier<T> factory, String name, Block... blocks)
    {
        TileEntityType<T> tileType = TileEntityType.Builder.create(factory, blocks).build(null);
        tileType.setRegistryName(R6Mod.MODID, name);
        typeRegistry.register(tileType);
        return tileType;
    }
}
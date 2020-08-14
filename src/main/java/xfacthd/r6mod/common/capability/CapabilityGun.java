package xfacthd.r6mod.common.capability;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import xfacthd.r6mod.R6Mod;
import xfacthd.r6mod.api.capability.ICapabilityGun;
import xfacthd.r6mod.api.interaction.IShootable;
import xfacthd.r6mod.common.R6Content;
import xfacthd.r6mod.common.data.gun_data.*;
import xfacthd.r6mod.common.data.itemsubtypes.*;
import xfacthd.r6mod.common.items.gun.ItemGun;
import xfacthd.r6mod.common.items.gun.ItemMagazine;
import xfacthd.r6mod.common.items.material.ItemBullet;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.gun.PacketGunState;
import xfacthd.r6mod.common.net.packets.gun.PacketReloadState;
import xfacthd.r6mod.common.util.*;
import xfacthd.r6mod.common.util.damage.DamageSourceGun;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class CapabilityGun implements ICapabilityGun
{
    //@CapabilityInject(ICapabilityGun.class) //TODO: reenable, causes debug class reload to fail
    public static final Capability<ICapabilityGun> GUN_CAPABILITY = null;
    public static final ICapabilityGun DUMMY = new Empty();

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final ItemStack stack;
    private EnumGun gun;
    private boolean wasSelected = false;
    private boolean firing = false;
    private boolean wasFiring = false; //Used by semi auto weapons to only trigger once per click
    private long lastShot = 0;
    private int burstShotsFired = 0; //The amount of bullets fired in burst mode since pressing the fire button
    private boolean charged = false; //Used for pump action shotguns and bolt action snipers
    private long chargeStart = 0;
    private Firemode firemode;
    private boolean aiming = false;
    private long aimStateChangeClient = 0;
    private boolean reloading = false;
    private long reloadStateStart = 0;
    private ReloadState reloadState = ReloadState.NONE;
    private long reloadStateDuration = 0;
    private boolean chambered = false; //Wether an additional bullet is chambered (if the weapon supports it)
    private boolean magLoaded = false; //Wether a mag is loaded
    private int bulletsLoaded = 0;  //How many bullets are loaded
    private ItemStack newMag = ItemStack.EMPTY;
    private final List<EnumAttachment> attachments = new ArrayList<>();
    private boolean specialAttachmentActive = false; //TODO: implement (de)activation
    private boolean attachmentsChanged = false;
    private float damage = 0;
    private float spread = 0;
    private float recoil = 0;
    private int adsTime = 0;

    public CapabilityGun(ItemStack stack)
    {
        this.stack = stack;

        if (stack.getItem() instanceof ItemGun)
        {
            gun = ((ItemGun)stack.getItem()).getGun();
            if (Utils.arrayContains(gun.getFiremodes(), Firemode.AUTO)) { firemode = Firemode.AUTO; }
            else { firemode = Firemode.SINGLE; }

            damage = gun.getActualDamage(attachments);
            spread = gun.getSpreadModified(attachments);
            recoil = gun.getRecoilModified(attachments);
            adsTime = gun.getAimTime(attachments);
        }
    }

    @Override
    public void tick(PlayerEntity player, int slot, boolean selected)
    {
        if (attachmentsChanged)
        {
            damage = gun.getActualDamage(attachments);
            spread = gun.getSpreadModified(attachments);
            recoil = gun.getRecoilModified(attachments);
            adsTime = gun.getAimTime(attachments);
        }

        //Server side only
        if (player.world.isRemote) { return; }

        //Check slot switching
        handleSelectionChange(player, slot, selected);

        //Only work if selected
        if (!selected) { return; }

        //TODO: handle EMP effect being added to player

        if (reloading) { manageReloading(player, slot); }
        else { manageNormalState(player, slot); }

        wasFiring = firing;
    }

    @Override //TODO: rewrite reload handling to be more generic to support strange stuff (like G8A1)
    public void reload(PlayerEntity player)
    {
        //Trying to seamlessly cancel aiming with the reload would add unnecessary complexity
        if (reloading || aiming) { return; }

        if (reloadState != ReloadState.NONE) //Reloading was canceled at a state that must be resumed
        {
            reloading = true;
        }
        else if (gun.hasMag())
        {
            if (magLoaded)
            {
                //CSRX300 is a special snowflake
                if (gun == EnumGun.CSRX_300 && bulletsLoaded == 0 && !chambered)
                {
                    reloadState = ReloadState.OPEN_BREACH;
                }
                else
                {
                    reloadState = ReloadState.MAG_OUT;
                }
                reloading = true;
            }
            else if (!findNewMag(player, gun, true).isEmpty())
            {
                reloadState = gun == EnumGun.CSRX_300 ? ReloadState.OPEN_BREACH : ReloadState.MAG_IN;
                reloading = true;
            }
        }
        else if (bulletsLoaded < gun.getMagCapacity()) //Magless and space to reload
        {
            if (gun.getGunType() == EnumGun.Type.REVOLVER)
            {
                //Revolvers use a speedloader
                if (!findBullets(player, gun, 1, true).isEmpty())
                {
                    reloadState = ReloadState.OPEN_BREACH;
                    reloading = true;
                }
            }
            else //Magless shotgun
            {
                if (!findBullets(player, gun, 1, true).isEmpty())
                {
                    reloadState = bulletsLoaded > 1 ? ReloadState.LOAD : ReloadState.OPEN_BREACH;
                    reloading = true;
                }
            }
        }

        if (reloading) { resetGunState(false); }
    }

    @Override
    public void restock(PlayerEntity player, int slot)
    {
        if (player.world.isRemote) { return; }

        if (reloading)
        {
            resetReloadState();
            newMag = ItemStack.EMPTY; //Don't care about losing this mag, it will get replaced anyway
            sendReloadStateUpdate((ServerPlayerEntity)player, slot);
        }

        magLoaded = true;
        bulletsLoaded = gun.getMagCapacity();
        chambered = gun.canChamberAdditionalBullet();
        sendGunStatusUpdate((ServerPlayerEntity)player, slot);

        if (gun.hasMag())
        {
            Item ammoItem = R6Content.itemMagazines.get(gun.getMagazine());
            ItemStack ammoStack = new ItemStack(ammoItem, 1);
            ammoStack.setDamage(gun.getMagCapacity());
            for (int i = 0; i < gun.getAdditionalAmmo(); i++) { player.inventory.addItemStackToInventory(ammoStack.copy()); }
        }
        else
        {
            Item ammoItem = R6Content.itemBullets.get(gun.getAmmoType());
            int count = gun.getAdditionalAmmo();
            while (count > 0)
            {
                //noinspection deprecation
                int size = Math.min(count, ammoItem.getMaxStackSize());

                ItemStack ammoStack = new ItemStack(ammoItem, size);
                player.inventory.addItemStackToInventory(ammoStack);

                count -= size;
            }
        }
    }

    /*
     * Private helpers
     */

    private void handleSelectionChange(PlayerEntity player, int slot, boolean selected)
    {
        if (selected != wasSelected)
        {
            if (wasSelected) //If the player switches away from the gun, cancel certain actions
            {
                if (reloading) { cancelReloading(player, slot); }

                if (firing || aiming)
                {
                    resetGunState(true);
                    sendGunStatusUpdate((ServerPlayerEntity) player, slot);
                }
            }
            else //Gun is now selected
            {
                if (!charged && isPumpActionOrBoltAction() && getLoadedBulletsInternal() > 0)
                {
                    chargeStart = player.world.getGameTime(); //Gun in hand again, try again to chamber the next bullet
                    sendGunStatusUpdate((ServerPlayerEntity)player, slot);
                }
            }

            wasSelected = selected;
        }
    }

    private void manageReloading(PlayerEntity player, int slot)
    {
        //Can't aim or fire while reloading
        if (firing)
        {
            resetGunState(false);
            sendGunStatusUpdate((ServerPlayerEntity)player, slot);
        }

        //Can't reload while sprinting
        if (player.isSprinting()) { cancelReloading(player, slot); }

        //If we are still reloading, then proceed
        if (reloading) { tickReload(player, slot); }
    }

    private void manageNormalState(PlayerEntity player, int slot)
    {
        //Can't aim of fire while sprinting
        if ((firing || aiming) && player.isSprinting())
        {
            resetGunState(true);
            sendGunStatusUpdate((ServerPlayerEntity)player, slot);
        }

        //automatically resume reloading when canceled in certain states
        if (reloadState != ReloadState.NONE)
        {
            if (!player.isSprinting() && !aiming)
            {
                reloading = true;

                if (firing)
                {
                    resetGunState(false);
                    sendGunStatusUpdate((ServerPlayerEntity)player, slot);
                }
            }
        }

        //Only try to fire if there is ammo and reloading is done
        if (firing) { tickFiring(player, slot); }

        if (!charged && isPumpActionOrBoltAction() && getLoadedBulletsInternal() > 0)
        {
            if (player.world.getGameTime() - chargeStart > gun.getChargeTime())
            {
                charged = true;
                sendGunStatusUpdate((ServerPlayerEntity)player, slot);
            }
        }
    }

    private void tickFiring(PlayerEntity player, int slot)
    {
        boolean hasAmmo = getLoadedBulletsInternal() > 0;
        long diff = player.world.getGameTime() - lastShot;

        if (firemode == Firemode.AUTO) { tickFiringAuto(player, slot, hasAmmo, diff); }
        else if (firemode == Firemode.BURST) { tickFiringBurst(player, slot, hasAmmo, diff); }
        else if (firemode == Firemode.SINGLE)
        {
            if (gun.getGunType() == EnumGun.Type.SHOTGUN || gun == EnumGun.BAILIFF_410) { tickFiringShotgun(player, slot, hasAmmo, diff); }
            else if (gun == EnumGun.CSRX_300) { tickFiringBoltAction(player, slot, hasAmmo, diff); }
            else { tickFiringSemiAuto(player, slot, hasAmmo, diff); }
        }
    }

    private void tickFiringAuto(PlayerEntity player, int slot, boolean hasAmmo, long timeDiff)
    {
        if (hasAmmo && timeDiff > gun.getTicksBetweenRounds())
        {
            fireProjectile(player, 0, 0, 1);
            boolean empty = consumeBullet();

            playSound(player, GunSoundType.FIRE);
            if (empty) { playSound(player, GunSoundType.LOCK_BACK); }

            sendGunStatusUpdate((ServerPlayerEntity)player, slot);

            lastShot = player.world.getGameTime();
        }
    }

    private void tickFiringBurst(PlayerEntity player, int slot, boolean hasAmmo, long timeDiff)
    {
        if (hasAmmo && timeDiff > gun.getTicksBetweenRounds() && burstShotsFired < gun.getBurstBulletCount())
        {
            fireProjectile(player, 0, 0, 1);
            boolean empty = consumeBullet();

            playSound(player, GunSoundType.FIRE);
            if (empty) { playSound(player, GunSoundType.LOCK_BACK); }

            sendGunStatusUpdate((ServerPlayerEntity)player, slot);

            lastShot = player.world.getGameTime();
            burstShotsFired++;
        }
    }

    private void tickFiringSemiAuto(PlayerEntity player, int slot, boolean hasAmmo, long timeDiff)
    {
        if (hasAmmo && timeDiff > gun.getTicksBetweenRounds() && !wasFiring)
        {
            if (gun == EnumGun.BAILIFF_410) { firePellets(player, 0, 0, 1); } //Special case for shotgun revolver
            else { fireProjectile(player, 0, 0, 1); }
            boolean empty = consumeBullet();

            playSound(player, GunSoundType.FIRE);
            if (empty) { playSound(player, GunSoundType.LOCK_BACK); }

            sendGunStatusUpdate((ServerPlayerEntity)player, slot);

            lastShot = player.world.getGameTime();
        }
    }

    private void tickFiringShotgun(PlayerEntity player, int slot, boolean hasAmmo, long timeDiff)
    {
        if (hasAmmo && timeDiff > gun.getTicksBetweenRounds() && !wasFiring)
        {
            firePellets(player, 0, 0, 1);
            boolean empty = consumeBullet();

            playSound(player, GunSoundType.FIRE);
            if (empty) { playSound(player, GunSoundType.LOCK_BACK); }
            else if (isPumpActionOrBoltAction())
            {
                charged = false;
                chargeStart = player.world.getGameTime();
            }

            sendGunStatusUpdate((ServerPlayerEntity)player, slot);

            lastShot = player.world.getGameTime();
        }
    }

    private void tickFiringBoltAction(PlayerEntity player, int slot, boolean hasAmmo, long timeDiff)
    {
        if (hasAmmo && timeDiff > gun.getTicksBetweenRounds() && !wasFiring)
        {
            fireProjectile(player, 0, 0, 1);
            boolean empty = consumeBullet();

            playSound(player, GunSoundType.FIRE);
            if (empty) { playSound(player, GunSoundType.LOCK_BACK); }
            else //This is a bolt action, no need to check again
            {
                charged = false;
                chargeStart = player.world.getGameTime();
            }

            sendGunStatusUpdate((ServerPlayerEntity)player, slot);

            lastShot = player.world.getGameTime();
        }
    }

    private boolean consumeBullet()
    {
        if (chambered) { chambered = false; }
        else { bulletsLoaded--; }

        return getLoadedBulletsInternal() == 0;
    }

    /**
     * @param player The player holding the gun
     * @param dropDistMin Distance where damage drop starts
     * @param dropDistMax Distance where damage drop end
     * @param dropFactor Damage multiplier at the end
     */
    private void fireProjectile(PlayerEntity player, int dropDistMin, int dropDistMax, float dropFactor)
    {
        if (!player.world.isRemote)
        {
            float actualSpread = spread;
            actualSpread *= 1F - getAimState(player.world.getGameTime(), 0);
            List<HitData> hits = RayTraceHelper.raytraceGunShot(player.world, player, 256, gun.getMaxPenetrationCount(), actualSpread);
            for (HitData hit : hits)
            {
                if (hit.getHitType() == HitData.Type.ENTITY)
                {
                    Entity entityHit = hit.getEntityHit();
                    if (entityHit instanceof LivingEntity)
                    {
                        float actualDamage = this.damage;
                        if (hit.isHeadshot()) { actualDamage = 1024F; }
                        else
                        {
                            //actualDamage = ; //TODO: implement damage drop, special case CSRX300
                        }
                        entityHit.attackEntityFrom(new DamageSourceGun(player, hit.isHeadshot(), gun), actualDamage);
                        entityHit.hurtResistantTime = 0; //Make sure the entity can be hurt by the next bullet no matter how soon it is fired
                    }
                    else if (entityHit instanceof IShootable)
                    {
                        ((IShootable) entityHit).shoot(player, hit.getHitVec());
                    }
                    else if (entityHit instanceof ItemFrameEntity)
                    {
                        entityHit.attackEntityFrom(new DamageSourceGun(player, false, gun), 20F);
                    }
                }
                else if (hit.getHitType() == HitData.Type.BLOCK)
                {
                    TileEntity te = player.world.getTileEntity(hit.getPos());
                    if (te instanceof IShootable) { ((IShootable)te).shoot(player, hit.getHitVec()); }
                    else
                    {
                        BlockState state = player.world.getBlockState(hit.getPos());
                        if (state.getMaterial() == Material.GLASS)
                        {
                            player.world.destroyBlock(hit.getPos(), false);
                        }
                        else if (state.getBlock() instanceof IShootable)
                        {
                            ((IShootable)state.getBlock()).shoot(player, hit.getHitVec());
                        }
                    }
                }
            }
        }
    }

    /**
     * @param player The player holding the gun
     * @param dropDistMin Distance where damage drop starts
     * @param dropDistMax Distance where damage drop end
     * @param dropFactor Damage multiplier at the end
     */
    private void firePellets(PlayerEntity player, int dropDistMin, int dropDistMax, float dropFactor)
    {

    }

    private void tickReload(PlayerEntity player, int slot)
    {
        ReloadState lastState = reloadState;
        boolean update;

        if (gun.hasMag()) { update = tickReloadMag(player); }
        else if (gun.getGunType() == EnumGun.Type.REVOLVER) { update = tickReloadRevolver(player); } //Revolvers need special casing because they use a speedloader
        else if (gun == EnumGun.CSRX_300) { update = tickReloadBoltAction(player); }
        else { update = tickReloadMagless(player); }

        if (update)
        {
            sendReloadStateUpdate((ServerPlayerEntity)player, slot);

            //Update needed => reload state changed => need to play new state's sound
            if (reloadState != ReloadState.NONE) { playSound(player, reloadState.getSound()); }

            //If the loaded mag changed, the client needs an update about that
            if (lastState == ReloadState.MAG_IN || lastState == ReloadState.MAG_OUT || lastState == ReloadState.LOAD)
            {
                sendGunStatusUpdate((ServerPlayerEntity)player, slot);
            }
        }
    }

    private boolean tickReloadMag(PlayerEntity player)
    {
        if (reloadStateStart == 0)
        {
            if (reloadState == ReloadState.MAG_IN)
            {
                newMag = findNewMag(player, gun, false);
                if (newMag.isEmpty())
                {
                    //If the new mag somehow got lost since last tick, abort here
                    resetReloadState();
                    return false;
                }
            }

            reloadStateStart = player.world.getGameTime();
            reloadStateDuration = gun.getReloadStateTime(reloadState);

            return true; //Can just return here, no point in checking the diff, will be 0 anyway and we need an update
        }

        long diff = player.world.getGameTime() - reloadStateStart;
        if (diff >= reloadStateDuration)
        {
            if (reloadState == ReloadState.MAG_OUT)
            {
                newMag = findNewMag(player, gun, false);
                //If a new mag was found, continue reloading, else abort here
                setNewReloadStateConditional(player.world, !newMag.isEmpty(), ReloadState.MAG_IN);

                //Chamber additional bullet
                if (bulletsLoaded > 0 && !chambered && gun.canChamberAdditionalBullet())
                {
                    bulletsLoaded--;
                    chambered = true;
                }

                //Search new mag first to avoid reloading the same mag again if it is not empty
                ItemStack magStack = new ItemStack(R6Content.itemMagazines.get(gun.getMagazine()));
                magStack.setDamage(bulletsLoaded);
                player.inventory.addItemStackToInventory(magStack);

                magLoaded = false;
                bulletsLoaded = 0;
            }
            else if (reloadState == ReloadState.MAG_IN)
            {
                magLoaded = true;
                bulletsLoaded = newMag.getDamage();
                newMag = ItemStack.EMPTY;

                //If no bullet is chambered, the gun has to be charged, else we are done
                setNewReloadStateConditional(player.world, !chambered, ReloadState.CHAMBER);
            }
            else
            {
                resetReloadState();
            }
            return true;
        }
        return false;
    }

    private boolean tickReloadRevolver(PlayerEntity player)
    {
        if (reloadStateStart == 0)
        {
            reloadStateStart = player.world.getGameTime();
            reloadStateDuration = gun.getReloadStateTime(reloadState);
            return true; //Can just return here, no point in checking the diff, will be 0 anyway and we need an update
        }

        long diff = player.world.getGameTime() - reloadStateStart;
        if (diff >= reloadStateDuration)
        {
            if (reloadState == ReloadState.OPEN_BREACH)
            {
                int count = Math.min(gun.getMagCapacity() - bulletsLoaded, findBullets(player, gun, 1, true).getCount());
                newMag = findBullets(player, gun, count, false);
                setNewReloadStateConditional(player.world, !newMag.isEmpty(), ReloadState.LOAD, ReloadState.CLOSE_BREACH);
            }
            else if (reloadState == ReloadState.LOAD)
            {
                bulletsLoaded += newMag.getCount();
                newMag = ItemStack.EMPTY;
            }
            else
            {
                resetReloadState();
            }
            return true;
        }
        return false;
    }

    private boolean tickReloadMagless(PlayerEntity player)
    {
        if (reloadStateStart == 0)
        {
            if (reloadState == ReloadState.LOAD)
            {
                newMag = findBullets(player, gun, 1, false);
                if (newMag.isEmpty())
                {
                    //If the bullet somehow got lost since last tick, abort here
                    resetReloadState();
                    return false;
                }
            }

            reloadStateStart = player.world.getGameTime();
            reloadStateDuration = gun.getReloadStateTime(reloadState);

            return true; //Can just return here, no point in checking the diff, will be 0 anyway, and we need an update
        }

        long diff = player.world.getGameTime() - reloadStateStart;
        if (diff >= reloadStateDuration)
        {
            if (reloadState == ReloadState.OPEN_BREACH)
            {
                newMag = findBullets(player, gun, 1, false);
                setNewReloadStateConditional(player.world, !newMag.isEmpty(), ReloadState.LOAD_FIRST, ReloadState.CLOSE_BREACH);
            }
            else if (reloadState == ReloadState.LOAD_FIRST)
            {
                bulletsLoaded++; //Magless shotguns can't chamber an additional round

                setNewReloadState(player.world, ReloadState.CLOSE_BREACH);
            }
            else if (reloadState == ReloadState.CLOSE_BREACH)
            {
                boolean canContinue = !findBullets(player, gun, 1, true).isEmpty() && bulletsLoaded < gun.getMagCapacity();
                setNewReloadStateConditional(player.world, canContinue, ReloadState.LOAD);
            }
            else if (reloadState == ReloadState.LOAD)
            {
                bulletsLoaded++;

                boolean continueLoad = !findBullets(player, gun, 1, true).isEmpty() && bulletsLoaded < gun.getMagCapacity();
                setNewReloadStateConditional(player.world, continueLoad, ReloadState.LOAD, ReloadState.NONE);

                if (continueLoad) { newMag = findBullets(player, gun, 1, false); }
                else { newMag = ItemStack.EMPTY; }
            }
            return true;
        }
        return false;
    }

    private boolean tickReloadBoltAction(PlayerEntity player)
    {
        if (reloadStateStart == 0)
        {
            if (reloadState == ReloadState.MAG_IN)
            {
                newMag = findNewMag(player, gun, false);
                if (newMag.isEmpty())
                {
                    //If the new mag somehow got lost since last tick, abort here
                    resetReloadState();
                    return false;
                }
            }

            reloadStateStart = player.world.getGameTime();
            reloadStateDuration = gun.getReloadStateTime(reloadState);

            return true; //Can just return here, no point in checking the diff, will be 0 anyway and we need an update
        }

        long diff = player.world.getGameTime() - reloadStateStart;
        if (diff >= reloadStateDuration)
        {
            if (reloadState == ReloadState.OPEN_BREACH)
            {
                setNewReloadState(player.world, ReloadState.MAG_OUT);
            }
            else if (reloadState == ReloadState.MAG_OUT)
            {
                newMag = findNewMag(player, gun, false);
                //If a new mag was found, continue reloading, else abort here
                setNewReloadStateConditional(player.world, !newMag.isEmpty(), ReloadState.MAG_IN, ReloadState.CLOSE_BREACH);

                //Chamber additional bullet
                if (bulletsLoaded > 0 && !chambered && gun.canChamberAdditionalBullet())
                {
                    bulletsLoaded--;
                    chambered = true;
                }

                //Search new mag first to avoid reloading the same mag again if it is not empty
                ItemStack magStack = new ItemStack(R6Content.itemMagazines.get(gun.getMagazine()));
                magStack.setDamage(bulletsLoaded);
                player.inventory.addItemStackToInventory(magStack);

                magLoaded = false;
                bulletsLoaded = 0;
            }
            else if (reloadState == ReloadState.MAG_IN)
            {
                magLoaded = true;
                bulletsLoaded = newMag.getDamage();
                newMag = ItemStack.EMPTY;

                //If no bullet is chambered, the breach needs to be closed, else we are done
                setNewReloadStateConditional(player.world, !chambered, ReloadState.CLOSE_BREACH);
            }
            else
            {
                resetReloadState();
            }
        }
        return false;
    }

    private void setNewReloadState(World world, ReloadState newState)
    {
        if (newState == ReloadState.NONE) { resetReloadState(); }
        else
        {
            reloadStateStart = world.getGameTime();
            reloadState = newState;
            reloadStateDuration = gun.getReloadStateTime(reloadState);
        }
    }

    private void setNewReloadStateConditional(World world, boolean condition, ReloadState newState)
    {
        if (condition)
        {
            reloadStateStart = world.getGameTime();
            reloadState = newState;
            reloadStateDuration = gun.getReloadStateTime(reloadState);
        }
        else { resetReloadState(); }
    }

    private void setNewReloadStateConditional(World world, boolean condition, ReloadState newState, ReloadState alternative)
    {
        if (alternative == ReloadState.NONE) { setNewReloadStateConditional(world, condition, newState); }
        else
        {
            if (condition) { reloadState = newState; }
            else { reloadState = alternative; }

            reloadStateStart = world.getGameTime();
            reloadStateDuration = gun.getReloadStateTime(reloadState);
        }
    }

    private void resetGunState(boolean resetAim)
    {
        firing = false;
        if (resetAim) { aiming = false; }
        burstShotsFired = 0;
    }

    private void resetReloadState()
    {
        reloading = false;
        reloadStateStart = 0;
        reloadState = ReloadState.NONE;
    }

    private void cancelReloading(PlayerEntity player, int slot)
    {
        reloading = false;
        reloadStateStart = 0;

        if (reloadState == ReloadState.MAG_IN || reloadState == ReloadState.LOAD)
        {
            player.inventory.addItemStackToInventory(newMag);
            newMag = ItemStack.EMPTY;
        }

        if (reloadState.canAbort()) { reloadState = ReloadState.NONE; }

        sendReloadStateUpdate((ServerPlayerEntity)player, slot);
    }

    private static ItemStack findNewMag(PlayerEntity player, EnumGun gun, boolean simulate)
    {
        if (gun.hasMag())
        {
            for (int i = 0; i < player.inventory.getSizeInventory(); i++)
            {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack.getItem() instanceof ItemMagazine)
                {
                    EnumMagazine type = ((ItemMagazine) stack.getItem()).getMagazine();
                    if (type == gun.getMagazine() && stack.getDamage() > 0) //Only reload if there is ammo in the magazine
                    {
                        return simulate ? stack : player.inventory.removeStackFromSlot(i);
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack findBullets(PlayerEntity player, EnumGun gun, int count, boolean simulate)
    {
        if (!gun.hasMag())
        {
            for (int i = 0; i < player.inventory.getSizeInventory(); i++)
            {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack.getItem() instanceof ItemBullet)
                {
                    EnumBullet bullet = ((ItemBullet)stack.getItem()).getBullet();
                    if (bullet == gun.getAmmoType() && stack.getCount() >= count)
                    {
                        return simulate ? stack : player.inventory.decrStackSize(i, count);
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }

    private void playSound(PlayerEntity player, GunSoundType type)
    {
        SoundEvent event = R6SoundEvents.getGunSound(gun, type); //FIXME: sound doesn't play when playing on dedicated server
        player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), event, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    private boolean isPumpActionOrBoltAction()
    {
        if (gun.getGunType() == EnumGun.Type.SHOTGUN)
        {
            return !gun.hasMag() && gun != EnumGun.BOSG_12_2;
        }
        return gun == EnumGun.CSRX_300;
    }

    private void sendGunStatusUpdate(ServerPlayerEntity player, int slot)
    {
        NetworkHandler.sendToPlayer(new PacketGunState(slot, gun, aiming, chambered, charged, chargeStart, magLoaded, bulletsLoaded), player);
    }

    private void sendReloadStateUpdate(ServerPlayerEntity player, int slot)
    {
        NetworkHandler.sendToPlayer(new PacketReloadState(slot, gun, reloading, reloadState, reloadStateStart), player);
    }

    /*
     * Getters
     */

    @Override
    public boolean isAiming() { return aiming; }

    @Override
    public boolean isFiring() { return firing; }

    @Override
    public boolean isCharged() { return charged; }

    @Override
    public boolean isLoaded() { return magLoaded; }

    private int getLoadedBulletsInternal()
    {
        if (gun.hasMag() && !magLoaded) { return 0; }
        return bulletsLoaded + (chambered ? 1 : 0);
    }
    
    @Override
    public int getLoadedBullets()
    {
        if (reloadState != ReloadState.NONE) { return 0; }
        if (gun.hasMag() && !magLoaded && !chambered) { return 0; }
        return bulletsLoaded + (chambered ? 1 : 0);
    }

    @Override
    public List<EnumAttachment> getAttachments() { return attachments; }

    @Override
    public boolean isAttachmentActive(EnumAttachment attachment)
    {
        if (attachment.getType() != EnumAttachment.Type.SPECIAL) { return false; }
        if (!attachments.contains(attachment)) { return false; }
        return specialAttachmentActive;
    }

    @Override
    public float getChargeState(long gameTime)
    {
        if (charged || !isPumpActionOrBoltAction()) { return 0; }

        float diff = (float)(gameTime - chargeStart);
        return MathHelper.clamp(diff / (float)gun.getChargeTime(), 0F, 1F);
    }

    @Override
    public float getAimState(long gameTime, float partialTicks)
    {
        long diff = gameTime - aimStateChangeClient;
        if (diff > adsTime) { return aiming ? 1F : 0F; }
        float state = ((float)diff + partialTicks) / (float)adsTime;
        return MathHelper.clamp(aiming ? state : 1F - state, 0F, 1F);
    }

    @Override
    public ReloadState getReloadState() { return reloadState; }

    @Override
    public float getReloadStateProgress(long gameTime)
    {
        if (!reloading) { return 0F; }

        float diff = (float)(gameTime - reloadStateStart);
        return MathHelper.clamp(diff / (float)reloadStateDuration, 0F, 1F);
    }

    @Override
    public EnumGun getGun() { return gun; }

    /*
     * Packet handlers
     */

    @Override
    public void handleFiringPacket(PlayerEntity player, boolean mouseDown)
    {
        if (reloading)
        {
            if (mouseDown && reloadState.canAbort())
            {
                cancelReloading(player, player.inventory.currentItem);
                firing = true;
            }
        }
        else
        {
            firing = mouseDown;

            if (mouseDown && getLoadedBulletsInternal() == 0) { playSound(player, GunSoundType.EMPTY_TRIGGER); }
            if (!mouseDown) { burstShotsFired = 0; }
        }
    }

    @Override
    public void handleAimingPacket(PlayerEntity player, boolean mouseDown, boolean holdToAim)
    {
        if (reloading)
        {
            if (mouseDown && reloadState.canAbort())
            {
                cancelReloading(player, player.inventory.currentItem);
                aiming = true;
                sendGunStatusUpdate((ServerPlayerEntity) player, player.inventory.currentItem);
            }
        }
        else
        {
            if (holdToAim) { aiming = mouseDown; }
            else if (mouseDown) { aiming = !aiming; }

            if (aiming) { player.setSprinting(false); }
            sendGunStatusUpdate((ServerPlayerEntity) player, player.inventory.currentItem);
        }
    }

    @Override
    public void handleCancelPacket(PlayerEntity player)
    {
        if (firing || aiming)
        {
            resetGunState(true);
            sendGunStatusUpdate((ServerPlayerEntity)player, player.inventory.currentItem);
        }
    }

    @Override
    public void handleGunStatePacket(boolean aiming, boolean chambered, boolean charged, long chargeStart, boolean loaded, int bulletsLoaded)
    {
        if (this.aiming != aiming)
        {
            this.aiming = aiming;
            aimStateChangeClient = R6Mod.getSidedHelper().getWorld().getGameTime();
        }
        this.chambered = chambered;
        this.charged = charged;
        this.chargeStart = chargeStart;
        this.magLoaded = loaded;
        this.bulletsLoaded = bulletsLoaded;
    }

    @Override
    public void handleReloadStatePacket(boolean reloading, ReloadState state, long stateStart)
    {
        this.reloading = reloading;
        this.reloadState = state;
        this.reloadStateStart = stateStart;
        this.reloadStateDuration = gun.getReloadStateTime(state);
    }

    /*
     * NBT stuff
     */

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("gun", gun != null ? gun.ordinal() : -1);
        nbt.putInt("firemode", firemode.ordinal());
        nbt.putBoolean("loaded", magLoaded);
        nbt.putInt("bullets", bulletsLoaded);
        nbt.put("newMag", newMag.write(new CompoundNBT()));
        nbt.putInt("reloadState", reloadState.ordinal());
        nbt.putBoolean("chambered", chambered);
        nbt.putBoolean("charged", charged);
        nbt.putBoolean("attachmentsChanged", attachmentsChanged);
        nbt.putFloat("spread", spread);
        nbt.putFloat("recoil", recoil);
        nbt.putInt("adsTime", adsTime);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        gun = EnumGun.values()[nbt.getInt("gun")];
        firemode = Firemode.values()[nbt.getInt("firemode")];
        magLoaded = nbt.getBoolean("loaded");
        bulletsLoaded = nbt.getInt("bullets");
        newMag = ItemStack.read(nbt.getCompound("newMag"));
        reloadState = ReloadState.values()[nbt.getInt("reloadState")];
        chambered = nbt.getBoolean("chambered");
        charged = nbt.getBoolean("charged");
        attachmentsChanged = nbt.getBoolean("attachmentsChanged");
        spread = nbt.getFloat("spread");
        recoil = nbt.getFloat("recoil");
        adsTime = nbt.getInt("adsTime");
    }

    @SuppressWarnings("ConstantConditions")
    public static ICapabilityGun getFrom(ItemStack stack) { return stack.getCapability(GUN_CAPABILITY).orElse(DUMMY); }

    public static class Empty implements ICapabilityGun
    {
        @Override
        public void tick(PlayerEntity player, int slot, boolean selected) { }

        @Override
        public void reload(PlayerEntity player) { }

        @Override
        public void restock(PlayerEntity player, int slot) { }

        @Override
        public boolean isAiming() { return false; }

        @Override
        public boolean isFiring() { return false; }

        @Override
        public boolean isCharged() { return false; }

        @Override
        public boolean isLoaded() { return false; }

        @Override
        public int getLoadedBullets() { return 0; }

        @Override
        public List<EnumAttachment> getAttachments() { return Collections.emptyList(); }

        @Override
        public boolean isAttachmentActive(EnumAttachment attachment) { return false; }

        @Override
        public float getChargeState(long gameTime) { return 0; }

        @Override
        public float getAimState(long gameTime, float partialTicks) { return 0; }

        @Override
        public ReloadState getReloadState() { return ReloadState.NONE; }

        @Override
        public float getReloadStateProgress(long gameTime) { return 0; }

        @Override
        public EnumGun getGun() { return EnumGun.L85A2; }

        @Override
        public void handleFiringPacket(PlayerEntity player, boolean mouseDown) { }

        @Override
        public void handleAimingPacket(PlayerEntity player, boolean mouseDown, boolean holdToAim) { }

        @Override
        public void handleCancelPacket(PlayerEntity player) { }

        @Override
        public void handleGunStatePacket(boolean aiming, boolean chambered, boolean charged, long chargeStart, boolean loaded, int bulletsLoaded) { }

        @Override
        public void handleReloadStatePacket(boolean reloading, ReloadState state, long stateStart) { }

        @Override
        public CompoundNBT serializeNBT() { return null; }

        @Override
        public void deserializeNBT(CompoundNBT nbt) { }
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundNBT>
    {
        private final ICapabilityGun instance;

        public Provider(ItemStack stack) { instance = new CapabilityGun(stack); }

        @Nonnull
        @Override
        @SuppressWarnings({"ConstantConditions", "unchecked"})
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
        {
            //Make sure to only return an instance if the capability is actually registered
            if (GUN_CAPABILITY == null) { return LazyOptional.empty(); }

            if (cap != GUN_CAPABILITY) { return LazyOptional.empty(); }
            return LazyOptional.of(() -> (T)instance);
        }

        @Override
        public CompoundNBT serializeNBT() { return instance.serializeNBT(); }

        @Override
        public void deserializeNBT(CompoundNBT nbt) { instance.deserializeNBT(nbt); }
    }

    public static class Storage implements Capability.IStorage<ICapabilityGun>
    {
        @Nullable
        @Override
        public INBT writeNBT(Capability<ICapabilityGun> capability, ICapabilityGun instance, Direction side)
        {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<ICapabilityGun> capability, ICapabilityGun instance, Direction side, INBT nbt)
        {
            instance.deserializeNBT((CompoundNBT) nbt);
        }
    }
}
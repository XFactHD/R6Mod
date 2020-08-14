package xfacthd.r6mod.common.util.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import xfacthd.r6mod.common.capability.CapabilityDBNO;
import xfacthd.r6mod.common.capability.CapabilityGun;
import xfacthd.r6mod.common.data.PointContext;
import xfacthd.r6mod.common.data.itemsubtypes.EnumAttachment;
import xfacthd.r6mod.common.data.itemsubtypes.EnumGadget;
import xfacthd.r6mod.common.items.gun.ItemGun;
import xfacthd.r6mod.common.net.NetworkHandler;
import xfacthd.r6mod.common.net.packets.match.PacketPointInfo;
import xfacthd.r6mod.common.util.damage.DamageSourceGadget;
import xfacthd.r6mod.common.util.damage.DamageSourceGun;

import java.util.ArrayList;
import java.util.List;

public class PointManager
{
    public static void awardPlayerInjure(PlayerEntity shooter, PlayerEntity victim, DamageSource source)
    {
        if (shooter.getTeam() != null && victim.getTeam() != null)
        {
            boolean friendly = shooter.getTeam().isSameTeam(victim.getTeam());
            int points = friendly ? -50 : 50;
            List<ExtraPointsEntry> extraPoints = friendly ? null : getExtraPoints(shooter, source);

            if (source instanceof DamageSourceGadget)
            {
                EnumGadget gadget = ((DamageSourceGadget)source).getGadget();
                NetworkHandler.sendToPlayer(new PacketPointInfo(PointContext.INJURE, gadget, points, extraPoints), (ServerPlayerEntity) shooter);
            }
            else
            {
                NetworkHandler.sendToPlayer(new PacketPointInfo(PointContext.INJURE, points, extraPoints), (ServerPlayerEntity) shooter);
            }

            if (extraPoints != null)
            {
                for (ExtraPointsEntry e : extraPoints) { points += e.getPoints(); }
            }
            int finalPoints = points; //Needed to satisfy the lambda
            shooter.getWorldScoreboard().forAllObjectives(R6Command.POINTS, shooter.getScoreboardName(), (score) -> score.increaseScore(finalPoints));
        }
    }

    public static void awardPlayerKill(PlayerEntity shooter, PlayerEntity victim, DamageSource source)
    {
        if (shooter.getTeam() != null && victim.getTeam() != null)
        {
            boolean friendly = shooter.getTeam().isSameTeam(victim.getTeam());
            boolean confirm = CapabilityDBNO.getFrom(victim).isDBNO(); //True, if the victim is injured
            int points = friendly ? (confirm ? -50 : -100) : (confirm ? 50 : 100);
            List<ExtraPointsEntry> extraPoints = friendly || confirm ? null : getExtraPoints(shooter, source);

            if (source instanceof DamageSourceGadget)
            {
                EnumGadget gadget = ((DamageSourceGadget)source).getGadget();
                NetworkHandler.sendToPlayer(new PacketPointInfo(PointContext.KILL, gadget, points, extraPoints), (ServerPlayerEntity) shooter);
            }
            else
            {
                NetworkHandler.sendToPlayer(new PacketPointInfo(PointContext.KILL, points, extraPoints), (ServerPlayerEntity) shooter);
            }

            if (extraPoints != null)
            {
                for (ExtraPointsEntry e : extraPoints) { points += e.getPoints(); }
            }
            int finalPoints = points; //Needed to satisfy the lambda
            shooter.getWorldScoreboard().forAllObjectives(R6Command.POINTS, shooter.getScoreboardName(), (score) -> score.increaseScore(finalPoints));
        }
    }

    public static void awardPlayerDamage(PlayerEntity shooter, PlayerEntity victim)
    {
        if (shooter.getTeam() != null && victim.getTeam() != null)
        {
            if (shooter.getTeam().isSameTeam(victim.getTeam()))
            {
                shooter.getWorldScoreboard().forAllObjectives(R6Command.POINTS, shooter.getScoreboardName(), (score) -> score.increaseScore(-10));
                NetworkHandler.sendToPlayer(new PacketPointInfo(PointContext.TEAM_DAMAGE, -10, null), (ServerPlayerEntity)shooter);
            }
        }
    }

    public static void awardGadgetDestroyed(EnumGadget gadget, PlayerEntity shooter, String team)
    {
        if (shooter.getTeam() != null && !team.equals("null"))
        {
            boolean friendly = shooter.getTeam().getName().equals(team);
            int points = friendly ? -10 : 10;
            shooter.getWorldScoreboard().forAllObjectives(R6Command.POINTS, shooter.getScoreboardName(), (score) -> score.increaseScore(points));

            NetworkHandler.sendToPlayer(new PacketPointInfo(PointContext.GADGET_DESTROY, gadget, points, null), (ServerPlayerEntity)shooter);
        }
    }

    public static void awardGadgetDestroyed(EnumGadget gadget, PlayerEntity shooter)
    {
        if (shooter.getTeam() != null)
        {
            int points = 10;
            shooter.getWorldScoreboard().forAllObjectives(R6Command.POINTS, shooter.getScoreboardName(), (score) -> score.increaseScore(points));

            NetworkHandler.sendToPlayer(new PacketPointInfo(PointContext.GADGET_DESTROY, gadget, points, null), (ServerPlayerEntity)shooter);
        }
    }

    public static void awardGadgetDisabled(EnumGadget gadget, PlayerEntity shooter, String team)
    {
        if (shooter.getTeam() != null && !team.equals("null"))
        {
            boolean friendly = shooter.getTeam().getName().equals(team);
            int points = friendly ? -10 : 10;
            shooter.getWorldScoreboard().forAllObjectives(R6Command.POINTS, shooter.getScoreboardName(), (score) -> score.increaseScore(points));

            NetworkHandler.sendToPlayer(new PacketPointInfo(PointContext.GADGET_DISABLE, gadget, points, null), (ServerPlayerEntity)shooter);
        }
    }

    public static void awardGadgetUse(EnumGadget gadget, PlayerEntity user, int points)
    {
        if (user.getTeam() != null)
        {
            user.getWorldScoreboard().forAllObjectives(R6Command.POINTS, user.getScoreboardName(), (score) -> score.increaseScore(10));

            NetworkHandler.sendToPlayer(new PacketPointInfo(PointContext.GADGET_USE, gadget, points, null), (ServerPlayerEntity)user);
        }
    }



    private static List<ExtraPointsEntry> getExtraPoints(PlayerEntity shooter, DamageSource source)
    {
        boolean repelling = false; //TODO: add when repelling is implemented

        boolean gunSilenced = source instanceof DamageSourceGun &&
                shooter.getHeldItemMainhand().getItem() instanceof ItemGun &&
                CapabilityGun.getFrom(shooter.getHeldItemMainhand()).getAttachments().contains(EnumAttachment.SUPPRESSOR);

        List<ExtraPointsEntry> extraPoints = new ArrayList<>();
        if (source instanceof DamageSourceGadget) { extraPoints.add(new ExtraPointsEntry(PointContext.GADGET, 10)); }
        if (repelling) { extraPoints.add(new ExtraPointsEntry(PointContext.REPELLING, 10)); }
        if (gunSilenced) { extraPoints.add(new ExtraPointsEntry(PointContext.SILENCED, 10)); }

        return extraPoints;
    }
}
package xfacthd.r6mod.api.capability;

public interface ICapabilityEffect
{
    void tick();

    float onPlayerAttacked(float dmg);

    void addFinkaBoost();

    void removeFinkaBoost();

    void applyStimShot();

    void setBoostPoolClient(float pool);

    void setStimPoolClient(float pool);

    float getBoostPool();

    float getStimPool();

    void invalidate();
}
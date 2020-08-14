package xfacthd.r6mod.common.data;

import net.minecraft.util.ActionResultType;

public enum InteractState
{
    FAILED,
    IN_PROGRESS,
    SUCCESS;

    public ActionResultType toActionResultType()
    {
        switch (this)
        {
            case FAILED: return ActionResultType.FAIL;
            case IN_PROGRESS:
            case SUCCESS:
            {
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }
}
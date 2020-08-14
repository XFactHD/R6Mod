package xfacthd.r6mod.common.util;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector
{
    @Override
    public void connect()
    {
        Mixins.addConfigurations("r6mod.mixins.json");
    }
}
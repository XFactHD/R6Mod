package xfacthd.r6mod.api.interaction;

//Same functionality as IHardDestructible, but limited to soft blocks.
//Blocks implementing IDestructable are inherently also IHardDestructable
public interface IDestructable extends IHardDestructable { }
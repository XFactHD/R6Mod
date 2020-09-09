//TODO: remove when https://github.com/MinecraftForge/MinecraftForge/pull/7216 is merged (save somewhere for future reference)

function initializeCoreMod()
{
    //print("Coremod hello world!");
    return {
        'r6mod worldrenderer render local player': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.WorldRenderer',
                'methodName': 'func_228426_a_', //updateCameraAndRender
                'methodDesc': '(Lcom/mojang/blaze3d/matrix/MatrixStack;FJZLnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/util/math/vector/Matrix4f;)V'
            },
            'transformer': function (method) {
                //print("Getting ASMAPI")
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                //print("Getting Opcodes")
                var opcodes = Java.type('org.objectweb.asm.Opcodes')

                //print("Getting InsnNode types")
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode')
                var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode')
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode')

                //print("Getting instruction list")
                var insnList = method.instructions

                //print("Building opcodeList")
                var opcodeList = ASMAPI.listOf(
                    new VarInsnNode(opcodes.ALOAD, 0),
                    new FieldInsnNode(opcodes.GETFIELD, "net/minecraft/client/renderer/WorldRenderer", "mc"/*"field_72777_q"*/, "Lnet/minecraft/client/Minecraft;"),
                    new FieldInsnNode(opcodes.GETFIELD, "net/minecraft/client/Minecraft", "player"/*"field_71439_g"*/, "Lnet/minecraft/client/entity/player/ClientPlayerEntity;"),
                    ASMAPI.buildMethodCall(
                        "net/minecraft/client/entity/player/ClientPlayerEntity",
                        "isSpectator", //"func_175149_v",
                        "()Z",
                        ASMAPI.MethodType.VIRTUAL
                    ),
                    new InsnNode(opcodes.IAND)
                );

                //print("Searching injection point")
                var targetInsn = null
                var i
                for (i = 0; i < insnList.size(); i++) {
                    var node = insnList.get(i);
                    if (node.getOpcode() === opcodes.INSTANCEOF) {
                        if (node.desc === "net/minecraft/client/entity/player/ClientPlayerEntity") {
                            targetInsn = node
                            break
                        }
                    }
                }

                //print("Checking if injection point found")
                if (targetInsn === null) {
                    throw "Failed to find instanceof statement!"
                }

                //print("Applying transformation")
                insnList.insert(targetInsn, opcodeList)

                //print("Done")
                return method
            }
        }
    }
}
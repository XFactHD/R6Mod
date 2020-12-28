package xfacthd.r6mod.common.datagen.providers.model.item;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.r6mod.common.data.itemsubtypes.*;

import java.util.Locale;

public class SimpleItemModelProvider extends R6ItemModelProvider
{
    public SimpleItemModelProvider(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        super(generator, fileHelper, "simple_item_models");
    }

    @Override
    protected void registerModels()
    {
        singleTexture("block_barbed_wire", mcLoc("item/generated"), "layer0", modLoc("block/gadget/block_barbed_wire"));
        singleTexture("block_barricade", mcLoc("item/generated"), "layer0", modLoc("item/building/item_block_barricade"));
        singleTexture("block_breach_charge", mcLoc("item/generated"), "layer0", modLoc("block/gadget/block_breach_charge"));
        singleTexture("block_thermite_charge", mcLoc("item/generated"), "layer0", modLoc("block/gadget/block_thermite_charge"));
        singleTexture("block_tough_barricade", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_block_tough_barricade"));
        singleTexture("block_welcome_mat", mcLoc("item/generated"), "layer0", modLoc("block/gadget/block_welcome_mat"));
        singleTexture("item_activator", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_activator"));
        //singleTexture("item_emp_grenade", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_emp_grenade"));
        singleTexture("item_frag_grenade", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_frag_grenade"));
        singleTexture("item_frag_grenade_active", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_frag_grenade_active"));
        //singleTexture("item_gas_canister", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_gas_canister"));
        singleTexture("item_impact_grenade", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_impact_grenade"));
        singleTexture("item_material_casing", mcLoc("item/generated"), "layer0", modLoc("item/material/item_material_casing"));
        singleTexture("item_material_ingot_brass", mcLoc("item/generated"), "layer0", modLoc("item/material/item_material_ingot_brass"));
        singleTexture("item_material_projectile", mcLoc("item/generated"), "layer0", modLoc("item/material/item_material_projectile"));
        singleTexture("item_nitro_phone", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_nitro_phone"));
        singleTexture("item_nitro_phone_active", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_nitro_phone_active"));
        singleTexture("item_phone", mcLoc("item/generated"), "layer0", modLoc("item/item_phone"));
        singleTexture("item_phone_hacked", mcLoc("item/generated"), "layer0", modLoc("item/item_phone_hacked"));
        singleTexture("item_reinforcement", mcLoc("item/generated"), "layer0", modLoc("item/building/item_reinforcement"));
        singleTexture("item_smoke_grenade", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_smoke_grenade"));
        singleTexture("item_stim_dart", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_stim_dart"));
        singleTexture("item_stun_grenade", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_stun_grenade"));
        singleTexture("item_ying_glasses", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_ying_glasses"));
        singleTexture("item_yokai_drone", mcLoc("item/generated"), "layer0", modLoc("item/gadget/item_yokai_drone"));

        for (EnumBullet bullet : EnumBullet.values())
        {
            String name = bullet.toString().toLowerCase(Locale.ROOT);
            singleTexture("item_ammo_" + name, mcLoc("item/generated"), "layer0", modLoc("item/ammo/item_ammo_" + name));
        }

        for (EnumMagazine magazine : EnumMagazine.values())
        {
            if (magazine == EnumMagazine.NONE) { continue; }
            singleTexture(magazine.toItemName(), mcLoc("item/generated"), "layer0", modLoc(magazine.toTexturePath()));
        }

        for (EnumGadgetAmmo ammo : EnumGadgetAmmo.values())
        {
            singleTexture(ammo.toItemName(), mcLoc("item/generated"), "layer0", modLoc(ammo.toTexturePath()));
        }

        getBuilder("item_candela").parent(new ModelFile.UncheckedModelFile(mcLoc("builtin/entity")));
        ModelFile candela = getExistingFile(modLoc("item/item_candela_0"));
        getBuilder("item_candela_1").parent(candela).texture("front", modLoc("item/gadget/item_candela_front_1"));
        getBuilder("item_candela_2").parent(candela).texture("front", modLoc("item/gadget/item_candela_front_2"));
        getBuilder("item_candela_3").parent(candela).texture("front", modLoc("item/gadget/item_candela_front_3"));
    }
}
package nukkitcoders.mobplugin.entities.animal.walking;

import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.GSPetData;
import nukkitcoders.mobplugin.entities.animal.WalkingAnimal;

public class WanderingTrader extends WalkingAnimal {

    public static final int NETWORK_ID = 118;

    public WanderingTrader(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.95f;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setMaxHealth(20);
    }

    @Override
    public int getKillExperience() {
        return 0;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Wandering Trader";
    }

    @Override
    public int getCost() {return GSPetData.petPrices.get(this.getClass().toString().replace(" ", ""));}

    @Override
    public Location getSpawnLoc() {return GSPetData.petLocs.get(this.getClass().toString().replace(" ", ""));}
}

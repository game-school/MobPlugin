package nukkitcoders.mobplugin.entities.animal.swimming;

import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.GSPetData;
import nukkitcoders.mobplugin.utils.Utils;

public class Cod extends Fish {

    public static final int NETWORK_ID = 112;

    public Cod(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    int getBucketMeta() {
        return 2;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    public float getHeight() {
        return 0.2f;
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(3);
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.RAW_FISH, 0, 1), Item.get(Item.BONE, 0, Utils.rand(0, 2))};
    }

    @Override
    public int getCost() {return GSPetData.petPrices.get(this.getClass().toString().replace(" ", ""));}

    @Override
    public Location getSpawnLoc() {return GSPetData.petLocs.get(this.getClass().toString().replace(" ", ""));}
}

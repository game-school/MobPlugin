package nukkitcoders.mobplugin.entities.monster.swimming;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import nukkitcoders.mobplugin.entities.GSPetData;
import nukkitcoders.mobplugin.entities.animal.swimming.Squid;
import nukkitcoders.mobplugin.entities.monster.SwimmingMonster;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.HashMap;

public class Guardian extends SwimmingMonster {

    public static final int NETWORK_ID = 49;
    private int laserChargeTick = 40;
    private long laserTargetEid = -1;

    public Guardian(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 0.85f;
    }

    @Override
    public float getHeight() {
        return 0.85f;
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setMaxHealth(30);
    }

    @Override
    public boolean targetOption(EntityCreature creature, double distance) {
        if (creature instanceof Player) {
            Player player = (Player) creature;
            return (!player.closed) && player.spawned && player.isAlive() && (player.isSurvival() || player.isAdventure()) && distance <= 100;
        } else if (creature instanceof Squid) {
            return creature.isAlive() && this.distanceSquared(creature) <= 80;
        }
        return false;
    }

    @Override
    public void attackEntity(Entity player) {
        HashMap<EntityDamageEvent.DamageModifier, Float> damage = new HashMap<>();
        damage.put(EntityDamageEvent.DamageModifier.BASE, 8.0F);
        @SuppressWarnings("serial")
        HashMap<Integer, Float> armorValues = new HashMap<Integer, Float>() {
            {
                put(Item.LEATHER_CAP, 1f);
                put(Item.LEATHER_TUNIC, 3f);
                put(Item.LEATHER_PANTS, 2f);
                put(Item.LEATHER_BOOTS, 1f);
                put(Item.CHAIN_HELMET, 1f);
                put(Item.CHAIN_CHESTPLATE, 5f);
                put(Item.CHAIN_LEGGINGS, 4f);
                put(Item.CHAIN_BOOTS, 1f);
                put(Item.GOLD_HELMET, 1f);
                put(Item.GOLD_CHESTPLATE, 5f);
                put(Item.GOLD_LEGGINGS, 3f);
                put(Item.GOLD_BOOTS, 1f);
                put(Item.IRON_HELMET, 2f);
                put(Item.IRON_CHESTPLATE, 6f);
                put(Item.IRON_LEGGINGS, 5f);
                put(Item.IRON_BOOTS, 2f);
                put(Item.DIAMOND_HELMET, 3f);
                put(Item.DIAMOND_CHESTPLATE, 8f);
                put(Item.DIAMOND_LEGGINGS, 6f);
                put(Item.DIAMOND_BOOTS, 3f);
            }
        };

        float points = 0;
        for (Item i : ((Player) player).getInventory().getArmorContents()) {
            points += armorValues.getOrDefault(i.getId(), 0f);
        }

        damage.put(EntityDamageEvent.DamageModifier.ARMOR,
                (float) (damage.getOrDefault(EntityDamageEvent.DamageModifier.ARMOR, 0f) - Math.floor(damage.getOrDefault(EntityDamageEvent.DamageModifier.BASE, 1f) * points * 0.04)));
        player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.DamageCause.MAGIC, damage));

    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        if (getServer().getDifficulty() == 0) {
            this.close();
            return true;
        }

        boolean hasUpdate = super.entityBaseTick(tickDiff);
        if (followTarget != null) {
            if (laserTargetEid !=followTarget.getId()) {
                this.setDataProperty(new LongEntityData(Entity.DATA_TARGET_EID, laserTargetEid = followTarget.getId()));
                laserChargeTick = 40;
            }
            if (targetOption((EntityCreature) followTarget,this.distanceSquared(followTarget))) {
                if (--laserChargeTick < 0) {
                    attackEntity(followTarget);
                    this.setDataProperty(new LongEntityData(Entity.DATA_TARGET_EID, laserTargetEid = -1));
                    laserChargeTick = 40;
                }
            } else {
                this.setDataProperty(new LongEntityData(Entity.DATA_TARGET_EID, laserTargetEid = -1));
                laserChargeTick = 40;
            }
        }
        return hasUpdate;
    }

    @Override
    public Item[] getDrops() {
        return new Item[]{Item.get(Item.PRISMARINE_SHARD, 0, Utils.rand(0, 2))};
    }

    @Override
    public int getKillExperience() {
        return 10;
    }

    @Override
    public int getCost() {return GSPetData.petPrices.get(this.getClass().toString().replace(" ", ""));}

    @Override
    public Location getSpawnLoc() {return GSPetData.petLocs.get(this.getClass().toString().replace(" ", ""));}
}

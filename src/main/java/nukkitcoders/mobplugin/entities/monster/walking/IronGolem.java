package nukkitcoders.mobplugin.entities.monster.walking;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.passive.EntityWolf;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import nukkitcoders.mobplugin.entities.GSPetData;
import nukkitcoders.mobplugin.entities.monster.WalkingMonster;
import nukkitcoders.mobplugin.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IronGolem extends WalkingMonster {

    public static final int NETWORK_ID = 20;

    public IronGolem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.setFriendly(true);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getWidth() {
        return 1.4f;
    }

    @Override
    public float getHeight() {
        return 2.9f;
    }

    @Override
    public double getSpeed() {
        return 0.7;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(100);
        super.initEntity();

        this.setDamage(new float[] { 0, 11, 21, 31 });
        this.setMinDamage(new float[] { 0, 4, 7, 11 });
    }

    public void attackEntity(Entity player) {
        if (this.attackDelay > 40 && this.distanceSquared(player) < 4) {
            this.attackDelay = 0;
            HashMap<EntityDamageEvent.DamageModifier, Float> damage = new HashMap<>();
            damage.put(EntityDamageEvent.DamageModifier.BASE, this.getDamage());

            if (player instanceof Player) {
                HashMap<Integer, Float> armorValues = new ArmorPoints();

                float points = 0;
                for (Item i : ((Player) player).getInventory().getArmorContents()) {
                    points += armorValues.getOrDefault(i.getId(), 0f);
                }
                damage.put(EntityDamageEvent.DamageModifier.ARMOR,
                        (float) (damage.getOrDefault(EntityDamageEvent.DamageModifier.ARMOR, 0f) - Math.floor(damage.getOrDefault(EntityDamageEvent.DamageModifier.BASE, 1f) * points * 0.04)));
            }
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage));
            EntityEventPacket pk = new EntityEventPacket();
            pk.eid = this.getId();
            pk.event = EntityEventPacket.ARM_SWING;
            Server.broadcastPacket(this.getViewers().values(), pk);
        }
    }

    public boolean targetOption(EntityCreature creature, double distance) {
        return (!(creature instanceof Player) || creature.getId() == this.isAngryTo) && !(creature instanceof EntityWolf) && creature.isAlive() && distance <= 100;
    }

    @Override
    public Item[] getDrops() {
        List<Item> drops = new ArrayList<>();

        for (int i = 0; i < Utils.rand(3, 5); i++) {
            drops.add(Item.get(Item.IRON_INGOT, 0, 1));
        }

        for (int i = 0; i < Utils.rand(0, 2); i++) {
            drops.add(Item.get(Item.POPPY, 0, 1));
        }

        return drops.toArray(new Item[0]);
    }

    @Override
    public int getKillExperience() {
        return 0;
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.getNameTag() : "Iron Golem";
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public boolean attack(EntityDamageEvent ev) {
        if (super.attack(ev)) {
            if (ev instanceof EntityDamageByEntityEvent) {
                this.isAngryTo = ((EntityDamageByEntityEvent) ev).getDamager().getId();
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean canTarget(Entity entity) {
        return entity.getId() == this.isAngryTo;
    }

    @Override
    public int getCost() {return GSPetData.petPrices.get(this.getClass().toString().replace(" ", ""));}

    @Override
    public Location getSpawnLoc() {return GSPetData.petLocs.get(this.getClass().toString().replace(" ", ""));}
}

/**
 * LICENSING
 * 
 * This software is copyright by sunkid <sunkid@iminurnetz.com> and is
 * distributed under a dual license:
 * 
 * Non-Commercial Use:
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Commercial Use:
 *    Please contact sunkid@iminurnetz.com
 */
package com.iminurnetz.bukkit.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.iminurnetz.util.StringUtils;

public enum KnownMob implements Serializable {
    CHICKEN,
    COW,
    CREEPER,
    FISH,
    GHAST,
    GIANT,
    PIG,
    PIG_ZOMBIE,
    PLAYER,
    SHEEP,
    SLIME,
    SPIDER,
    SQUID,
    SKELETON,
    WOLF,
    ZOMBIE;
    
    public final static List<KnownMob> monsters = 
        Arrays.asList(CREEPER, GHAST, GIANT, PIG_ZOMBIE, SLIME, SKELETON, SPIDER, ZOMBIE);
    public final static List<KnownMob> animals = 
        Arrays.asList(CHICKEN, COW, FISH, PIG, SHEEP, SQUID, WOLF);
    
    @SuppressWarnings("unchecked")
    public Class<? extends LivingEntity> getEntityClass() {
        Class<? extends LivingEntity> clazz = null;
        String pkg = "org.bukkit.entity.";
        String className = StringUtils.toCamelCase(name());

        try {
            clazz = (Class<? extends LivingEntity>) Class.forName(pkg + className);
        } catch (Exception e) {
            System.err.println("Programming error!");
            e.printStackTrace(System.err);
        }
        
        return clazz;
    }
    
    public static KnownMob getMobForEntity(LivingEntity entity) {
        if (entity == null)
            return null;
        
        for (KnownMob mob : values()) {
            if (mob.getEntityClass().isInstance(entity)) {
                return mob;
            }
        }
        
        return null;
    }

    public static boolean isAnimal(Entity entity) {
        if (entity instanceof LivingEntity) {
            KnownMob mob = getMobForEntity((LivingEntity) entity);
            return animals.contains(mob);
        }
        return false;
    }

    public static boolean isMonster(Entity entity) {
        if (entity instanceof LivingEntity) {
            KnownMob mob = getMobForEntity((LivingEntity) entity);
            return monsters.contains(mob);
        }
        return false;
    }
}

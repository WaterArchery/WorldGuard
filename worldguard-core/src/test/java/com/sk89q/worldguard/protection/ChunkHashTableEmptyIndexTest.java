/*
 * WorldGuard, a suite of tools for Minecraft
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldGuard team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.worldguard.protection;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.index.ChunkHashTable;
import com.sk89q.worldguard.protection.managers.index.PriorityRTreeIndex;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChunkHashTableEmptyIndexTest {

    @Test
    public void emptyChunkStateSeesRegionsAddedLater() throws Exception {
        ChunkHashTable table = new ChunkHashTable(new PriorityRTreeIndex(), "test");
        BlockVector3 point = BlockVector3.at(5, 64, 5);
        table.bias(BlockVector2.at(0, 0));

        assertEquals(Set.of(), containing(table, point));

        ProtectedRegion region = new ProtectedCuboidRegion("region", BlockVector3.at(0, 0, 0), BlockVector3.at(20, 100, 10));
        table.add(region);
        assertEquals(Set.of(region), containing(table, point));

        BlockVector3 nextChunkPoint = BlockVector3.at(18, 64, 5);
        table.bias(BlockVector2.at(1, 0));
        assertTrue(table.awaitCompletion(5, TimeUnit.SECONDS));
        assertEquals(Set.of(region), containing(table, point));
        assertEquals(Set.of(region), containing(table, nextChunkPoint));
    }

    private static Set<ProtectedRegion> containing(ChunkHashTable table, BlockVector3 point) {
        Set<ProtectedRegion> regions = new HashSet<>();
        table.applyContaining(point, regions::add);
        return regions;
    }
}

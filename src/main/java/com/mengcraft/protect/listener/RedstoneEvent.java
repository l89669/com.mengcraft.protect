package com.mengcraft.protect.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneEvent implements Runnable, Listener {

	private final List<Integer> list;
	private final Map<Block, Integer> cache;
	private final int limit;
	private final boolean replace;

	@Override
	public void run() {
		for (Entry<Block, Integer> e : cache.entrySet()) {
			check(e);
		}
		cache.clear();
	}

	private void check(Entry<Block, Integer> e) {
		if (e.getValue() > limit) {
			if (replace) {
				e.getKey().setType(Material.AIR);
			} else {
				e.getKey().breakNaturally();
			}
		}
	}

	@EventHandler
	public void handle(BlockRedstoneEvent event) {
		int id = event.getBlock().getTypeId();
		if (!list.contains(id)) {
			cache(event.getBlock());
		}
	}

	private void cache(Block block) {
		if (cache.get(block) != null) {
			cache.put(block, cache.get(block) + 1);
		} else {
			cache.put(block, 1);
		}
	}

	public RedstoneEvent(Configuration conf) {
		this.list = conf.getIntegerList("manager.redstone.white-list");
		this.cache = new HashMap<>();
		this.limit = conf.getInt("manager.redstone.limit-freq", 35);
		this.replace = conf.getBoolean("manager.redstone.replace");
	}

}

package org.powerbot.script.os;

import java.awt.Color;
import java.lang.ref.SoftReference;

import org.powerbot.bot.os.client.Client;
import org.powerbot.bot.os.client.MRUCache;
import org.powerbot.bot.os.client.NpcConfig;
import org.powerbot.bot.os.client.VarBit;

public class Npc extends Actor implements Identifiable {
	private static final int[] lookup;

	static {
		lookup = new int[32];
		int i = 2;
		for (int j = 0; j < 32; j++) {
			lookup[j] = i - 1;
			i += i;
		}
	}

	public static final Color TARGET_STROKE_COLOR = new Color(255, 0, 255, 15);
	private final SoftReference<org.powerbot.bot.os.client.Npc> npc;

	Npc(final ClientContext ctx, final org.powerbot.bot.os.client.Npc npc) {
		super(ctx);
		this.npc = new SoftReference<org.powerbot.bot.os.client.Npc>(npc);
	}

	@Override
	protected org.powerbot.bot.os.client.Actor getActor() {
		return npc.get();
	}

	@Override
	public String getName() {
		final NpcConfig config = getConfig();
		final String str = config != null ? config.getName() : "";
		return str != null ? str : "";
	}

	@Override
	public int getCombatLevel() {
		final NpcConfig config = getConfig();
		return config != null ? config.getLevel() : -1;
	}

	@Override
	public int getId() {
		final Client client = ctx.client();
		if (client == null) {
			return -1;
		}
		final org.powerbot.bot.os.client.Npc npc = this.npc.get();
		final NpcConfig config = npc != null ? npc.getConfig() : null;
		if (config != null) {
			final int varbit = config.getVarBit(), si = config.getSettingsIndex();
			int index = -1;
			if (varbit != -1) {
				final MRUCache cache = client.getVarBitMRUCache();
				for (final VarBit varBit : new HashTable<VarBit>(cache != null ? cache.getHashTable() : null, VarBit.class)) {
					if (varBit.getId() == varbit) {//TODO
						final int mask = lookup[varBit.getEndBit() - varBit.getStartBit()];
						index = ctx.varpbits.getVarpbit(varBit.getIndex()) >> varBit.getStartBit() & mask;
						break;
					}
				}
			} else if (si != -1) {
				index = ctx.varpbits.getVarpbit(si);
			}
			if (index >= 0) {
				final int[] configs = config.getConfigs();
				if (configs != null && index < configs.length && configs[index] != -1) {
					return configs[index];
				}
			}
			return config.getId();
		}
		return -1;
	}

	public String[] getActions() {
		final NpcConfig config = getConfig();
		final String[] arr = config != null ? config.getActions() : new String[0];
		if (arr == null) {
			return new String[0];
		}
		final String[] arr_ = new String[arr.length];
		int c = 0;
		for (final String str : arr) {
			arr_[c++] = str != null ? str : "";
		}
		return arr_;
	}

	private NpcConfig getConfig() {
		final Client client = ctx.client();
		final org.powerbot.bot.os.client.Npc npc = this.npc.get();
		final NpcConfig config = npc != null ? npc.getConfig() : null;
		if (client == null || config == null) {
			return null;
		}
		final int id = config.getId(), uid = getId();
		if (id != uid) {
			final MRUCache cache = client.getNPCConfigMRUCache();
			for (final NpcConfig c : new HashTable<NpcConfig>(cache != null ? cache.getHashTable() : null, NpcConfig.class)) {
				if (c.getId() == uid) {
					return c;
				}
			}
		}
		return config;
	}

	@Override
	public boolean isValid() {
		final Client client = ctx.client();
		final org.powerbot.bot.os.client.Npc npc = this.npc.get();
		if (client == null || npc == null) {
			return false;
		}
		final org.powerbot.bot.os.client.Npc[] arr = client.getNpcs();
		for (final org.powerbot.bot.os.client.Npc a : arr) {
			if (a == npc) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%s[id=%d/name=%s/level=%d]",
				Npc.class.getName(), getId(), getName(), getCombatLevel());
	}
}

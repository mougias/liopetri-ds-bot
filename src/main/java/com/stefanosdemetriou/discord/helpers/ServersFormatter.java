package com.stefanosdemetriou.discord.helpers;

import java.util.Iterator;

import discord4j.core.object.entity.Guild;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServersFormatter {

	private final Iterator<Guild> guilds;

	@Override
	public String toString() {
		var s = new StringBuilder("Connected servers:\n");
		var i = 1;
		while (this.guilds.hasNext()) {
			var guild = this.guilds.next();
			s.append("  " + i++ + ": " + guild.getName() + "\n");
		}

		return s.toString();
	}
}

package com.stefanosdemetriou.discord.helpers;

import java.util.Iterator;

import com.stefanosdemetriou.discord.radio.StationPlayer;

import discord4j.core.object.entity.Guild;

public final class StringOutputFormatter {

	private StringOutputFormatter() {
	}

	public static String formatRadioStations(Iterator<StationPlayer> players) {
		var s = new StringBuilder("Available stations:\n");
		var i = 1;
		while (players.hasNext()) {
			var player = players.next();

			s.append("  " + i++ + ": " + player.getStationName());
			if (!player.isConnected()) {
				s.append(" (Down)");
			}
			s.append("\n");
		}

		return s.toString();
	}

	public static String formatDiscordServers(Iterator<Guild> guilds) {
		var s = new StringBuilder("Connected servers:\n");
		var i = 1;
		while (guilds.hasNext()) {
			var guild = guilds.next();
			s.append("  " + i++ + ": " + guild.getName() + "\n");
		}

		return s.toString();
	}
}

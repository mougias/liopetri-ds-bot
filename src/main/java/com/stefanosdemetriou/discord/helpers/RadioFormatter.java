package com.stefanosdemetriou.discord.helpers;

import java.util.Iterator;

import com.stefanosdemetriou.discord.radio.StationPlayer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RadioFormatter {

	private final Iterator<StationPlayer> players;

	@Override
	public String toString() {
		var i = 1;
		var s = new StringBuilder("Available stations:\n");
		while (this.players.hasNext()) {
			var player = players.next();

			s.append("  " + i++ + ": " + player.getStationName());
			if (!player.isConnected()) {
				s.append(" (Down)");
			}
			s.append("\n");
		}

		return s.toString();
	}
}

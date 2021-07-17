package com.stefanosdemetriou.discord.shell;

import java.util.Map;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.stefanosdemetriou.discord.helpers.RadioFormatter;
import com.stefanosdemetriou.discord.radio.ServerManager;
import com.stefanosdemetriou.discord.radio.StationPlayerFactory;

import discord4j.core.object.entity.Guild;
import lombok.AllArgsConstructor;

@ShellComponent
@AllArgsConstructor
public class RadioCommands {
	private final StationPlayerFactory stations;
	private final ServerManager serverManager;

	/**
	 * Shell command to list available radio stations.
	 *
	 * @return available stations
	 */
	@ShellMethod(value = "List available radio stations", key = "radio list")
	public String listStations() {
		return new RadioFormatter(this.stations.listPlayers()).toString();
	}

	/**
	 * Shell command to list servers/stations that are currently playing.
	 *
	 * @return currently playing
	 */
	@ShellMethod(value = "List currently playing", key = "radio playing")
	public String listPlaying() {
		var playing = this.serverManager.playing();
		if (playing.isEmpty()) {
			return "Nothing is currently playing";
		}

		var s = new StringBuilder("Currently playing:\n");
		for (Map.Entry<Guild, String> entry : this.serverManager.playing().entrySet()) {
			s.append("  " + entry.getKey().getName() + " : " + entry.getValue());
		}

		return s.toString();
	}
}

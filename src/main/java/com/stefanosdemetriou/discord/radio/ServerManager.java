package com.stefanosdemetriou.discord.radio;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.VoiceConnection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Maintains a map of guilds together with the active voice connections where
 * radio stations are being broadcasted.
 *
 * @author stefanos
 *
 */
@Component
@RequiredArgsConstructor
public class ServerManager {

	private final ServerAudioTransmitterFactory factory;

	private final Map<Guild, VoiceConnection> connections = new HashMap<>();

	@Getter
	private final Map<Guild, String> playing = new HashMap<>();

	/**
	 * Connects the bot the given voice channel and starts broadcasting using the
	 * given player. Since the bot can only connect to one voice channel per guild,
	 * this method takes VoiceChannel as a parameter, but uses the guild to save the
	 * connection for later stopping playback.
	 *
	 * @param channel The voice channel to connect to
	 * @param player  The radio station player
	 */
	public void play(VoiceChannel channel, StationPlayer player) {
		var guild = channel.getGuild().block();
		if (guild == null) {
			return;
		}

		this.stop(guild);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		var connection = channel.join(spec -> spec.setProvider(this.factory.getAudioTransmitter(player))).block();
		if (connection == null) {
			this.connections.remove(guild);
			this.playing.remove(guild);
		} else {
			this.connections.put(guild, connection);
			this.playing.put(guild, player.getStationName());
		}
	}

	/**
	 * Stops the bot from broadcasting to the given guild.
	 *
	 * @param guild
	 */
	public void stop(Guild guild) {
		var connection = connections.get(guild);
		if (connection != null) {
			connection.disconnect().block();
			this.connections.remove(guild);
			this.playing.remove(guild);
		}
	}
}

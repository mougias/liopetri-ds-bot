package com.stefanosdemetriou.discord.radio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.stefanosdemetriou.discord.exceptions.NoSuchStationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages the station players. During initialization it loops through the
 * available radio stations and creates a player for each. The players are
 * always active regardless if any servers a re listening to them or not.
 * Provides access methods to the players.
 *
 * @author stefanos
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StationPlayerFactory implements AudioLoadResultHandler {

	private final AudioPlayerManager manager = new DefaultAudioPlayerManager();

	private final List<StationPlayer> players = new ArrayList<>();

	/**
	 * Init, called when bean is created. Loops through the available stations in
	 * the {@link Station} class and requests the audio player manager to load them.
	 */
	@PostConstruct
	public void init() {
		AudioSourceManagers.registerRemoteSources(this.manager);

		for (var station : Station.values()) {
			var reference = new AudioReference(station.getUrl(), station.toString());
			manager.loadItem(reference, this);
		}
	}

	public Iterator<StationPlayer> listPlayers() {
		return Collections.unmodifiableCollection(this.players).iterator();
	}

	/**
	 * Returns the respective player for the given station number.
	 *
	 * @param num The station number, as given by {@link #listTracksMessage()}
	 * @return The respective {@link StationPlayer}
	 * @throws NoSuchStationException if num is out of bounds
	 */
	public StationPlayer getPlayer(int num) throws NoSuchStationException {
		if (num <= 0 || num > this.players.size()) {
			throw new NoSuchStationException();
		}

		return this.players.get(--num);
	}

	@Override
	public void trackLoaded(AudioTrack track) {
		var player = this.manager.createPlayer();
		player.playTrack(track);

		this.players.add(new StationPlayer(player));
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		// ignore
	}

	@Override
	public void noMatches() {
		// ignore
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		log.error("Failed to load station: " + exception.getMessage());
	}
}

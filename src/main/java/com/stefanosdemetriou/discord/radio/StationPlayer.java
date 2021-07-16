package com.stefanosdemetriou.discord.radio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Station Player. Manages the connection the the radio station, and caches
 * frames read, so they can be distributed to all connected servers.
 *
 * @author stefanos
 *
 */
@RequiredArgsConstructor
public class StationPlayer {

	private final AudioPlayer audioPlayer;

	private AudioFrame frame = null;

	private int reconnectDelay = 1;
	private long reconnectTime = System.currentTimeMillis();

	@Getter
	private volatile boolean connected = true;

	/**
	 * Attempts to fetch a new audio frame from the source, and caches it. Returns
	 * the last frame (either just taken, or previously). Reconnects audio source on
	 * read exception with exponential back-off.
	 *
	 * @return The last audio frame read.
	 */
	public AudioFrame provide() {
		if (!this.connected && this.reconnectTime > System.currentTimeMillis()) {
			return null;
		} else if (!this.connected) {
			// not connected, but reconnect time passed, so try to reconnect
			this.audioPlayer.playTrack(this.audioPlayer.getPlayingTrack().makeClone());
		}

		AudioFrame newFrame = null;

		try {
			newFrame = this.audioPlayer.provide();
			this.connected = true;
			this.reconnectTime = 1;
		} catch (Exception e) {
			// could have disconnected, so restart with exponential backoff
			this.connected = false;
			this.reconnectTime = System.currentTimeMillis() + this.reconnectDelay * 1000;
			this.reconnectDelay = this.reconnectDelay < 256 ? this.reconnectDelay * 2 : this.reconnectDelay;
		}

		if (newFrame != null) {
			this.frame = newFrame;
		}

		return this.frame;
	}

	/**
	 * Returns the station name, read from the source audio track.
	 */
	public String getStationName() {
		return this.audioPlayer.getPlayingTrack().getInfo().author;
	}
}

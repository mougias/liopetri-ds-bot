package com.stefanosdemetriou.discord.radio;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Factory class to create new {@link ServerAudioTransmitter} for radio station
 * players.
 *
 * @author stefanos
 *
 */
@Component
@RequiredArgsConstructor
public class ServerAudioTransmitterFactory {

	/**
	 * Creates a new {@link ServerAudioTransmitter} for the station with the given
	 * station number, as given by {@link StationPlayerFactory#listTracksMessage()}
	 * 
	 * @param player The station player
	 * @return A {@link ServerAudioTransmitter}
	 *
	 */
	public ServerAudioTransmitter getAudioTransmitter(StationPlayer player) {
		return new ServerAudioTransmitter(player);
	}
}

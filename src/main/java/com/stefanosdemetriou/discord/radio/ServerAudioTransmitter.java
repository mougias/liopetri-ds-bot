package com.stefanosdemetriou.discord.radio;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import discord4j.voice.AudioProvider;

/**
 * Class to manage transmission of data from a station player to a discord voice
 * channel.
 *
 * @author stefanos
 *
 */
public class ServerAudioTransmitter extends AudioProvider {

	private final StationPlayer player;
	private final MutableAudioFrame frame = new MutableAudioFrame();

	private AudioFrame lastFrame;

	public ServerAudioTransmitter(StationPlayer player) {
		super(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()));

		frame.setBuffer(getBuffer());
		this.player = player;
	}

	@Override
	public boolean provide() {
		var newFrame = this.player.provide();

		if (newFrame != null && newFrame != lastFrame) {
			this.lastFrame = newFrame;

			if (this.getBuffer().capacity() - this.getBuffer().position() > newFrame.getDataLength()) {
				this.getBuffer().put(newFrame.getData(), this.getBuffer().position(), newFrame.getDataLength());
				this.getBuffer().flip();

				return true;
			}
		}

		return false;
	}

}

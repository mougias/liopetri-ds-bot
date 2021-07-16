package com.stefanosdemetriou.discord.events.discord;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.stefanosdemetriou.discord.exceptions.NoSuchStationException;
import com.stefanosdemetriou.discord.helpers.RadioFormatter;
import com.stefanosdemetriou.discord.messaging.MessageFactory;
import com.stefanosdemetriou.discord.radio.ServerManager;
import com.stefanosdemetriou.discord.radio.StationPlayer;
import com.stefanosdemetriou.discord.radio.StationPlayerFactory;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MessageEventListener implements EventListener<MessageCreateEvent> {

	private final MessageFactory messageFactory;
	private final StationPlayerFactory stations;
	private final ServerManager serverRadioManager;

	@Override
	public Class<MessageCreateEvent> getEventType() {
		return MessageCreateEvent.class;
	}

	@Override
	public Mono<Void> execute(MessageCreateEvent event) {
		String[] message = event.getMessage().getContent().split(" ");
		if (message.length <= 1 || !"!liopetri".equals(message[0])) {
			return Mono.empty();
		}

		var channel = event.getMessage().getChannel().block();
		var user = event.getMember();

		switch (message[1]) {
		case "message":
			if (channel != null) {
				channel.createMessage(this.messageFactory.random()).block();
			}
			break;
		case "radio":
			this.handleRadioCommand(user, channel, Arrays.copyOfRange(message, 2, message.length));
			break;
		default:
			break;
		}

		return Mono.empty();
	}

	private void handleRadioCommand(Optional<Member> user, MessageChannel channel, String[] message) {
		if (message == null || message.length == 0) {
			channel.createMessage("Available commands: list, play, help").block();
			return;
		}

		switch (message[0]) {
		case "list":
			if (channel != null) {
				channel.createMessage(new RadioFormatter(this.stations.listPlayers()).toString()).block();
			}
			break;
		case "play":
			var response = this.playRadio(user, message);
			if (response.isPresent()) {
				channel.createMessage(response.get());
			}
			break;
		case "stop":
			this.stopRadio(user);
			break;
		default:
			if (channel != null) {
				channel.createMessage("Available commands: list, play, stop, help").block();
			}
			break;
		}
	}

	private Optional<String> playRadio(Optional<Member> user, String[] message) {
		if (user.isEmpty()) {
			return Optional.empty();
		}
		if (message == null || message.length != 2) {
			return Optional.of("Usage: radio play <station>");
		}

		StationPlayer station = null;
		try {
			var num = Integer.parseInt(message[1]);
			station = this.stations.getPlayer(num);
		} catch (NumberFormatException e) {
			return Optional.of("Station must be a number. Try `radio list` to get a list of radio stations");
		} catch (NoSuchStationException e) {
			return Optional.of("No such station. Try `radio list` to get a list of radio stations");
		}

		var voiceState = user.get().getVoiceState().block();
		VoiceChannel voiceChannel = null;
		if (voiceState != null) {
			voiceChannel = voiceState.getChannel().block();
		}

		if (voiceChannel == null) {
			return Optional.of("You must be in a voice channel to start radio");
		}

		this.serverRadioManager.play(voiceChannel, station);

		return Optional.empty();
	}

	private void stopRadio(Optional<Member> user) {
		if (user.isEmpty()) {
			return;
		}

		var guild = user.get().getGuild().block();
		if (guild == null) {
			return;
		}

		this.serverRadioManager.stop(guild);
	}
}

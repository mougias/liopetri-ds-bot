package com.stefanosdemetriou.discord.messaging;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import discord4j.common.util.Snowflake;
import lombok.Getter;

public class ServerChannelPair {

	private static final LocalDate NW_RELEASE_DATE = LocalDate.parse("20210928",
			DateTimeFormatter.ofPattern("yyyyMMdd"));

	@Getter
	private final Snowflake serverId;

	@Getter
	private final Snowflake channelId;

	private final String lang;

	public ServerChannelPair(long serverId, long channelId, String lang) {
		this.serverId = Snowflake.of(serverId);
		this.channelId = Snowflake.of(channelId);
		this.lang = lang;
	}

	public String getMessage() {
		final var remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), NW_RELEASE_DATE) - 1;
		final var message = new StringBuilder("Good morning team!");

		switch (this.lang.toLowerCase()) {
		case "el":
			this.appendGreek(message, remainingDays);
			break;
		default:
			this.appendEnglish(message, remainingDays);
			break;
		}

		return message.toString();
	}

	private void appendGreek(StringBuilder message, long remainingDays) {
		if (remainingDays == 0) {
			message.append(" je popse!");
		} else {
			message.append(" " + remainingDays + " je popse!");
		}
	}

	private void appendEnglish(StringBuilder message, long remainingDays) {
		if (remainingDays == 0) {
			message.append(" Last day! Get pumped!!");
		} else if (remainingDays == 1) {
			message.append(" " + remainingDays + " day and tonight left!");
		} else {
			message.append(" " + remainingDays + " day and tonight left!");
		}
	}
}

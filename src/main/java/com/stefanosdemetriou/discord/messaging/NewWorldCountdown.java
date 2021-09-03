package com.stefanosdemetriou.discord.messaging;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.jline.utils.Log;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.rest.entity.RestChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewWorldCountdown implements DisposableBean {

	private final GatewayDiscordClient client;
	private final TaskScheduler scheduler;

	private static final long DISCORD_SERVER_ID = 258369598833950723L;
	private static final long DISCORD_CHANNEL_ID = 582582345790521364L;
	private static final LocalDate NW_RELEASE_DATE = LocalDate.parse("20210928",
			DateTimeFormatter.ofPattern("yyyyMMdd"));

	private ScheduledFuture<Void> cronJob;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		final var server = this.client.getGuildById(Snowflake.of(DISCORD_SERVER_ID)).block();
		if (server == null) {
			throw new BeanInitializationException("Could not get discord server by its id");
		}

		final var channel = server.getChannelById(Snowflake.of(DISCORD_CHANNEL_ID)).block();
		if (channel == null) {
			throw new BeanInitializationException("Could not get discord channel by its id");
		}

		this.cronJob = (ScheduledFuture<Void>) this.scheduler.schedule(
				() -> this.sendCountdown(channel.getRestChannel()), new CronTrigger("0 0 6 * * *", ZoneId.of("UTC")));

		this.scheduler.schedule(this::cancelCountDown, new Date(1632834000000L));

		Log.info("New World countdown scheduled");
	}

	public void sendCountdown(RestChannel channel) {
		if (channel == null) {
			return;
		}

		final var remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), NW_RELEASE_DATE) - 1;
		final var message = new StringBuilder("Good morning team!");

		if (remainingDays == 0) {
			message.append(" je popse!");
			channel.createMessage(message.toString()).block();
		} else {
			message.append(" " + remainingDays + " je popse!");
			channel.createMessage(message.toString()).block();
		}

	}

	public void cancelCountDown() {
		if (this.cronJob != null) {
			log.info("New world countdown cronjob cancelled");
			this.cronJob.cancel(true);
		}
	}

	@Override
	public void destroy() throws Exception {
		this.cancelCountDown();
	}
}

package com.stefanosdemetriou.discord.messaging;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.jline.utils.Log;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import discord4j.core.GatewayDiscordClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewWorldCountdown implements DisposableBean {

	private final GatewayDiscordClient client;
	private final TaskScheduler scheduler;

	private static final List<ServerChannelPair> channels = List.of(
			new ServerChannelPair(258369598833950723L, 582582345790521364L, "el"),
			new ServerChannelPair(880572111196925974L, 880572111968686102L, "en"));

	private List<ScheduledFuture<Void>> cronJobs = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		for (var pair : channels) {
			this.cronJobs.add((ScheduledFuture<Void>) this.scheduler.schedule(() -> this.sendCountdown(pair),
					new CronTrigger("0 0 6 * * *", ZoneId.of("UTC"))));
		}

		this.scheduler.schedule(this::cancelCountDown, new Date(1632834000000L));

		Log.info("New World countdown scheduled");
	}

	public void sendCountdown(ServerChannelPair pair) {
		if (pair == null) {
			return;
		}

		final var server = this.client.getGuildById(pair.getServerId()).block();
		if (server == null) {
			log.error("Could not get discord server with id {}", pair.getServerId());
			return;
		}

		final var channel = server.getChannelById(pair.getChannelId()).block();
		if (channel == null) {
			log.error("Could not get discord channel with id {} (server {})", pair.getChannelId(), pair.getServerId());
			return;
		}

		final var message = pair.getMessage();
		channel.getRestChannel().createMessage(message).block();

	}

	public void cancelCountDown() {
		for (var job : this.cronJobs) {
			job.cancel(true);
		}

		log.info("New world countdown cronjobs cancelled");
	}

	@Override
	public void destroy() throws Exception {
		this.cancelCountDown();
	}
}

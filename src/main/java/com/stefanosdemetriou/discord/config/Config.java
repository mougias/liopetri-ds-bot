package com.stefanosdemetriou.discord.config;

import java.util.List;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stefanosdemetriou.discord.events.discord.EventListener;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import lombok.AllArgsConstructor;

@Configuration
@ConfigurationProperties
@AllArgsConstructor
public class Config {

	private final DiscordConfig dsConfig;

	@Bean
	public <T extends Event> GatewayDiscordClient client(List<EventListener<T>> eventListeners) {
		var client = DiscordClient.create(this.dsConfig.getToken()).gateway().login().block();

		if (client == null) {
			throw new BeanInitializationException("Could not login to discord");
		}

		for (EventListener<T> listener : eventListeners) {
			client.on(listener.getEventType()).flatMap(listener::execute).onErrorResume(listener::handleError)
					.subscribe();
		}

		return client;
	}
}

package com.stefanosdemetriou.discord.web.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stefanosdemetriou.discord.radio.ServerManager;
import com.stefanosdemetriou.discord.web.dto.Server;

import discord4j.core.GatewayDiscordClient;
import lombok.AllArgsConstructor;

@RequestMapping("/discord")
@RestController
@AllArgsConstructor
public class DiscordController {

	private final GatewayDiscordClient client;
	private final ServerManager serverManager;

	@GetMapping("/servers")
	public List<Server> getServers() {
		var playing = serverManager.playing();

		return this.client.getGuilds().map(guild -> new Server(guild.getName(), playing.get(guild)))
				.collect(Collectors.toList()).block();
	}
}

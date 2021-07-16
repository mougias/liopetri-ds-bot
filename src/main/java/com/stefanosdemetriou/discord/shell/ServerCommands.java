package com.stefanosdemetriou.discord.shell;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.stefanosdemetriou.discord.helpers.ServersFormatter;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import lombok.AllArgsConstructor;

@ShellComponent
@AllArgsConstructor
public class ServerCommands {

	private final GatewayDiscordClient client;

	private List<Guild> guilds;

	@PostConstruct
	public void init() {
		this.reloadServersList();
	}

	/**
	 * Fetches connected servers from discord and caches them.
	 */
	public void reloadServersList() {
		this.guilds = this.client.getGuilds().collect(Collectors.toList()).block();
	}

	/**
	 * Shell command to list connected servers.
	 *
	 * @return connected servers list
	 */
	@ShellMethod(value = "List servers we are connected to", key = "servers list")
	public String listServers() {
		this.reloadServersList();

		return new ServersFormatter(this.guilds.iterator()).toString();
	}
}

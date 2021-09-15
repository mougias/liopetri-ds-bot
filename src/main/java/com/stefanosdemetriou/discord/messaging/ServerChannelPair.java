package com.stefanosdemetriou.discord.messaging;

import discord4j.common.util.Snowflake;
import lombok.Getter;

public class ServerChannelPair {

	@Getter
	private final Snowflake serverId;

	@Getter
	private final Snowflake channelId;

	public ServerChannelPair(long serverId, long channelId) {
		this.serverId = Snowflake.of(serverId);
		this.channelId = Snowflake.of(channelId);
	}
}

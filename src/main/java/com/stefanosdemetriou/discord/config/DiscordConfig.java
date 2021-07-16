package com.stefanosdemetriou.discord.config;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "discord")
@Data
@Validated
public class DiscordConfig {
	@NotBlank(message = "Discord API key is needed. Visit https://discord.com/developers/")
	private String token;
}

package com.stefanosdemetriou.discord.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Loads messages (1 per line) from messages.txt, and provides method
 * to get a random message.
 * 
 * @author stefanos
 *
 */
@Component
@Slf4j
public class MessageFactory {

	private final List<String> messages = new ArrayList<>();
	private final Random random = new Random();

	@PostConstruct
	public void init() throws IOException {
		this.reload();
	}

	/**
	 * Reloads messages.txt
	 *
	 * @return Number of loaded messages.
	 * @throws IOException
	 */
	public int reload() throws IOException {
		messages.clear();

		var is = new ClassPathResource("messages.txt").getInputStream();
		try (var br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = br.readLine()) != null) {
				this.messages.add(line);
			}
			log.info("Loaded " + this.messages.size() + " messages");
		}
		
		return this.messages.size();
	}

	/**
	 * Returns a random message from the list
	 * @return message
	 */
	public String random() {
		if (this.messages.isEmpty()) {
			return null;
		}

		return this.messages.get(this.random.nextInt(this.messages.size()));
	}
}

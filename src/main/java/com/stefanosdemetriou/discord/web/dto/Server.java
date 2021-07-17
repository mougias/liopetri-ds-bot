package com.stefanosdemetriou.discord.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Server {
	private String name;
	private String playing;
}

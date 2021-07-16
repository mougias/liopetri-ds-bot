package com.stefanosdemetriou.discord.radio;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Stations enum. Contains all the stations together with their source URL.
 * 
 * @author stefanos
 *
 */
@AllArgsConstructor
public enum Station {
	ACTIVE("https://securestreams3.autopo.st:1417/active"),
	ASTRA("https://securestreams2.autopo.st:1106/stream"),
	KISSFM("https://securestreams3.autopo.st:1417/89FM"),
	MIXFM("http://eco.onestreaming.com:8127/");
	
	@Getter
	private final String url;
}

package com.stefanosdemetriou.discord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(App.class, args);

		// when running without tty (e.g. in container) shell must be disabled
		// and this leaves no other thread to keep the app running
		Thread.currentThread().join();
	}

}

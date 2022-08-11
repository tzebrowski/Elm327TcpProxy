package org.obd.metrics.proxy;

import java.io.FileNotFoundException;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Runner {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		final Settings settings = SettingsLoader.settings();
		log.info("Starting service with the settings: {}", settings);
		new Server().launch(settings);
	}
}

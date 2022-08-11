package org.obd.metrics.proxy;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.obd.metrics.proxy.model.Settings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Launcher {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		final Settings settings = SettingsLoader.settings();
		log.info("Starting service with the settings: {}", settings);
		new Server().launch(settings);
	}
}

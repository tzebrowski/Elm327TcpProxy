package org.obd.metrics.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
final class SettingsLoader {

	private static final String SETTINGS_FILE_NAME = "config.yaml";

	static Settings settings() throws FileNotFoundException {
		final Yaml yaml = new Yaml(new Constructor(Settings.class));
		InputStream inputStream = null;
		File file = new File(SETTINGS_FILE_NAME);
		if (file.exists()) {
			inputStream = new FileInputStream(file);
		} else {
			log.warn("Could not find: '{}' in the local dir. Loading default configuration.", SETTINGS_FILE_NAME);
			inputStream = Settings.class.getResourceAsStream("/" + SETTINGS_FILE_NAME);
		}
		return yaml.load(inputStream);
	}
}

package org.obd.metrics.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

final class SettingsLoader {

	private static final String SETTINGS_FILE_NAME = "config.yaml";

	static Settings settings() throws FileNotFoundException {
		final Yaml yaml = new Yaml(new Constructor(Settings.class));
		final InputStream inputStream = new FileInputStream(SETTINGS_FILE_NAME);
		final Settings settings = yaml.load(inputStream);
		return settings;
	}
}

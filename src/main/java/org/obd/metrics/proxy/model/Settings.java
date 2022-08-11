package org.obd.metrics.proxy.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public final class Settings {

	private List<Overrides> overrides;
	private Host adapter;
	private Host server;
}

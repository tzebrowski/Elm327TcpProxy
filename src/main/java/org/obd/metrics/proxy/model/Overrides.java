package org.obd.metrics.proxy.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public final class Overrides {
	private String key;
	private String value;
}
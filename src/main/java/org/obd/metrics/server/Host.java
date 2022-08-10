package org.obd.metrics.server;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public final class Host {
	private int port;
	private String ip;
}
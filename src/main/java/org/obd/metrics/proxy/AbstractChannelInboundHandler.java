package org.obd.metrics.proxy;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.obd.metrics.proxy.model.Settings;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract class AbstractChannelInboundHandler extends ChannelInboundHandlerAdapter {
	private static final int TASK_DELAY = 6000;
	protected static final String END_CHARACTERS = "\r\r>";
	
	protected Settings settings;
	protected final Map<String, String> overrides = new HashMap<>();

	AbstractChannelInboundHandler(Settings settings) {
		this.settings = settings;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.error("Error:", cause);
		Channels.flushAndClose(ctx.channel());
	}
	
	protected void loadOverrides() {

		settings.getOverrides().stream().forEach(o -> {
			overrides.put(o.getKey() + "\r", o.getValue());
		});
	}

	protected void startBackgroundTask() {
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(TASK_DELAY);
					settings = SettingsLoader.settings();
					loadOverrides();
				} catch (InterruptedException | FileNotFoundException e) {
					log.error("Failed to load file", e);
				}
			}

		}).start();
	}
}

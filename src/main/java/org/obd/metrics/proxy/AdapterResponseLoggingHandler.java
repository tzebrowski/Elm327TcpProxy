package org.obd.metrics.proxy;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class AdapterResponseLoggingHandler extends ChannelDuplexHandler {

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		log.info(format("RX:", msg).replace("\r", "") + "\n");
		ctx.write(msg, promise);
	}

	protected String format(String eventName, Object msg) {
		String c = "";
		if (msg instanceof ByteBufHolder) {
			c = eventName + " " + ((ByteBufHolder) msg).content().toString(Charset.defaultCharset());
		} else if (msg instanceof ByteBuf) {
			c = eventName + " " + ((ByteBuf) msg).toString(Charset.defaultCharset());
		} else {
			c = eventName + " " + String.valueOf(msg);
		}
		return c;
	}
}

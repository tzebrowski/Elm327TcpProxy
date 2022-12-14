package org.obd.metrics.proxy;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.obd.metrics.proxy.model.Host;
import org.obd.metrics.proxy.model.Settings;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ServerHandler extends ChannelInboundHandlerAdapter {

	private final Settings settings;
	private volatile Channel adapterChannel;
	private Map<String, String> overrides = new HashMap<>();
	
	@Override
	public void channelActive(ChannelHandlerContext context) {
		loadOverrides();
		
		
		final Channel serverChannel = context.channel();

		final Bootstrap bootstrap = new Bootstrap();

		bootstrap.group(serverChannel.eventLoop())
				 .channel(context.channel().getClass())
				 .handler(new AdapterInitializer(serverChannel))
				 .option(ChannelOption.AUTO_READ, true);

		final Host adapterHost = settings.getAdapter();
		final ChannelFuture future = bootstrap.connect(adapterHost.getIp(), adapterHost.getPort());
		adapterChannel = future.channel();
		future.addListener(new WriteAndFlushListener(context));
	}



	@Override
	public void channelRead(final ChannelHandlerContext context, Object msg) {
		if (adapterChannel.isActive()) {

			if (msg instanceof ByteBuf) {
				final WriteAndFlushListener writeAndFlushListener = new WriteAndFlushListener(context);

				final String messageContent = ((ByteBuf) msg).toString(Charset.defaultCharset());
				log.info("TX: {}", messageContent);

				if (overrides.containsKey(messageContent)) {
					final String response = overrides.get(messageContent);
					final ByteBuf in = Unpooled.copiedBuffer((response + ">").getBytes());
					context.channel().writeAndFlush(in).addListener(writeAndFlushListener);
				} else {
					adapterChannel.writeAndFlush(msg).addListener(writeAndFlushListener);
				}

			}
		} else {
			log.error("Channel not active");
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		Channels.flushAndClose(adapterChannel);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.error("Server error failed", cause);
		Channels.flushAndClose(ctx.channel());
	}
	
	private void loadOverrides() {
		settings.getOverrides().stream().forEach(o -> {
			overrides.put(o.getKey() + "\r", o.getValue());
		});
	}
}

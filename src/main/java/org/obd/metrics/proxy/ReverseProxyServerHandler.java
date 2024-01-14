package org.obd.metrics.proxy;

import java.nio.charset.Charset;

import org.obd.metrics.proxy.model.Host;
import org.obd.metrics.proxy.model.Settings;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final class ReverseProxyServerHandler extends AbstractChannelInboundHandler {

	private volatile Channel adapterChannel;

	ReverseProxyServerHandler(Settings settings) {
		super(settings);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext context) {
		loadOverrides();
		startBackgroundTask();

		final Channel serverChannel = context.channel();

		final Bootstrap bootstrap = new Bootstrap();

		bootstrap.group(serverChannel.eventLoop()).channel(context.channel().getClass())
				.handler(new AdapterInitializer(serverChannel)).option(ChannelOption.AUTO_READ, true);

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
					final String override = overrides.get(messageContent);
					final ByteBuf in = Unpooled.copiedBuffer((override + END_CHARACTERS).getBytes());
					context.writeAndFlush(in).addListener(writeAndFlushListener);
				} else {
					if (messageContent.contains(">")) {
						context.writeAndFlush(msg).addListener(writeAndFlushListener);
					} else {
						final ByteBuf in = Unpooled.copiedBuffer((messageContent).getBytes());
						adapterChannel.writeAndFlush(in).addListener(writeAndFlushListener);
					}
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
}

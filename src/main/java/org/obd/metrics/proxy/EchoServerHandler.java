package org.obd.metrics.proxy;

import java.nio.charset.Charset;

import org.obd.metrics.proxy.model.Settings;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
final class EchoServerHandler extends AbstractChannelInboundHandler {

	EchoServerHandler(Settings settings) {
		super(settings);
	}

	@Override
	public void channelActive(ChannelHandlerContext context) {
		loadOverrides();
		startBackgroundTask();

		final Channel serverChannel = context.channel();

		final Bootstrap bootstrap = new Bootstrap();

		bootstrap.group(serverChannel.eventLoop()).channel(context.channel().getClass()).option(ChannelOption.AUTO_READ,
				true);
	}

	@Override
	public void channelRead(ChannelHandlerContext context, Object msg) {

		if (msg instanceof ByteBuf) {

			final String messageContent = ((ByteBuf) msg).toString(Charset.defaultCharset());
			

			log.info("TX: {}", messageContent);

			if (overrides.containsKey(messageContent)) {
				final String response = overrides.get(messageContent);
				final ByteBuf in = Unpooled.copiedBuffer((response + END_CHARACTERS).getBytes());
				context.channel().writeAndFlush(in);
				log.info("RX: {}\n", response);

			} else {

				final ByteBuf in = Unpooled.copiedBuffer((messageContent + END_CHARACTERS).getBytes());
				context.channel().writeAndFlush(in);
				log.info("RX: {}\n", messageContent);
			}
		}
	}

}

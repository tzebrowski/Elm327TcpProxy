package org.obd.metrics.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
final class Server {

	private final Settings settings;

	void launch() {
		log.info("Starting ReverseProxy server on the port: {} ", settings.getServer().getPort());

		final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(10);

		try {

			try {
				final ServerBootstrap serverBootstrap = new ServerBootstrap();
				final Channel channel = serverBootstrap
						.group(eventLoopGroup)
						.channel(NioServerSocketChannel.class)
						.childHandler(new ServerChannelInitializer(settings))
						.childOption(ChannelOption.AUTO_READ, false)
						.bind(settings.getServer().getPort())
						.sync()
						.channel();

				channel.closeFuture().sync();
				log.info("ReverseProxy server started");

			} catch (InterruptedException e) {
				log.error("Exception caught", e);
			}
		} finally {
			eventLoopGroup.shutdownGracefully();
		}
	}
}
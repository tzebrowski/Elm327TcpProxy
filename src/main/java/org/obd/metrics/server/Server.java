package org.obd.metrics.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final class Server {

	void launch(@NonNull final Settings settings) {
		log.info("Starting server on the port: {} ", settings.getServer().getPort());

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
				log.info("Server started.");

			} catch (InterruptedException e) {
				log.error("Exception caught", e);
			}
		} finally {
			eventLoopGroup.shutdownGracefully();
		}
	}
}
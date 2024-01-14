package org.obd.metrics.proxy;

import org.obd.metrics.proxy.model.ServerType;
import org.obd.metrics.proxy.model.Settings;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
final class Server {

	void launch(@NonNull final Settings settings) {
		log.info("Starting server on the port: {}. Type: {}", settings.getServer().getPort(), settings.getServerType());

		final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		final EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			final ServerBootstrap bootstrap = new ServerBootstrap();
			
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 100)
			.handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					final ChannelPipeline pipeline = ch.pipeline();

					pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
					if (settings.getServerType().equals(ServerType.ECHO)) {
						pipeline.addLast(new EchoServerHandler(settings));
					}else {
						pipeline.addLast(new ReverseProxyServerHandler(settings));
						pipeline.addLast(new AdapterResponseLoggingHandler());
					}
				}
			});

			final ChannelFuture channelFuture = bootstrap.bind(settings.getServer().getPort()).sync();
			channelFuture.channel().closeFuture().sync();

			log.info("Server started.");

		} catch (InterruptedException e) {
			log.error("Exception caught", e);
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
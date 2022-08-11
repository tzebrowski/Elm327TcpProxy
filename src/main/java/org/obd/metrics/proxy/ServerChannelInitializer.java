package org.obd.metrics.proxy;

import org.obd.metrics.proxy.model.Settings;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
	private final Settings adapterSettings;
	

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {

		final ChannelPipeline pipeLine = socketChannel.pipeline();
		pipeLine.addLast(new ServerHandler(adapterSettings));
		pipeLine.addLast(new AdapterResponseLoggingHandler());
	}
}

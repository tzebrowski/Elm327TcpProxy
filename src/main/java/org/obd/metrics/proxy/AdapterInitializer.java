package org.obd.metrics.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class AdapterInitializer extends ChannelInitializer<SocketChannel> {
	private final Channel serverChannel;

	@Override
	protected void initChannel(SocketChannel socketChannel) {
		socketChannel.pipeline().addLast(new AdapterHandler(serverChannel));
	}
}
package org.obd.metrics.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class WriteAndFlushListener implements ChannelFutureListener {
	private final ChannelHandlerContext ctx;

	@Override
	public void operationComplete(ChannelFuture channelFuture) throws Exception {
		if (channelFuture.isSuccess()) {
			ctx.channel().read();
		} else {
			ctx.channel().close();
		}
	}
}
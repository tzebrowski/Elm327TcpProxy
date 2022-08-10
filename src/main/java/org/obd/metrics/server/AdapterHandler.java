package org.obd.metrics.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class AdapterHandler extends ChannelInboundHandlerAdapter {

	private final Channel inboundChannel;

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.read();
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg) {
		inboundChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws InterruptedException {
				if (future.isSuccess()) {
					ctx.channel().read();
				} else {
					future.channel().close();
				}
			}
		});
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		ChannelUtils.closeOnFlush(inboundChannel);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ChannelUtils.closeOnFlush(ctx.channel());
	}
}
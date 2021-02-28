package server;

import common.event.GameEvent;
import event.MessageSentGameEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class DuelsServerHandler extends SimpleChannelInboundHandler<GameEvent> {

	private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	public DuelsServerHandler() {
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
//		incoming.writeAndFlush(new TileMapDataEvent(System.currentTimeMillis(), map));
		System.out.println(incoming.remoteAddress() + " connected.");
		channels.add(ctx.channel());
		System.out.println(channels.size() + " total connected clients.");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		System.out.println(incoming.remoteAddress() + " has left.");
		for (Channel channel : channels) {
			channel.writeAndFlush("[Server]: " + incoming.remoteAddress() + " has left.\n");
		}
		channels.remove(ctx.channel());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, GameEvent msg) throws Exception {
		Channel incoming = ctx.channel();
		System.out.println(incoming.remoteAddress() + " sent a message: '" + ((MessageSentGameEvent) msg).getMessage() + "'.");
		for (Channel channel : channels) {
			if (channel != incoming) {
				System.out.println("Forwarding message to " + channel.remoteAddress() + ".");
				channel.writeAndFlush("[" + incoming.remoteAddress() + "]: " + "\n");
			}
		}
	}

}

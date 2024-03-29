package serverserver.network;

import java.util.HashMap;
import java.util.Map;

import common.event.GameEvent;
import event.MessageSentGameEvent;
import event.servertoclient.ServerToClientGameStateEvent;
import event.servertoclient.ServerToClientIdCreationEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import server.game.DuelsServerSideGame;

public class DuelsServerHandler extends SimpleChannelInboundHandler<GameEvent> {

	private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	private Map<String, Long> channelIdToPlayerId = new HashMap<>();
	private DuelsServerSideGame game;

	public DuelsServerHandler(DuelsServerSideGame game) {
		this.game = game;
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		System.out.println(incoming.remoteAddress() + " join.");
		long id = game.addPlayer();
		channelIdToPlayerId.put(incoming.id().asLongText(), id);
		incoming.writeAndFlush(new ServerToClientIdCreationEvent(id));
		incoming.writeAndFlush(new ServerToClientGameStateEvent(game.getCurrentState(), game.getInputFrames()));
		channels.add(ctx.channel());
		System.out.println(channels.size() + " total connected players.");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		System.out.println(incoming.remoteAddress() + " has left.");
		long id = channelIdToPlayerId.remove(incoming.id().asLongText());
		game.removePlayer(id);
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

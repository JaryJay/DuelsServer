package serverserver.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import server.game.DuelsServerSideGame;

public class DuelsServerInitializer extends ChannelInitializer<SocketChannel> {

	private DuelsServerSideGame game;

	public DuelsServerInitializer(DuelsServerSideGame game) {
		this.game = game;
	}

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
		pipeline.addLast("encoder", new ObjectEncoder());
		pipeline.addLast("handler", new DuelsServerHandler(game));
	}

}

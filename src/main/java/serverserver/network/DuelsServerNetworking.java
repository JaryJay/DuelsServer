package serverserver.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import server.game.DuelsServerSideGame;

public class DuelsServerNetworking {

	private final int port;
	private DuelsServerSideGame game;

	public DuelsServerNetworking(DuelsServerSideGame game, int port) {
		this.game = game;
		this.port = port;
	}

	public void run() throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.childHandler(new DuelsServerInitializer(game));
			ChannelFuture future = bootstrap.bind(port).sync();
			System.out.println("Chat Server initialization complete");
			future.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}

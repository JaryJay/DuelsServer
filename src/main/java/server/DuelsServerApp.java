package server;

import server.game.DuelsServerSideGame;
import server.game.GameLogicTimer;
import serverserver.network.DuelsServerNetworking;

public class DuelsServerApp {

	public static void main(String[] args) throws InterruptedException {
		DuelsServerSideGame game = startGame();
		openNetwork(game);
	}

	private static DuelsServerSideGame startGame() {
		DuelsServerSideGame game = new DuelsServerSideGame();
		Runnable timer = new GameLogicTimer(game);
		Thread gameThread = new Thread(timer);
		gameThread.start();
		return game;
	}

	private static void openNetwork(DuelsServerSideGame game) throws InterruptedException {
		DuelsServerNetworking network = new DuelsServerNetworking(game, 8080);
		network.run();
	}

}

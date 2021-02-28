package server.game;

import java.util.List;

import input.InputFrame;
import misc.IdGenerator;
import misc.LimitedQueue;
import state.GameState;

public class DuelsServerSideGame {

	private List<GameState> states = new LimitedQueue<>(30);
	private List<InputFrame> inputFrames = new LimitedQueue<>(30);
	private GameState currentState;

	private IdGenerator playerIdGenerator = new IdGenerator();

	public void init() {
		currentState = new GameState(0);
		states.add(currentState);
	}

	public void update() {
		InputFrame inputFrame = new InputFrame();
		inputFrames.add(inputFrame);
		synchronized (currentState) {
			currentState = currentState.getNextState(inputFrame);
		}
		states.add(currentState);
	}

	public long addPlayer() {
		long id = playerIdGenerator.getId();
		synchronized (currentState) {
			currentState.addPlayer(id);
		}
		return id;
	}

	public void removePlayer(long id) {
		synchronized (currentState) {
			currentState.removePlayer(id);
		}
	}

	public GameState getCurrentState() {
		return currentState;
	}

	public List<InputFrame> getInputFrames() {
		return inputFrames;
	}

}
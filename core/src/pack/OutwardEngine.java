package pack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import maps.Room;
import timers.Timer;
import entities.*;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;

public class OutwardEngine extends ApplicationAdapter {

	public static final boolean debug = false;
	private static final Timer hitstunTimer = new Timer(0), dialogueTimer = new Timer(3), transitionTimer = new Timer(30);
	private static final List<Timer> timerList = new ArrayList<Timer>(Arrays.asList(hitstunTimer, dialogueTimer, transitionTimer));
	private static GameState gameState;
	private static int deltaTime = 0;
	private static Player player;
	private static boolean reset = false, change_room, paused;
	private static final boolean log_fps = false;
	private static InputHandler input_handler;
	private static Dialogue active_dialogue = null;
	private static Room active_room = null;
	private static final Vector2 active_position = new Vector2(0, 0);
	private static SaveFile save_file = new SaveFile();
	private static FPSLogger logger;

	@Override public void create () {
		player = new Player(0, 0);
		logger = new FPSLogger();
		input_handler = new ControllerHandler();
		if (!input_handler.begin()) input_handler = new KeyboardHandler();
		GraphicsHandler.begin();
		ThreeDimensionalGraphicsHandler.begin();
		MapHandler.begin();
		gameState = GameState.GAME;
	}

	@Override public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (!paused){
			deltaTime++;
			updateTimers();
			AudioHandler.update();
		}
		input_handler.update();
		if (log_fps) logger.log();

		switch(gameState){
		case GAME:		updateGame();		break;
		case DIALOGUE:	updateDialogue();	break;
		case DEBUG: 	updateDebug();		break;
		}	
	}

	private void updateGame(){
		if (reset) {
			reset = false;
			MapHandler.resetRoom();
		}
		if (change_room) change();
		if (!transitioning()){
			if (!inHitstun() && !paused) {
				MapHandler.update();
			}
			GraphicsHandler.updateGraphics();
		}
		GraphicsHandler.updateCamera();
	}

	private void updateDialogue(){
		active_dialogue.update();
		GraphicsHandler.updateGraphics();
		GraphicsHandler.drawDialogue();
		GraphicsHandler.updateCamera();
	}

	private void updateDebug(){
		DebugMenu.update();
	}

	private void updateTimers(){
		for (Timer t: timerList) t.countUp();
	}

	public static void causeHitlag(int length){
		hitstunTimer.reset(length);
	}

	public static void changeRoom(Destination des) {
		gameState = GameState.GAME;
		change_room = true;
		boolean same_room = false;
		if (null != active_room) {
			same_room = active_room.getClass() == des.room.getClass();
		}
		active_room = des.room;
		if (null != des.coordinates && !same_room) active_position.set(des.coordinates);
		if (null != des.dir && player.getDirection() != des.dir) player.flip();
	}
	
	public static void setSpawnPoint(Vector2 spawn) {
		active_position.set(spawn);
	}

	private static void change(){
		change_room = false;
		deltaTime = 0;
		MapHandler.updateRoomMap(active_room, active_position);
		player.stop();
		transitionTimer.reset();
		GraphicsHandler.updateRoomGraphics();
		MapHandler.update();
	}

	public static int getDeltaTime(){ 
		return deltaTime; 
	}

	static boolean inHitstun(){ 
		return !hitstunTimer.timeUp(); 
	}

	public static void reset() {
		reset = true;
	}

	public static void startDialogue(Dialogue d){
		if (gameState == GameState.DIALOGUE || !dialogueTimer.timeUp()){
			dialogueTimer.reset();
			if (!active_dialogue.advance()) gameState = GameState.GAME;
			return;
		}
		active_dialogue = d;
		active_dialogue.begin();
		gameState = GameState.DIALOGUE;
	}

	public static Player getPlayer(){
		return player;
	}

	static Dialogue getDialogue(){
		return active_dialogue;
	}

	public static InputHandler getInputHandler(){
		return input_handler;
	}

	public static SaveFile getSaveFile(){
		return save_file;
	}

	public static void handleJumpHeld(boolean held) {
		if (transitioning()) return;
		switch(gameState){
		case GAME: player.handleJumpHeld(held); break;
		case DIALOGUE: break;
		case DEBUG: break;
		}
	}

	public static void handleFireHeld(boolean held) {
		if (transitioning()) return;
		switch(gameState){
		case GAME: player.handleFireHeld(held); break;
		case DIALOGUE: break;
		case DEBUG: break;
		}
	}
	
	public static void handleAPress(){
		switch(gameState){
		case GAME: player.handlePress(Player.commandJump); break;
		case DIALOGUE: break;
		case DEBUG: DebugMenu.select(); break;
		}
	}
	
	public static void handleBPress(){
		switch(gameState){
		case GAME: player.handlePress(Player.commandFire); break;
		case DIALOGUE: advanceDialogue(); break;
		case DEBUG: break;
		}
	}
	
	private static void advanceDialogue(){
		if (!active_dialogue.advance()){
			gameState = GameState.GAME;
		}
		dialogueTimer.reset();
	}

	public static void handleStartPress(){
		paused = !paused;
	}
	public static boolean isPaused() {
		return paused;
	}

	public static void handleSelectPress() {
		gameState = GameState.DEBUG;
	}
	
	private static boolean transitioning(){
		return !transitionTimer.timeUp();
	}

	public enum GameState { 
		GAME, DIALOGUE, DEBUG
	}


}
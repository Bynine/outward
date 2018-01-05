package pack;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import entities.Destination;
import entities.Entity.Direction;
import maps.Room;
import timers.Timer;

public class DebugMenu {

	private static SpriteBatch batch = null;
	private static BitmapFont font = null;
	private static int list_pos = 0;
	private static boolean world_selected = false;
	private static final Timer cursor_timer = new Timer(5);
	
	private static ArrayList<String> worlds = new ArrayList<String>(Arrays.asList("WORLD1"));

	public static void initialize(){
		batch = new SpriteBatch();
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/nes.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.color = Color.BLACK;
		parameter.size = 16;
		parameter.spaceY = 2;
		font = generator.generateFont(parameter);
		generator.dispose();
	}

	public static void update(){
		if (!initialized()) {
			initialize();
		}
		cursor_timer.countUp();
		int cursor_move = (int) OutwardEngine.getInputHandler().getXInput();
		if (Math.abs(cursor_move) < 0.2) cursor_move = 0;
		moveCursor((int) OutwardEngine.getInputHandler().getXInput());
		
		Gdx.gl.glClearColor(0.8f, 0.5f, 0.2f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		final int pos_x = 200;
		final int pos_y = 500;
		final int dec 	= 50;
		batch.begin();
		font.draw(batch, "DEBUG", pos_x, pos_y + dec);
		font.draw(batch, "SELECT", pos_x, pos_y - dec);
		font.draw(batch, getActiveList().get(list_pos).toString(), pos_x - 50, pos_y - dec*2);
		batch.end();
	}

	public static void select(){
		if (world_selected){
			Room room = MapHandler.activeLevel.getRoom(list_pos);
			room.setup();
			OutwardEngine.changeRoom(new Destination(room, room.getStartPosition().x, room.getStartPosition().y, Direction.RIGHT));
		}
		else{
			MapHandler.changeActiveLevel();
			world_selected = true;
		}
		list_pos = 0;
	}

	public static void moveCursor(int i){
		if (!cursor_timer.timeUp()) return;
		list_pos += i;
		if (list_pos < 0) list_pos = getActiveList().size() - 2;
		if (list_pos >= getActiveList().size()) list_pos = 0;
		cursor_timer.reset();
	}

	private static ArrayList<String> getActiveList(){
		if (world_selected){
			return MapHandler.activeLevel.getRoomNames();
		}
		else{
			return worlds;
		}
	}

	private static boolean initialized(){
		return null != batch;
	}

}

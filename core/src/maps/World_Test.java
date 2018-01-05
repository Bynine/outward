package maps;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import entities.Checkpoint;
import entities.NPC;
import pack.Dialogue;
import pack.GlobalRepo;

public class World_Test extends World {

	public World_Test(){
		rooms.addAll(Arrays.asList(
				new Graveyard(this),
				new Riddle_Islands(this),
				new Level_Test(this),
				new Movement_Test(this)
				));
	}

	public class Riddle_Islands extends Room {

		public Riddle_Islands(World superLevel){
			super(superLevel);
			roomMusic = Gdx.audio.newMusic(Gdx.files.internal("music/none.mp3"));
			startPosition.x = 18;
			startPosition.y = 8;
			skyColor = new Color(0.35f, 0.18f, 0.45f, 1.0f);
			setup();
		}

		public void initEntities(){
			super.initEntities();

			entityList.add(new NPC(GlobalRepo.TILE * 20, GlobalRepo.TILE * 8, NPC.DUMMY, new Dialogue(Dialogue.voice_deep,
					"Red is good."
					)));
			entityList.add(new Checkpoint(GlobalRepo.TILE * 20, GlobalRepo.TILE * 8));
			entityList.add(new NPC(GlobalRepo.TILE * 66, GlobalRepo.TILE * 13, NPC.DUMMY, new Dialogue(Dialogue.voice_deep,
					"Blue is true."
					)));
			entityList.add(new Checkpoint(GlobalRepo.TILE * 66, GlobalRepo.TILE * 13));
			entityList.add(new NPC(GlobalRepo.TILE * 118, GlobalRepo.TILE * 10, NPC.DUMMY, new Dialogue(Dialogue.voice_deep,
					"Red's dead.",
					"Blue? Boohoo."
					)));
			entityList.add(new Checkpoint(GlobalRepo.TILE * 118, GlobalRepo.TILE * 10));
			entityList.add(new NPC(GlobalRepo.TILE * 189, GlobalRepo.TILE * 17, NPC.DUMMY, new Dialogue(Dialogue.voice_deep,
					"Try your prize"
					+ "\non for size."
					)));
			entityList.add(new Checkpoint(GlobalRepo.TILE * 189, GlobalRepo.TILE * 17));
		}

		public TiledMap getMap() {
			return tmxMapLoader.load("maps/riddle_islands.tmx");
		}
	}
	
	public class Graveyard extends Room {

		public Graveyard(World superLevel){
			super(superLevel);
			roomMusic = Gdx.audio.newMusic(Gdx.files.internal("music/none.mp3"));
			startPosition.x = 88;
			startPosition.y = 22;
			skyColor = new Color(0.15f, 0.25f, 0.3f, 1.0f);
			setup();
		}

		public void initEntities(){
			super.initEntities();
			entityList.add(new Checkpoint(GlobalRepo.TILE * 88, GlobalRepo.TILE * 22));
			entityList.add(new Checkpoint(GlobalRepo.TILE * 105, GlobalRepo.TILE * 23));
			entityList.add(new Checkpoint(GlobalRepo.TILE * 148, GlobalRepo.TILE * 2));
		}

		public TiledMap getMap() {
			return tmxMapLoader.load("maps/graveyard.tmx");
		}
	}


	public class Movement_Test extends Room {

		public Movement_Test(World superLevel){
			super(superLevel);
			roomMusic = Gdx.audio.newMusic(Gdx.files.internal("music/none.mp3"));
			startPosition.x = 45;
			startPosition.y = 5;
			wind.set(-0.00f, 0);
			skyColor = new Color(0.3f, 0.5f, 0.7f, 1.0f);
			setup();
		}

		public void initEntities(){
			super.initEntities();
		}

		public TiledMap getMap() {
			return tmxMapLoader.load("maps/test/movement_test.tmx");
		}
	}

	public class Level_Test extends Room {

		public Level_Test(World superLevel){
			super(superLevel);
			roomMusic = Gdx.audio.newMusic(Gdx.files.internal("music/none.mp3"));
			startPosition.x = 2;
			startPosition.y = 5;
			skyColor = new Color(0.6f, 0.5f, 0.4f, 1.0f);
			setup();
		}

		public void initEntities(){
			super.initEntities();
		}

		public TiledMap getMap() {
			return tmxMapLoader.load("maps/test/level_test.tmx");
		}
	}
}

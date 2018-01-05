package pack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import maps.*;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import effects.Graphic;
import entities.Destination;
import entities.Entity;
import entities.Entity.Direction;
import entities.Player;

public class MapHandler {

	static Room activeRoom;
	static TiledMap activeMap;
	static World activeLevel; 
	public static int mapWidth;
	public static int mapHeight; 
	private static final List<Vector2> chunks = new ArrayList<Vector2>();
	private static final int num_chunks = 10;
	private static int chunk_width = 0;

	static void begin(){
		activeLevel = new World_Test();
		activeRoom = activeLevel.getRoom(activeLevel.getStartRoom());
		updateRoomMap(activeRoom, activeRoom.getStartPosition());
		activeMap = activeRoom.getMap();
		OutwardEngine.changeRoom(new Destination(activeRoom, activeRoom.getStartPosition().x, activeRoom.getStartPosition().y, Direction.RIGHT));
		AudioHandler.changeMusic(activeRoom.getMusic());
	}

	static void update(){
		activeRoom.update();
		updateEntities();
	}

	private static void updateEntities(){
		int chunk_index = -1;
		Player player = OutwardEngine.getPlayer();
		for (Vector2 chunk: chunks){
			if (inChunk(player, chunk)){
				chunk_index = chunks.indexOf(chunk);
			}
		}
		Iterator<Entity> entityIter = activeRoom.getEntityList().iterator();
		while (entityIter.hasNext()) {
			Entity en = entityIter.next();
			boolean inChunkProximity = inChunks(en, getSurroundingChunks(chunk_index, 1));
			boolean shouldUpdate = (!OutwardEngine.inHitstun() || en instanceof Graphic && inChunkProximity );
			if (shouldUpdate) { 
				en.update(activeRoom.getRectangleList(), activeRoom.getEntityList());
			}
			if ( en.isOOB(mapWidth, mapHeight) || en.toRemove() ) {
				removeEntity(entityIter, en);
			}
			if (!inChunkProximity){
				if (inChunks(en, getSurroundingChunks(chunk_index - 4, 1))) en.getPosition().x += chunk_width * 4;
				else if (inChunks(en, getSurroundingChunks(chunk_index + 4, 1))) en.getPosition().x -= chunk_width * 4;
			}
		}
		if (player.getCenter().x > chunks.get(num_chunks-1).x){
			player.getPosition().x -= mapWidth;
			GraphicsHandler.snapCamera(-mapWidth);
		}
		if (player.getCenter().x < chunks.get(1).x){
			player.getPosition().x += mapWidth;
			GraphicsHandler.snapCamera(mapWidth);
		}
	}
	
	private static List<Vector2> getSurroundingChunks(int start, int distance){
		//System.out.println("Start: " + start + " distance: " + distance);
		List<Vector2> surroundingChunks = new ArrayList<Vector2>();
		for (int i = start - distance; i <= start + distance; ++i){
			if ((i >= 0) && (i < num_chunks)){
				surroundingChunks.add(chunks.get(i));
			}
		}
		//System.out.println(surroundingChunks.toString());
		return surroundingChunks;
	}

	private static boolean inChunks(Entity en, List<Vector2> sub_chunks){
		for (Vector2 chunk: sub_chunks){
			if (inChunk(en, chunk)) return true;
		}
		return false;
	}
	
	private static boolean inChunk(Entity en, Vector2 chunk){
		return (chunk.x < en.getCenter().x && chunk.y > en.getCenter().x);
	}

	private static void removeEntity(Iterator<Entity> entityIter, Entity en){
		if (OutwardEngine.getPlayer() == en){
			resetRoom();
		}
		en.dispose();
		entityIter.remove();
	}

	public static void updateRoomMap(Room room, Vector2 position) {
		activeRoom = room;
		activeMap = activeRoom.getMap();
		activeRoom.initEntities();
		activeRoom.setup();
		mapWidth  = activeMap.getProperties().get("width",  Integer.class) * GlobalRepo.TILE;
		mapHeight = activeMap.getProperties().get("height", Integer.class) * GlobalRepo.TILE;
		OutwardEngine.getPlayer().setPosition(position);
		GraphicsHandler.setCamera(position);
		AudioHandler.changeMusic(activeRoom.getMusic());
		chunks.clear();
		chunk_width = mapWidth/4;
		final int chunk_start = -3;
		for (int i = 0; i < num_chunks; ++i){
			int left_corner = (chunk_width) * (i + chunk_start);
			chunks.add(new Vector2(left_corner, left_corner+chunk_width));
		}
	}

	public static void resetRoom() {
		OutwardEngine.getPlayer().reset();
		OutwardEngine.changeRoom(new Destination(activeRoom, null, null));
	}

	public static void addEntity(Entity e){
		activeRoom.addEntity(e);
	}

	public static void changeActiveLevel() {
		// TODO: implement
	}

	public static List<Rectangle> getClimbableRectangles() {
		return activeRoom.getClimbableList();
	}

	public static Room getActiveRoom() {
		return activeRoom;
	}

}

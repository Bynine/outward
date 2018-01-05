package maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import entities.Entity;
import pack.GlobalRepo;
import pack.MapHandler;
import pack.OutwardEngine;

public abstract class Room {
	protected MapObjects mapCollision, mapEntities, mapClimbable;
	protected final TmxMapLoader tmxMapLoader = new TmxMapLoader();
	protected final List<Rectangle> rectangleList = new ArrayList<>();
	protected final List<Rectangle> climbableList = new ArrayList<>();
	protected final List<Entity> entityList = new ArrayList<>();
	protected final List<Entity> newEntityList = new ArrayList<>();
	protected final Vector2 startPosition = new Vector2();
	protected final Vector2 wind = new Vector2();
	protected Music roomMusic;
	protected float r, g, b = 1;
	protected float a = 0;
	protected World superLevel;
	protected Texture 
	atmosphere = new Texture(Gdx.files.internal("sprites/weather/clouds.png")),
	weather = new Texture(Gdx.files.internal("sprites/weather/fog.png")); 
	protected boolean lit = true, fixedHeight = false;
	protected float cameraHeight = GlobalRepo.TILE * 4.0f;
	protected Color skyColor = new Color(0, 0, 0, 0);
	
	public Room(World superLevel){
		clearOut();
		this.superLevel = superLevel;
	}
	
	public void setup(){
		MapLayers layers = getMap().getLayers();
		mapCollision = layers.get(layers.getCount() - 3).getObjects(); // gets the collision layer
		for(RectangleMapObject solidObject: mapCollision.getByType(RectangleMapObject.class)){		
			Rectangle r = solidObject.getRectangle();
			Rectangle right_rect = new Rectangle(r.x + MapHandler.mapWidth, r.y, r.width, r.height);
			Rectangle left_rect = new Rectangle(r.x - MapHandler.mapWidth, r.y, r.width, r.height);
			rectangleList.addAll(Arrays.asList(r, right_rect, left_rect));
		}
		mapEntities = layers.get(layers.getCount() - 2).getObjects(); // loads entities from map
		mapClimbable = layers.get(layers.getCount() - 1).getObjects(); // gets the climbable layer
		for(RectangleMapObject climbable: mapClimbable.getByType(RectangleMapObject.class)){		
			climbableList.add(climbable.getRectangle());
		}
	}

	public void initEntities(){
		clearOut();
		OutwardEngine.getPlayer().setPosition(startPosition);
		entityList.add(OutwardEngine.getPlayer());
		
		for (MapObject m: mapEntities) entityList.add(new EntityLoader().loadEntity(m));
	}

	public void addEntity(Entity e){
		newEntityList.add(e);
	}
	
	public void update(){
		for (Entity e: newEntityList) entityList.add(e);
		newEntityList.clear();
	}
	
	void clearOut(){
		rectangleList.clear();
		for (Entity en: entityList){
			en.dispose();
		}
		entityList.clear();
	}
	
	void setStartPosition(float x, float y){
		startPosition.x = x;
		startPosition.y = y;
	}
	
	public void removeEntity(Entity en){
		getEntityList().remove(en);
		getRectangleList().remove(en.getImage().getBoundingRectangle());
	}
	
	public Color getSkyColor() {
		return skyColor;
	}
	
	public abstract TiledMap getMap();
	public List<Rectangle> getRectangleList(){ return rectangleList; }
	public List<Rectangle> getClimbableList(){ return climbableList; }
	public List<Entity> getEntityList(){ return entityList; }
	public Vector2 getStartPosition(){ return startPosition; }
	public Vector2 getWind(){ return wind; }
	public void stopMusic(){ roomMusic.stop(); }
	public Music getMusic(){ return roomMusic; }
	public float getR(){ return r; }
	public float getB(){ return b; }
	public float getG(){ return g; }
	public float getA(){ return a; }
	public Texture getAtmosphere() { return atmosphere; }
	public Texture getFront() { return weather; }
	public float getCameraHeight() { return cameraHeight; }
	public boolean isFixedHeight() { return fixedHeight; }

}

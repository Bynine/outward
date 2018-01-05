package entities;

import com.badlogic.gdx.math.Vector2;

import entities.Entity.Direction;
import maps.Room;

public class Destination {
	
	public final Room room;
	public final Vector2 coordinates;
	public final Direction dir;

	public Destination(Room room, float desX, float desY, Direction dir){
		this.room = room;
		this.dir = dir;
		coordinates = new Vector2(desX, desY);
	}
	
	public Destination(Room room, Vector2 des, Direction dir){
		this.room = room;
		this.dir = dir;
		if (des == null) coordinates = null;
		else coordinates = new Vector2(des.x, des.y);
	}
	
}

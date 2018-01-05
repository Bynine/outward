package maps;

import java.util.ArrayList;
import java.util.List;

public abstract class World {
	final List<Room> rooms = new ArrayList<>();
	protected int startRoom = 0;
	
	public Room getRoom(int i){
		return rooms.get(i);
	}
	
	public int numberRooms(){
		return rooms.size();
	}

	public int getStartRoom() {
		return startRoom;
	}
	
	SkyColor getSkyColor(){
		return SkyColor.DAY;
	}
	
	enum SkyColor{
		DAY, NIGHT
	}

	public ArrayList<String> getRoomNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (Room room: rooms){
			names.add(room.getClass().getName());
		}
		return names;
	}
	
}

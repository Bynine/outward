package maps;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;

import entities.*;

public class EntityLoader {
	
	Entity loadEntity(MapObject m){
		MapProperties mp = m.getProperties();
		float x = mp.get("x", Float.class);
		float y = mp.get("y", Float.class);
		
		switch(m.getName()){
		case "path": return new Collectible.Path(x, y);
		case "secret": return new Collectible.Secret(x, y);
		case "bump": return new Bumper(x, y);
		case "lift": return new Lifter(x, y);
		
		default: return new Dummy(x, y, m.getName());
		}
	}
}

package entities;

import com.badlogic.gdx.math.Vector2;

import pack.GlobalRepo;
import pack.OutwardEngine;

public class Checkpoint extends Entity {
	
	private final Vector2 spawn;

	public Checkpoint(float posX, float posY) {
		super(posX, posY);
		this.spawn = new Vector2(posX/GlobalRepo.TILE, posY/GlobalRepo.TILE);
		createImage("sprites/entities/misc/checkpoint.png");
	}
	
	@Override
	protected void updatePosition(){ /* doesn't move */ }
	
	@Override
	void handleTouchHelper(Entity e){
		if (isTouching(e, 0) && e == OutwardEngine.getPlayer()){
			OutwardEngine.setSpawnPoint(spawn);
		}
	}

	@Override
	public void dispose() {
		image.getTexture().dispose();
	}

}

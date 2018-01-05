package entities;

import com.badlogic.gdx.math.Rectangle;

import pack.OutwardEngine;

public class PlayerTrigger extends Entity {
	
	private boolean triggered = false, stopped = false;
	private final int width, height;

	public PlayerTrigger(float posX, float posY, int width, int height) {
		super(posX, posY);
		hasShadow = false;
		createImage("sprites/entities/misc/blank.png");
		
		this.width = width;
		this.height = height;
	}
	
	public boolean triggered(){
		return triggered && !stopped;
	}
	
	@Override
	public Rectangle getCollisionBox(float x, float y){
		Rectangle r = image.getBoundingRectangle();
		r.setX(x); 
		r.setY(y);
		r.setWidth(width);
		r.setHeight(height);
		return r;
	}
	
	@Override
	void handleTouchHelper(Entity e){
		if (isTouching(e, 0) && e == OutwardEngine.getPlayer()){
			triggered = true;
		}
	}

	@Override
	public void dispose() {
		image.getTexture().dispose();
	}

	public void stop() {
		stopped = true;
		
	}

}

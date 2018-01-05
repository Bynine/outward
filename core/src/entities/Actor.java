package entities;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import effects.*;
import pack.MapHandler;
import pack.OutwardEngine;
import timers.Timer;

public abstract class Actor extends Entity {

	protected float weight = 1;
	protected Timer moveTimer = new Timer(0), stunTimer = new Timer(0);
	protected float prevDirect = 0;

	public Actor(float posX, float posY) {
		super(posX, posY);
		timerList.addAll(Arrays.asList(moveTimer, stunTimer));
		hasShadow = true;
	}

	@Override
	public void update(List<Rectangle> rectangleList, List<Entity> entityList){
		super.update(rectangleList, entityList);
		if (stunned() && OutwardEngine.getDeltaTime() % 8 == 0){
			MapHandler.addEntity(new Puff(this));
		}
	}

	@Override
	protected void changeImage(){
		TextureRegion prevImage = image;
		changeImageHelper();
		if (!prevImage.equals(image)) adjustImage(prevImage);
		if (doesCollide(position.x, position.y)) setImage(prevImage);
	}

	protected void changeImageHelper(){
		/**/
	}

	protected void adjustImage(TextureRegion prevImage) {
		float adjustedPosX = (image.getWidth() - prevImage.getRegionWidth())/2;
		if (!doesCollide(position.x - adjustedPosX, position.y)) position.x -= adjustedPosX;
	}

	@Override
	void handleTouchHelper(Entity e){
		super.handleTouchHelper(e);
	}

	@Override
	protected void hitGround(){
		stunTimer.end();
	}

	@Override
	protected void hitWall(){
		super.hitWall();
		if (stunned()) velocity.x *= -0.25;
		if (state == State.DIVE){
			knock(-2.4f * direct(), 0, 30);
		}
	}

	@Override
	void handleFriction(){
		if (stunned() && !isGrounded()){
			velocity.x *= Math.pow(airFrictionX, 0.1);
		}
		else super.handleFriction();
	}
	
	public void knock(float x, float y, int stun) {
		velocity.set(x, y);
		state = State.FALL;
		stunTimer.reset(stun);
	}

	protected boolean canMove(){
		return !stunned() && state != State.CROUCH;
	}

	protected boolean canTurn(){
		return (state == State.CLIMB || isGrounded()) && state != State.LAND && state != State.CROUCH && state != State.DIVE;
	}

	protected boolean stunned(){
		return !stunTimer.timeUp();
	}

	public boolean flashing(){
		return stunTimer.getCounter() < 2;
	}

}

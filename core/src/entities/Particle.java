package entities;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import timers.DurationTimer;

public class Particle extends Entity{

	protected TextureRegion tex = new TextureRegion(new Texture(Gdx.files.internal("sprites/entities/misc/particle.png")));
	protected final DurationTimer dur = new DurationTimer(80);

	public Particle(float posX, float posY, float direct) {
		super(posX, posY);
		image = new Sprite(tex);
		timerList.add(dur);
		gravity = -0.3f;
		airFrictionX = 0.98f;
		hasShadow = false;
	}
	
//	private void setTrajectory(float direct){
//		float angle = hit.ANG;
//		float speedMod = (float) ((3.2 - Math.random())/2.0);
//		float speed = hit.BKB * speedMod;
//		float deviation = 20 * speed;
//		angle += (deviation * Math.random()) - deviation/2.0;
//		velocity.set(speed, speed);
//		velocity.setAngle(angle);
//		velocity.x *= direct;
//	}

	@Override
	public void update(List<Rectangle> rectangleList, List<Entity> entityList){
		super.update(rectangleList, entityList);
		if (dur.timeUp()) setRemove();
	}
	
	@Override
	protected void hitSurface(){
		setRemove();
	}

	@Override
	public void dispose() {
		tex.getTexture().dispose();
	}

}

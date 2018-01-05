package effects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

import entities.Entity;
import pack.GlobalRepo;
import timers.DurationTimer;

public abstract class Graphic extends Entity{
	
	protected Animation<TextureRegion> anim = GlobalRepo.makeAnimation("sprites/graphics/puff.png", 2, 1, 8, PlayMode.NORMAL);
	protected final DurationTimer dur = new DurationTimer(16);
	
	public Graphic(float posX, float posY){
		super(posX, posY);
		timerList.add(dur);
		image = new Sprite(anim.getKeyFrame(0));
		collision = Collision.GHOST;
		hasShadow = false;
	}
	
	@Override
	protected void updatePosition(){
		setImage(anim.getKeyFrame(dur.getCounter()));
		if (dur.timeUp()) setRemove();
	}
	
	@Override
	public void dispose() {
		GlobalRepo.freeAnimation(anim);
	}
	
}

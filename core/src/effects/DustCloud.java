package effects;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

import entities.Actor;
import pack.GlobalRepo;

public class DustCloud extends Graphic{
	
	private float speed = -1.5f;

	public DustCloud(Actor actor) {
		super(actor.getCenter().x, actor.getPosition().y);
		position.x += actor.direct() * 8;
		anim = GlobalRepo.makeAnimation("sprites/graphics/dustcloud.png", 1, 1, 1, PlayMode.NORMAL);
		dur.setEndTime(12);
		if (actor.getDirection() == Direction.RIGHT) {
			flip();
			speed *= -1;
		}
		else{
			position.x -= actor.getCollisionBox(actor.getPosition().x, actor.getPosition().y).getWidth();
		}
		updatePosition();
	}
	
	@Override
	protected void updatePosition(){
		super.updatePosition();
		position.x += 0.5 * speed;
	}
	
}
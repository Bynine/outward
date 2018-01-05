package entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;

import pack.GlobalRepo;

public class Lifter extends Entity {
	
	protected static Animation<TextureRegion> move = GlobalRepo.makeAnimation("sprites/entities/enemies/bumper/move.png", 2, 1, 15, PlayMode.LOOP);

	public Lifter(float posX, float posY) {
		super(posX, posY);
		image = new Sprite(new TextureRegion(move.getKeyFrame(0)));
	}
	
	@Override
	void handleTouchHelper(Entity e){
		if (e.isTouching(this, 4) && e instanceof Actor){
			e.velocity.y = 13;
		}
	}

	@Override
	public void dispose() {
		GlobalRepo.freeAnimation(move);
	}

}

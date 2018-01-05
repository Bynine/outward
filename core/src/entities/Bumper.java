package entities;

import java.util.Arrays;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;

import pack.GlobalRepo;
import timers.Timer;

public class Bumper extends Enemy{

	protected static Animation<TextureRegion> move = GlobalRepo.makeAnimation("sprites/entities/enemies/bumper/move.png", 2, 1, 15, PlayMode.LOOP);
	protected static Animation<TextureRegion> bonk = GlobalRepo.makeAnimation("sprites/entities/enemies/bumper/bonk.png", 1, 1, 5, PlayMode.NORMAL);
	private final Timer stopTimer = new Timer(20);

	public Bumper(float posX, float posY) {
		super(posX, posY);
		image = new Sprite(new TextureRegion(move.getKeyFrame(0)));
		timerList.addAll(Arrays.asList(stopTimer));
	}

	@Override
	protected void changeImageHelper(){
		if (!stopTimer.timeUp()) setImage(bonk.getKeyFrame(stopTimer.getCounter()));
		else setImage(move.getKeyFrame(stopTimer.getCounter()));
	}

	@Override
	void handleTouchHelper(Entity e){
		if (e.isTouching(this, 4) && stopTimer.timeUp() && e instanceof Actor){
			Actor act = (Actor) e;
			if ((act.getPosition().y - position.y) > 12){
				act.getVelocity().y = 5.4f;
				act.state = State.FALL;
			}
			else{
				float dir = Math.signum(act.getCenter().x - getCenter().x);
				float mod = 1;
				if ((direct() * (act.getCenter().x - getCenter().x) ) < 0) mod = 0.5f;
				act.knock(dir * mod * 2.4f, 2.4f, 30);
			}
			stopTimer.reset();
		}
	}

	@Override
	protected void aiRoutine() {
		if (!stopTimer.timeUp()) return;
		velocity.x += 0.2 * direct();
		if (doesCollide(position.x, position.y - 1) && !doesCollide(position.x + (24 * direct()), position.y - 1)) flip();
	}

	@Override
	public void dispose() {
		/*static*/
	}

}

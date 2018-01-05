package entities;

import java.util.List;

import com.badlogic.gdx.math.Rectangle;

public abstract class Enemy extends Actor {
	
	public Enemy(float posX, float posY) {
		super(posX, posY);
	}
	
	public void update(List<Rectangle> rectangleList, List<Entity> entityList){
		aiRoutine();
		super.update(rectangleList, entityList);
	}
	
	protected abstract void aiRoutine();

}

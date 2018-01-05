package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Chest extends Actor {

	//private final Class<? extends Entity> reward;
	private boolean is_open = false;
	private final TextureRegion
	closed = new TextureRegion(new Texture(Gdx.files.internal("sprites/entities/objects/chest_closed.png"))),
	open = new TextureRegion(new Texture(Gdx.files.internal("sprites/entities/objects/chest_open.png")));

	public Chest(float posX, float posY, Class<? extends Entity> reward) {
		super(posX, posY);
		image = new Sprite(closed);
		//this.reward = reward;
	}
	
	@Override
	protected void changeImageHelper() {
		if (is_open) setImage(open);
		else setImage(closed); 
	}

	@Override
	public void dispose() {
		closed.getTexture().dispose();
		open.getTexture().dispose();
	}

}

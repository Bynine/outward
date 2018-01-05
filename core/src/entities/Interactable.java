package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class Interactable extends Entity {
	
	protected TextureRegion talk_icon = new TextureRegion(new Texture(Gdx.files.internal("sprites/graphics/talk.png")));
	protected TextureRegion open_icon = new TextureRegion(new Texture(Gdx.files.internal("sprites/graphics/open.png")));

	public Interactable(float posX, float posY) {
		super(posX, posY);
		hasShadow = false;
		layer = Layer.GROUND;
	}
	
	public abstract void interact();
	
	public Rectangle getInteractionBox(float x, float y){
		return getCollisionBox(x, y);
	}

	public TextureRegion getIcon() {
		return talk_icon;
	}

}

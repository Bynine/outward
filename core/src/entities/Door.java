package entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import pack.Dialogue;
import pack.OutwardEngine;

public class Door extends Interactable {
	
	protected final Destination destination;

	public Door(float posX, float posY, Destination des) {
		super(posX, posY);
		createImage("sprites/entities/misc/nothing.png");
		destination = des;
		layer = Layer.GROUND;
	}
	
	@Override
	public TextureRegion getIcon() {
		return open_icon;
	}

	@Override
	public void interact() {
		OutwardEngine.changeRoom(destination);
	}
	
	@Override
	protected void updatePosition(){ /* doesn't move */ }

	@Override
	public void dispose() {
		image.getTexture().dispose();
	}
	
	public static class MansionDoor extends Door{
		
		private Dialogue d = new Dialogue(Dialogue.voice_text,
				"It's locked.");
		
		public MansionDoor(float posX, float posY, Destination des) {
			super(posX, posY, des);
			createImage("sprites/entities/misc/door.png");
		}
		
		@Override
		public void interact() {
			if (OutwardEngine.getSaveFile().flag_mansion_key.is_on()) OutwardEngine.changeRoom(destination);
			else OutwardEngine.startDialogue(d);
		}
		
		@Override
		public TextureRegion getIcon() {
			if (OutwardEngine.getSaveFile().flag_mansion_key.is_on()) return open_icon;
			else return talk_icon;
		}
		
	}

}

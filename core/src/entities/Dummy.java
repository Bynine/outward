package entities;

import pack.Dialogue;
import pack.OutwardEngine;

public class Dummy extends Interactable {
	
	private Dialogue d = new Dialogue(Dialogue.voice_mid,
			"I will never be anything.");

	public Dummy(float posX, float posY) {
		super(posX, posY);
		createImage("sprites/entities/npcs/dummy.png");
		hasShadow = false;
	}
	
	@Override
	protected void updatePosition(){ /* doesn't move */ }
	
	public Dummy(float posX, float posY, String string) {
		super(posX, posY);
		createImage("sprites/entities/npcs/dummy.png");
		d = new Dialogue(Dialogue.voice_mid, "Someday I will be...\n" + string + "! How exciting!");
	}

	@Override
	public void interact() {
		OutwardEngine.startDialogue(d);
	}

	@Override
	public void dispose() {
		image.getTexture().dispose();
	}

}

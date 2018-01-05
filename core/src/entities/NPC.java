package entities;

import java.util.ArrayList;
import java.util.List;

import pack.Dialogue;
import pack.OutwardEngine;
import pack.SaveFile;
import pack.SaveFile.Flag;

public class NPC extends Interactable {

	public static final String DUMMY = "sprites/entities/npcs/dummy.png";
	public static final String bookshelf_url = "sprites/entities/npcs/bookshelf.png";
	public static final String goat_url = "sprites/entities/npcs/goat.png";
	public static final String barricade_url = "sprites/entities/misc/door_barricade.png";
	public static final String none_url = "sprites/entities/misc/nothing.png";

	private Dialogue dialogue;
	private List<FlagTalk> flagtalks = new ArrayList<FlagTalk>();

	/**
	 * NPC who says different things based on flags. Null flag is default and needs to be last.
	 */
	public NPC(float posX, float posY, String url, FlagTalk ... fts) {
		super(posX, posY);
		createImage(url);
		for (FlagTalk ft: fts) this.flagtalks.add(ft);
	}

	public NPC(float posX, float posY, String url, Dialogue dialogue){
		super(posX, posY);
		createImage(url);
		this.dialogue = dialogue;
	}

	@Override
	public void interact() {
		OutwardEngine.startDialogue(getDialogue());
	}

	private Dialogue getDialogue(){
		if (flagtalks.isEmpty()){
			return dialogue;
		}
		else{
			for (FlagTalk ft: flagtalks){
				if (ft.isOn()) return ft.getDialogue();
			}
			return new Dialogue(Dialogue.voice_mid, "NPC couldn't find"
					+ "\n default dialogue");
		}
	}

	@Override
	public void dispose() {
		image.getTexture().dispose();
	}

	@Override
	protected void updatePosition(){ /* doesn't move */ }

	public static class FlagTalk{
		private final Dialogue dialogue;
		private final Flag flag;
		public FlagTalk(SaveFile.Flag flag, Dialogue dialogue){
			this.dialogue = dialogue;
			this.flag = flag;
		}
		boolean isOn(){
			if (null == flag) return true;
			return flag.is_on();
		}
		Dialogue getDialogue(){
			return dialogue;
		}
	}

}

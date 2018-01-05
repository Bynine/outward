package effects;

import entities.Actor;

public class Puff extends Graphic {

	public Puff(Actor actor) {
		super(actor.getCenter().x, actor.getCenter().y);
		
	}

}

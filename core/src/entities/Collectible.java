package entities;

import pack.OutwardEngine;
import pack.SaveFile.Flag;
import timers.DurationTimer;
import timers.Timer;

public abstract class Collectible extends Entity {

	protected Timer noPickupTimer = new DurationTimer(15);

	public Collectible(float posX, float posY) {
		super(posX, posY);
		timerList.add(noPickupTimer);
		gravity = -0.2f;
		airFrictionX = 0.991f;
	}

	@Override
	void handleTouchHelper(Entity e){
		if (isTouching(e, 0) && OutwardEngine.getPlayer() == e && noPickupTimer.timeUp()){
			collect((Player) e);
		}
	}

	protected abstract void collect(Player player);

	public static class Path extends Collectible {

		/**
		 * Floating collectibles that unlock new levels
		 */
		public Path(float posX, float posY) {
			super(posX, posY);
			createImage("sprites/entities/misc/coin.png");
			gravity = 0.0f;
		}

		@Override
		protected void collect(Player player) {
			setRemove();
			//TODO: play sound
		}

		@Override
		public void dispose() {
			image.getTexture().dispose();
		}
	}
	
	public static class Secret extends Collectible {

		/**
		 * Hidden collectibles that unlock something special
		 */
		public Secret(float posX, float posY) {
			super(posX, posY);
			createImage("sprites/entities/misc/secret.png");
			gravity = 0.0f;
		}

		@Override
		protected void collect(Player player) {
			setRemove();
			//TODO: play sound
		}

		@Override
		public void dispose() {
			image.getTexture().dispose();
		}
	}

	public static class Item extends Collectible {

		private final Flag flag;

		public Item(float posX, float posY, String url, Flag flag) {
			super(posX, posY);
			createImage(url);
			this.flag = flag;
		}

		@Override
		protected void collect(Player player) {
			setRemove();
			flag.activate();
		}

		@Override
		public void dispose() {
			image.getTexture().dispose();
		}
	}

}

package entities;

public class Portal extends Entity {
	
	private final Destination destination;

	public Portal(float x, float y, Destination des){
		super(x, y);
		destination = des;
		createImage("sprites/entities/misc/unknown.png");
		updateImage();
		layer = Layer.GROUND;
		hasShadow = false;
	}

	@Override
	protected void updatePosition(){ /* doesn't move */ }
	
	public Destination getDestination(){ 
		return destination; 
	}
	
	@Override
	public void dispose() {
		image.getTexture().dispose();
	}
	
	public static class Hole extends Portal{

		public Hole(float x, float y, Destination des) {
			super(x, y, des);
			createImage("sprites/entities/misc/hole.png");
			updateImage();
		}
		
	}

}
